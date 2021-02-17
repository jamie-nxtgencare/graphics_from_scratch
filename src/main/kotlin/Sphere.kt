class Sphere(val vector: Vector, val radius: Int, val color: Color, val specular: Double, val reflective: Double) {
    val radiusSquare = radius * radius
    fun colorVector(): Vector {
        return Vector(color.red.toDouble(), color.green.toDouble(), color.blue.toDouble())
    }
}