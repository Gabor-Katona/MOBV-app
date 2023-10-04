package eu.mcomputing.mobv.zadanie

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class LoginFragment : Fragment(R.layout.fragment_login) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.submit_button).apply {
            setOnClickListener {
                val username: String = view.findViewById<TextInputEditText>(R.id.edit_text_username).text.toString()
                val password: String = view.findViewById<TextInputEditText>(R.id.edit_text_password).text.toString()

                Log.d("TAG", username)
                Log.d("TAG", password)
                login(username, password, view)
            }
        }


        view.findViewById<Button>(R.id.reset_passwd_button).apply {
            setOnClickListener {
                it.findNavController().navigate(R.id.action_to_passwordResetFragment)
            }
        }

    }

    private fun login(username: String, password: String, view: View) {
        Snackbar.make(view, "Toto je Snackbar", Snackbar.LENGTH_LONG)
            .setAction("Akcia") {
                // kód po stlačení akcie na Snackbar
            }.show()

        findNavController().navigate(R.id.action_login_feed)
    }
}