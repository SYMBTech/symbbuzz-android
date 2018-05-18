package symbbuzz.com.symbbuzz

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var className = "symbbuzz.com.symbbuzz.SecondActivity"
        val classType = Class.forName(className)
        go.setOnClickListener {
            startActivity(Intent(applicationContext, classType))
        }
    }
}
