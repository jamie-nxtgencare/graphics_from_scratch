import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement

class Canvas(val canvas: HTMLCanvasElement) {
    private val context = canvas.getContext("2d") as CanvasRenderingContext2D
    private val buffer = context.getImageData(0.0, 0.0, canvas.width.toDouble(), canvas.height.toDouble())
    private val pitch = buffer.width * 4
    private val image : dynamic = buffer.data

    fun putPixel(topLeftX: Double, topLeftY: Double, color: Color) {
        val x = canvas.height / 2 + topLeftX
        val y = canvas.width / 2 - topLeftY - 1

        if (x < 0 || x >= canvas.width || y < 0 || y >= canvas.height) {
            return
        }

        var offset: Int = (4 * x + pitch * y).toInt()

        image[offset++] = color.red
        image[offset++] = color.green
        image[offset++] = color.blue
        image[offset] = 255
    }

    fun update() {
        context.putImageData(buffer, 0.0, 0.0)
    }

    fun getWidth(): Int {
        return canvas.width
    }

    fun getHeight(): Int {
        return canvas.height
    }
}

