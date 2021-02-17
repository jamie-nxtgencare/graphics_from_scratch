class Color(val red: Int, val green: Int, val blue: Int) {
    constructor(colorVector: Vector) : this(colorVector.x.toInt(), colorVector.y.toInt(), colorVector.z.toInt())
    fun toVector(): Vector {
        return Vector(red.toDouble(), green.toDouble(), blue.toDouble())
    }
}