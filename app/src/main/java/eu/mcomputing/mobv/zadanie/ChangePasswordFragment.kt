package eu.mcomputing.mobv.zadanie

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import eu.mcomputing.mobv.zadanie.data.api.DataRepository
import eu.mcomputing.mobv.zadanie.databinding.FragmentChangePasswordBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChangePasswordFragment : Fragment(R.layout.fragment_change_password) {
    private var binding: FragmentChangePasswordBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentChangePasswordBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
        }.also { bnd ->

            bnd.resetPasswdButton.setOnClickListener {
                val newPassword = bnd.editTextNewPassword.text.toString();
                val newPasswordRepeat = bnd.editTextNewPasswordRepeat.text.toString()

                if (!newPassword.equals(newPasswordRepeat)) {
                    // the repeated new password is not the same as new password
                    Snackbar.make(bnd.resetPasswdButton, "Password and repeated password do not match", Snackbar.LENGTH_SHORT).show()
                } else {
                    val scope = CoroutineScope(Dispatchers.Main)

                    scope.launch {
                        val response = DataRepository.getInstance(requireContext()).apiChangePassword(bnd.editTextActualPasswd.text.toString(), bnd.editTextNewPassword.text.toString())
                        Snackbar.make(bnd.resetPasswdButton, response, Snackbar.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_to_profile)
                    }
                }
            }
        }

    }
}