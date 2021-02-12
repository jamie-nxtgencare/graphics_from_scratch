import kotlin.math.sqrt

class Scene(
    private val viewPortSize: Int,
    private val projectionPlaneZ: Int,
    val cameraPosition: Vector,
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

    fun computeLighting(p: Vector, normal: Vector): Double {
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

                    val shadow = closestIntersection(p, l, 0.001, if (light.type == LightType.POINT) 1 else Int.MAX_VALUE)
                    if (shadow.obj != null) {
                        continue
                    }

                    light.intensity * normal.dotProduct(l) / (normal.length() * l.length())
                }
            }
        }

        return i
    }

    fun closestIntersection(origin: Vector, direction: Vector, minT: Double, maxT: Int): ObjectIntersection {
        var intersection = Double.MAX_VALUE
        var closestObject: Sphere? = null

        for (obj in objects) {
            val ts = intersectObj(origin, direction, obj)
            if (ts.front < intersection && minT < ts.front && ts.front < maxT) {
                intersection = ts.front
                closestObject = obj
            }
            if (ts.back < intersection && minT < ts.back && ts.back < maxT) {
                intersection = ts.back
                closestObject = obj
            }
        }

        return ObjectIntersection(closestObject, intersection)
    }

    fun traceRay(origin: Vector, direction: Vector, minT: Double, maxT: Int): Color {
        val objectIntersection = closestIntersection(origin, direction, minT, maxT)
        val closestObject = objectIntersection.obj
        val intersection = objectIntersection.intersection

        if (closestObject == null) {
            return backgroundColor
        }

        val offsetIntersection = origin.add(direction.multiply(intersection))
        var normal = offsetIntersection.subtract(closestObject.vector)
        normal = normal.multiply(1.0/normal.length())

        val colorVector = closestObject.colorVector().multiply(computeLighting(offsetIntersection, normal))
        return Color(colorVector.x.toInt(), colorVector.y.toInt(), colorVector.z.toInt())
    }

    private fun intersectObj(origin: Vector, direction: Vector, obj: Sphere): Face {
        val oc = origin.subtract(obj.vector)
        val k1 = direction.dotProduct(direction)
        val k2 = 2 * oc.dotProduct(direction)
        val k3 = oc.dotProduct(oc) - obj.radius * obj.radius;

        val discriminant = k2 * k2 - 4 * k1 * k3;
        if (discriminant < 0) {
            return Face(Double.MAX_VALUE, Double.MAX_VALUE);
        }

        val t1 = (-k2 + sqrt(discriminant)) / (2*k1);
        val t2 = (-k2 - sqrt(discriminant)) / (2*k1);

        return Face(t1, t2);
    }

    fun update() {
        canvas.update()
    }

}

class ObjectIntersection(val obj: Sphere?, val intersection: Double)
