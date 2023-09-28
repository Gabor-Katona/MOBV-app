package eu.mcomputing.mobv.zadanie

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.Navigation
import androidx.navigation.findNavController

class CustomConstraintLayout(context: Context, attrs: AttributeSet? = null) : ConstraintLayout(context, attrs) {
    init {
        val layout = LayoutInflater.from(context).inflate(R.layout.custom_layout, this, true)

        //LayoutInflater.from(context).inflate(R.layout.fragment_signup, this, true)
        // kod na spracovania kliknuti na ikony

        /*val mapButton: ImageView = findViewById(R.id.ic_action_map)
        mapButton.setOnClickListener {
            //Toast.makeText(context,"toast message with gravity",Toast.LENGTH_SHORT).show()

            findNavController().navigate(R.id.secondFragment1)
        }*/

        layout.findViewById<ImageView>(R.id.ic_action_map).setOnClickListener {
            it.findNavController().navigate(R.id.action_first_to_second)
        }

        layout.findViewById<ImageView>(R.id.ic_action_list).setOnClickListener {
            it.findNavController().navigate(R.id.action_first_to_feed)
        }

        layout.findViewById<ImageView>(R.id.ic_action_account).setOnClickListener {
            it.findNavController().navigate(R.id.action_first_to_profil)
        }
    }

    // pridanie metod, ktore dokazu menit stav widgetu
    // .. napriklad zvyraznenie, aktivnej ikony
}