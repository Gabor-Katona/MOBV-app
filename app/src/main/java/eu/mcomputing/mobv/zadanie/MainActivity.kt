package eu.mcomputing.mobv.zadanie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("MojTAG", "Správa");

        /*val myButton: Button = findViewById(R.id.button1)
        myButton.setOnClickListener {
            // Kód, ktorý sa vykoná po kliknutí na tlačidlo
            Log.d("MojTAG", "button1");
        }*/
    }

}