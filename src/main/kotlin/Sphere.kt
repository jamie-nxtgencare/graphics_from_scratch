class Sphere(val vector: Vector, val radius: Int, val color: Color) {
    fun colorVector(): Vector {
        return Vector(color.red.toDouble(), color.green.toDouble(), color.blue.toDouble())
    }
}