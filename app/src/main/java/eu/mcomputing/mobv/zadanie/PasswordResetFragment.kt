package eu.mcomputing.mobv.zadanie

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import eu.mcomputing.mobv.zadanie.data.api.DataRepository
import eu.mcomputing.mobv.zadanie.databinding.FragmentPasswordResetBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PasswordResetFragment : Fragment(R.layout.fragment_password_reset) {
    private var binding: FragmentPasswordResetBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentPasswordResetBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
        }.also { bnd ->

            bnd.sendEmailButton.setOnClickListener {
                val scope = CoroutineScope(Dispatchers.Main)

                scope.launch {
                    val response = DataRepository.getInstance(requireContext()).apiResetPassword(bnd.editTextEmail.text.toString())
                    Snackbar.make(bnd.sendEmailButton, response, Snackbar.LENGTH_SHORT).show()
                    it.findNavController().navigate(R.id.action_to_loginFragment)
                }

            }
        }

    }
}