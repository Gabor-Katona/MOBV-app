package eu.mcomputing.mobv.zadanie

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import eu.mcomputing.mobv.zadanie.data.api.DataRepository
import eu.mcomputing.mobv.zadanie.viewmodels.AuthViewModel

class LoginFragment : Fragment(R.layout.fragment_login) {
    private lateinit var viewModel: AuthViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(DataRepository.getInstance()) as T
            }
        })[AuthViewModel::class.java]

        // login button
        view.findViewById<Button>(R.id.submit_button).apply {
            setOnClickListener {
                val username: String = view.findViewById<TextInputEditText>(R.id.edit_text_username).text.toString()
                val password: String = view.findViewById<TextInputEditText>(R.id.edit_text_password).text.toString()

                login(username, password)
            }
        }

        // password reset button
        view.findViewById<Button>(R.id.reset_passwd_button).apply {
            setOnClickListener {
                it.findNavController().navigate(R.id.action_to_passwordResetFragment)
            }
        }

        viewModel.loginResult.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                requireView().findNavController().navigate(R.id.action_login_feed)
            } else {
                Snackbar.make(
                    view.findViewById(R.id.submit_button),
                    it,
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun login(username: String, password: String) {
        viewModel.loginUser(username, password)
    }
}