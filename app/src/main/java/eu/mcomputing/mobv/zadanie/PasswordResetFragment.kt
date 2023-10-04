package eu.mcomputing.mobv.zadanie

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController

class PasswordResetFragment : Fragment(R.layout.fragment_password_reset) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.send_email_button).apply {
            setOnClickListener {
                it.findNavController().navigate(R.id.action_to_passwordResetFragment2)
            }
        }
    }
}