package campalans.m8.animacionsavanades

import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.drawable.ShapeDrawable

/**

 * A data structure that holds a Shape and various properties that can be used to define

 * how the shape is drawn.

*/

class ShapeHolder(shape: ShapeDrawable) {
    //Coordenades de la bola
    private var x = 0f
    private var y = 0f

    //La forma
    private var shape: ShapeDrawable? = shape

    private var color = 0

    private var gradient: RadialGradient? = null

    //Transparència
    private var alpha = 1f

    private var paint: Paint? = null

    fun setPaint(value: Paint?) {
        paint = value
    }

    fun getPaint(): Paint? {
        return paint
    }

    fun setX(value: Float) {
        x = value
    }

    fun getX(): Float {
        return x
    }

    fun setY(value: Float) {
        y = value
    }

    fun getY(): Float {
        return y
    }

    fun setShape(value: ShapeDrawable?) {
        shape = value
    }

    fun getShape(): ShapeDrawable? {
        return shape
    }

    fun getColor(): Int {
        return color
    }

    fun setColor(value: Int) {
        shape!!.paint.color = value
        color = value
    }

    fun setGradient(value: RadialGradient?) {
        gradient = value
    }

    fun getGradient(): RadialGradient? {
        return gradient
    }

    fun setAlpha(alpha: Float) {
        this.alpha = alpha
        shape!!.alpha = (alpha * 255f + .5f).toInt()
    }

    //Ens retorna l'espai que ocupa la forma horitzontalment
    fun getWidth(): Float {
        return shape!!.shape.width
    }

    fun setWidth(width: Float) {
        val s = shape!!.shape
        s.resize(width, s.height)
    }

    //Ens retorna l'espai que ocupa la forma vertical
    fun getHeight(): Float {
        return shape!!.shape.height
    }

    fun setHeight(height: Float) {
        val s = shape!!.shape
        s.resize(s.width, height)
    }

    fun ShapeHolder(s: ShapeDrawable?) {
        shape = s
    }
}