package eu.mcomputing.mobv.zadanie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText

class InputActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_signup)

        val submitButton: Button = findViewById(R.id.submit_button)
        submitButton.setOnClickListener {
            /*val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)*/

            // Logika po kliknutí na tlačidlo, napríklad na získanie textu z EditText
            val input1: String = findViewById<EditText>(R.id.edit_text_email).text.toString()
            val input2: String = findViewById<EditText>(R.id.edit_text_password).text.toString()

            // Spracovanie dát alebo iné akcie
            Log.d("MojTAG", input1)
        }
    }
}
