package campalans.m8.animacionsavanades

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val container = findViewById<View>(R.id.container) as LinearLayout
        //Afegim al layout prèviament definit una vista en temps d'execució
        container.addView(MyAnimationView(this))
    }
}