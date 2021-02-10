import kotlin.math.sqrt

open class Vector(val x: Double, val y: Double, val z: Double) {
    constructor(x: Double, y: Double) : this(x, y, 0.0)

    fun add(vector: Vector): Vector {
        return Vector(x + vector.x, y + vector.y, z + vector.z)
    }

    fun subtract(vector: Vector): Vector {
        return Vector(x - vector.x, y - vector.y, z - vector.z)
    }

    fun multiply(k: Double): Vector {
        return Vector(x * k, y * k, z * k)
    }

    fun dotProduct(direction: Vector): Double {
        return x * direction.x + y * direction.y + z * direction.z
    }

    fun length(): Double {
        return sqrt(dotProduct(this))
    }
}
