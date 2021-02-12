import kotlinx.browser.document
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLElement
import kotlin.KotlinVersion.Companion.CURRENT
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@ExperimentalTime
fun main() {
    init()
    println("Initialized")
    val scene = Scene(1, 1, Vector(0.0, 0.0, 0.0), Color(255, 255, 255), Canvas(document.getElementById("graphics") as HTMLCanvasElement))
    println("Scene created")

    scene.addObjects(
        listOf(
            Sphere(Vector(0.0, -1.0, 3.0), 1, Color(255, 0, 0)),
            Sphere(Vector(2.0, 0.0, 4.0), 1, Color(0, 0, 255)),
            Sphere(Vector(-2.0, 0.0, 4.0), 1, Color(0, 255, 0)),
            Sphere(Vector(0.0, -5001.0, 0.0), 5000, Color(255, 255, 0))
        )
    )

    scene.addLights(
        listOf(
            Light(LightType.AMBIENT, 0.2),
            Light(LightType.POINT, 0.6, Vector(2.0, 1.0, 0.0), Vector(0.0, 0.0)),
            Light(LightType.DIRECTIONAL, 0.2, Vector(0.0, 0.0), Vector(1.0, 4.0, 4.0))
        )
    )

    println("Scene populated")

    val measureTime = measureTime {
        for (x in -scene.canvas.getWidth() / 2..scene.canvas.getWidth() / 2) {
            for (y in -scene.canvas.getHeight() / 2..scene.canvas.getHeight() / 2) {
                val direction = scene.toViewport(Vector(x.toDouble(), y.toDouble()))
                val color = scene.traceRay(scene.cameraPosition, direction, 1.0, Int.MAX_VALUE);
                scene.canvas.putPixel(x.toDouble(), y.toDouble(), color);
            }
        }
    }

    println(measureTime)

    val measureTime2 = measureTime {
        println("Updating canvas")
        scene.update();
        println("Updated canvas")
    }

    println(measureTime2)

}

fun init() {
    val main = document.getElementById("main") as HTMLElement
    main.textContent = greeting()
    val kotlinVersion = document.getElementById("kotlin-version") as HTMLElement
    kotlinVersion.textContent = CURRENT.toString()
}

fun greeting() = "Raytracing in Kotlin transpiled to JavaScript in Electron"

