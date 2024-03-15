package campalans.m8.animacionsavanades

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.RadialGradient
import android.graphics.Shader
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.graphics.drawable.shapes.Shape
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator

class MyAnimationView(context: Context?) : View(context) {
    //Declarem la llista on guardarem totes les balls generades
    val balls: ArrayList<ShapeHolder> = ArrayList()

    //Conjunt d'animacions
    var animation: AnimatorSet? = null

    init {
        // Animate background color
        // Note that setting the background color will automatically invalidate the
        // view, so that the animated color, and the bouncing balls, get redisplayed on
        // every frame of the animation.

        //Canviem la propietat del backgroundColor de this (el view) entre els valors Red i Blue
        val colorAnim: ValueAnimator = ObjectAnimator.ofInt(this, "backgroundColor", RED, BLUE)
        colorAnim.duration = 3000   //Duració de l'animació
        colorAnim.setEvaluator(ArgbEvaluator())
        colorAnim.repeatCount = ValueAnimator.INFINITE  //Repetició infinita
        colorAnim.repeatMode = ValueAnimator.REVERSE    //L'animació un cop acabada (pasats els 3 segons), retorna però a la inversa RED a BLUE i BLUE a RED
        colorAnim.start()   //Comença l'animació
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        //Controlem que l'usuari estigui o polsant la pantalla o desplaçant-se per ella
        if (event.action != MotionEvent.ACTION_DOWN &&
            event.action != MotionEvent.ACTION_MOVE
        ) {
            return false
        }

        //Afegim una nova bola en la posició on s'ha registrat l'event
        val newBall : ShapeHolder = addBall(event.x, event.y)
        //Animació de rebot amb estirament i compressió de la bola
        //Calculem la posició inicial i final de l'animació
        val startY: Float = newBall.getY()
        //La altura de la pantalla menys l'alçada de la bola
        val endY = height - 50f
        val h = height.toFloat()
        val eventY = event.y
        //Calculem el punt mitg entre el punt inicial del clic i el final de la pantalla
        val halfDistance = (endY - startY) / 2
        //Restem
        val halfwayY = endY - halfDistance

        //La duració de l'animació segons el punt on es genera la bola
        //h és la altura del view
        //eventY és el punt y on s'ha clicat la pantalla
        val duration = (500 * ((h - eventY) / h)).toInt()
        //Animació de rebot
        //Es veu modificat el valor y de la bola (newBall) des de startY a endY
        val bounceAnim: ValueAnimator = ObjectAnimator.ofFloat(newBall, "y", startY, endY)
        bounceAnim.duration = duration.toLong()
        bounceAnim.interpolator = AccelerateInterpolator()

        //Corregeix el centre en les x perquè es mantingui centrada la bola
        val squashAnim1: ValueAnimator = ObjectAnimator.ofFloat(
            newBall, "x", newBall.getX(),
            newBall.getX() - 25f
        )
        squashAnim1.duration = (duration / 4).toLong()
        squashAnim1.repeatCount = 1     //Es repeteix dues vegades
        squashAnim1.repeatMode = ValueAnimator.REVERSE  //Després de xafar-se torna a comprimir-se
        squashAnim1.interpolator = DecelerateInterpolator()

        //Animació de compressió (aplastar)
        val squashAnim2: ValueAnimator = ObjectAnimator.ofFloat(
            newBall,
            "width",
            newBall.getWidth(),
            newBall.getWidth() + 50
        )
        squashAnim2.duration = (duration / 4).toLong()
        squashAnim2.repeatCount = 1
        squashAnim2.repeatMode = ValueAnimator.REVERSE
        squashAnim2.interpolator = DecelerateInterpolator()

        //Corregeix la y perquè es mantingui l'animació centrada a la bola
        val stretchAnim1: ValueAnimator = ObjectAnimator.ofFloat(
            newBall, "y", endY,
            endY + 25f
        )
        stretchAnim1.duration = (duration / 4).toLong()
        stretchAnim1.repeatCount = 1
        stretchAnim1.interpolator = DecelerateInterpolator()
        stretchAnim1.repeatMode = ValueAnimator.REVERSE

        //Animació de compressió (allargar)
        val stretchAnim2: ValueAnimator = ObjectAnimator.ofFloat(
            newBall,
            "height",
            newBall.getHeight(),
            newBall.getHeight() - 25
        )
        stretchAnim2.duration = (duration / 4).toLong()
        stretchAnim2.repeatCount = 1
        stretchAnim2.interpolator = DecelerateInterpolator()
        stretchAnim2.repeatMode = ValueAnimator.REVERSE

        //Animació cap a la posició original
        val bounceBackAnim: ValueAnimator = ObjectAnimator.ofFloat(
            newBall, "y", endY,
            halfwayY
        )
        bounceBackAnim.duration = duration.toLong()
        bounceBackAnim.interpolator = DecelerateInterpolator()

        //L'animator set del rebot on s'ordena l'ordre de les animacions
        // Sequence the down/squash&stretch/up animations
        val bouncer = AnimatorSet()
        bouncer.play(bounceAnim).before(squashAnim1)    //Botem la pilota des del punt d'inici al final de la pantalla
        bouncer.play(squashAnim1).with(squashAnim2)     //Aplast
        bouncer.play(squashAnim1).with(stretchAnim1)
        bouncer.play(squashAnim1).with(stretchAnim2)
        bouncer.play(bounceBackAnim).after(stretchAnim2)

        //Un cop acaba l'animació desapareix la pilota poc a poc
        // Fading animation - remove the ball when the animation is done
        val fadeAnim: ValueAnimator = ObjectAnimator.ofFloat(newBall, "alpha", 1f, 0f)
        fadeAnim.duration = 250
        fadeAnim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                //Eliminem la pilota del llistat
                balls.remove((animation as ObjectAnimator).target)
            }
        })

        // Sequence the two animations to play one after the other
        val animatorSet = AnimatorSet()
        animatorSet.play(bouncer).before(fadeAnim)
        // Start the animation
        animatorSet.start()
        return true
    }

    //Funció que s'encarrega d'afegir les boles
    private fun addBall(x: Float, y: Float): ShapeHolder {
        //Declarem un objecte oval
        val circle = OvalShape()

        //Li assignem una mida a l'oval
        circle.resize(50f, 50f)

        //Forma pintable / dibuixable
        val drawable = ShapeDrawable(circle)

        //Definim l'objecte ShapeHolder mitjançant el drawable definit prèviament que també inclou l'oval
        val shapeHolder = ShapeHolder(drawable)
        //Restem la meitat de la mida que mesura l'oval perquè quedi centrat en centre de l'oval
        shapeHolder.setX(x - 25f)
        shapeHolder.setY(y - 25f)

        //Declarem un valor aleatori de red, verd i blau
        val red = (Math.random() * 255).toInt()
        val green = (Math.random() * 255).toInt()
        val blue = (Math.random() * 255).toInt()

        //Segons els colors aleatoris definim el color de la bola
        val color = -0x1000000 or (red shl 16) or (green shl 8) or blue

        //A l'element paint, li assignem l'element gradient que creem
        val paint = drawable.paint //new Paint(Paint.ANTI_ALIAS_FLAG);
        val darkColor = -0x1000000 or (red / 4 shl 16) or (green / 4 shl 8) or blue / 4
        val gradient = RadialGradient(
            37.5f, 12.5f,
            50f, color, darkColor, Shader.TileMode.CLAMP
        )
        paint.shader = gradient

        //Assignem a la bola que hem creat l'objecte paint
        shapeHolder.setPaint(paint)
        //Afegim la nova bola a la llista
        balls.add(shapeHolder)
        return shapeHolder
    }

    override fun onDraw(canvas: Canvas) {
        Log.d("OnDraw", "OnDraw")
        for (i in balls.indices) {
            val shapeHolder = balls[i] as ShapeHolder
            canvas.save()
            canvas.translate(shapeHolder.getX(), shapeHolder.getY())
            shapeHolder.getShape()?.draw(canvas)
            canvas.restore()
        }
    }

    companion object {
        private const val RED = -0x7f80
        private const val BLUE = -0x7f7f01
        private const val CYAN = -0x7f0001
        private const val GREEN = -0x7f0080
    }
}