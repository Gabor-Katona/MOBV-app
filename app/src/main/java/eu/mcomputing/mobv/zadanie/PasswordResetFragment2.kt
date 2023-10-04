package eu.mcomputing.mobv.zadanie

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController

class PasswordResetFragment2 : Fragment(R.layout.fragment_password_reset2) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.reset_passwd_button).apply {
            setOnClickListener {
                it.findNavController().navigate(R.id.action_to_loginFragment)
            }
        }
    }
}