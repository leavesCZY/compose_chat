package github.leavesc.compose_chat.ui.theme

import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Shapes
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

val AppShapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(12.dp)
)

val BottomSheetShape = RoundedCornerShape(
    topStart = CornerSize(16.dp),
    topEnd = CornerSize(16.dp),
    bottomEnd = CornerSize(0.dp),
    bottomStart = CornerSize(0.dp)
)

val DiagonalShape: Shape = object : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path()
        path.lineTo(size.width, 0f)
        path.lineTo(size.width, size.height / 1.5f)
        path.lineTo(0f, size.height)
        path.close()
        return Outline.Generic(path = path)
    }

    override fun toString(): String = "DiagonalShape"

}

class BezierShape(private val animateValue: Float) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val width = size.width
        val height = size.height
        val progress = height / 7 * 5 + height / 7 * 2 * animateValue
        val path = Path()
        path.lineTo(0f, progress / 7 * 5)
        path.quadraticBezierTo(width / 2 + width / 4 * animateValue, height, width, progress)
        path.lineTo(width, 0f)
        path.lineTo(0f, 0f)
        return Outline.Generic(path = path)
    }

    override fun toString(): String = "BezierShape"

}