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
import eu.mcomputing.mobv.zadanie.databinding.FragmentLoginBinding
import eu.mcomputing.mobv.zadanie.viewmodels.AuthViewModel

class LoginFragment : Fragment(R.layout.fragment_login) {
    private lateinit var viewModel: AuthViewModel
    private var binding: FragmentLoginBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(DataRepository.getInstance()) as T
            }
        })[AuthViewModel::class.java]

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentLoginBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
        }.also { bnd ->
            bnd.submitButton.apply {
                setOnClickListener {
                    val username: String = bnd.editTextUsername.text.toString()
                    val password: String = bnd.editTextPassword.text.toString()
                    login(username, password)
                }
            }

            bnd.resetPasswdButton.apply {
                setOnClickListener {
                    it.findNavController().navigate(R.id.action_to_passwordResetFragment)
                }
            }

            viewModel.loginResult.observe(viewLifecycleOwner) {
                if (it.isEmpty()) {
                    requireView().findNavController().navigate(R.id.action_login_feed)
                } else {
                    Snackbar.make(
                        bnd.submitButton,
                        it,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }

        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun login(username: String, password: String) {
        viewModel.loginUser(username, password)
    }
}