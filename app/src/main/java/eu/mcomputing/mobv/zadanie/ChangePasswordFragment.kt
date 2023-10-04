package eu.mcomputing.mobv.zadanie

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class ChangePasswordFragment : Fragment(R.layout.fragment_change_password) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.reset_passwd_button).apply {
            setOnClickListener {

                findNavController().navigate(R.id.action_to_profile)
            }
        }

    }
}