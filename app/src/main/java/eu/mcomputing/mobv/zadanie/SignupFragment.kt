package eu.mcomputing.mobv.zadanie

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class SignupFragment : Fragment(R.layout.fragment_signup) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.submit_button).apply {
            setOnClickListener {

                findNavController().navigate(R.id.action_signup_feed)
            }
        }
    }
}