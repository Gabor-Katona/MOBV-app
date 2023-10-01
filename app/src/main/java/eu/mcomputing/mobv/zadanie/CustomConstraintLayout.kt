package eu.mcomputing.mobv.zadanie

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController

class CustomConstraintLayout(context: Context, attrs: AttributeSet? = null) : ConstraintLayout(context, attrs) {
    init {
        val layout = LayoutInflater.from(context).inflate(R.layout.custom_layout, this, true)

        layout.findViewById<ImageView>(R.id.map).setOnClickListener {
            it.findNavController().navigate(R.id.action_to_map)
        }

        layout.findViewById<ImageView>(R.id.feed).setOnClickListener {
            it.findNavController().navigate(R.id.action_to_feed)
        }

        layout.findViewById<ImageView>(R.id.profile).setOnClickListener {
            it.findNavController().navigate(R.id.action_to_profile)
        }
    }

    // pridanie metod, ktore dokazu menit stav widgetu
    // .. napriklad zvyraznenie, aktivnej ikony
}