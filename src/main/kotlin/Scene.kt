import kotlin.math.pow
import kotlin.math.sqrt

class Scene(
    private val viewPortSize: Int,
    private val projectionPlaneZ: Int,
    val cameraPosition: Vector,
    val cameraRotation: Matrix,
    private val backgroundColor: Color,
    val canvas: Canvas
) {
    private val objects = ArrayList<Sphere>()
    private val lights = ArrayList<Light>()

    fun addObjects(objects: List<Sphere>) {
        this.objects.addAll(objects)
    }

    fun addLights(lights: List<Light>) {
        this.lights.addAll(lights)
    }

    fun toViewport(vector: Vector): Vector {
        return Vector(
            vector.x * viewPortSize / canvas.getWidth(),
            vector.y * viewPortSize / canvas.getHeight(),
            projectionPlaneZ.toDouble()
        )
    }

    private fun computeLighting(p: Vector, normal: Vector, view: Vector, specular: Double): Double {
        var i = 0.0
        for (light : Light in lights) {
            i += when (light.type) {
                LightType.AMBIENT -> light.intensity
                else -> {
                    val l = when (light.type) {
                        LightType.POINT -> light.position.subtract(p)
                        LightType.DIRECTIONAL -> light.direction
                        else -> Vector(0.0, 0.0, 0.0)
                    }

                    val shadow = closestIntersection(p, l, 0.001, if (light.type == LightType.POINT) 1 else Int.MAX_VALUE, true)
                    if (shadow.obj != null) {
                        continue
                    }

                    var intensity = light.intensity * normal.dotProduct(l) / (normal.length() * l.length())

                    if (specular != -1.0) {
                        val r = reflectRay(l, normal)
                        val rDotV = r.dotProduct(view)
                        if (rDotV > 0) {
                            intensity += light.intensity * (rDotV/(r.length() * view.length())).pow(specular)
                        }
                    }

                    intensity
                }
            }
        }

        return i
    }

    private fun closestIntersection(origin: Vector, direction: Vector, minT: Double, maxT: Int, anyIntersect: Boolean = false): ObjectIntersection {
        var intersection = Double.MAX_VALUE
        var closestObject: Sphere? = null

        val dDotD = direction.dotProduct(direction)

        for (obj in objects) {
            val ts = intersectObj(origin, dDotD, direction, obj)
            if (ts.front < intersection && minT < ts.front && ts.front < maxT) {
                intersection = ts.front
                closestObject = obj
            }
            if (ts.back < intersection && minT < ts.back && ts.back < maxT) {
                intersection = ts.back
                closestObject = obj
            }

            if (closestObject != null && anyIntersect) {
                return ObjectIntersection(closestObject, intersection)
            }
        }

        return ObjectIntersection(closestObject, intersection)
    }

    private fun reflectRay(ray: Vector, normal: Vector): Vector {
        return normal.multiply(2.0).multiply(normal.dotProduct(ray)).subtract(ray)
    }

    fun traceRay(origin: Vector, direction: Vector, minT: Double, maxT: Int, recursionDepth: Int = 3): Vector {
        val objectIntersection = closestIntersection(origin, direction, minT, maxT)
        val closestObject = objectIntersection.obj
        val intersection = objectIntersection.intersection

        if (closestObject == null) {
            return backgroundColor.toVector()
        }

        val offsetIntersection = origin.add(direction.multiply(intersection))
        var normal = offsetIntersection.subtract(closestObject.vector)
        normal = normal.multiply(1.0/normal.length())

        val colorVector = closestObject
            .colorVector()
            .multiply(computeLighting(offsetIntersection, normal, direction.multiply(-1.0), closestObject.specular))

        val reflective = closestObject.reflective
        if (recursionDepth <= 0 || reflective <= 0) {
            return colorVector
        }

        val ray = reflectRay(direction.multiply(-1.0), normal)
        val reflectedColor = traceRay(offsetIntersection, ray, 0.001, Int.MAX_VALUE, recursionDepth -1)
        return colorVector.multiply(1 - reflective).add(reflectedColor.multiply(reflective))
    }

    private fun intersectObj(origin: Vector, dDotD: Double, direction: Vector, obj: Sphere): Face {
        val oc = origin.subtract(obj.vector)
        val k2 = 2 * oc.dotProduct(direction)
        val k3 = oc.dotProduct(oc) - obj.radiusSquare;

        val discriminant = k2 * k2 - 4 * dDotD * k3;
        if (discriminant < 0) {
            return Face(Double.MAX_VALUE, Double.MAX_VALUE);
        }

        val t1 = (-k2 + sqrt(discriminant)) / (2*dDotD);
        val t2 = (-k2 - sqrt(discriminant)) / (2*dDotD);

        return Face(t1, t2);
    }

    fun update() {
        canvas.update()
    }

}

class ObjectIntersection(val obj: Sphere?, val intersection: Double)
