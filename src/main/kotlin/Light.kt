class Light(val type: LightType, val intensity: Double, val position: Vector, val direction: Vector) {
    constructor(type: LightType, intensity: Double) : this(type, intensity, Vector(0.0,0.0), Vector(0.0,0.0))
}