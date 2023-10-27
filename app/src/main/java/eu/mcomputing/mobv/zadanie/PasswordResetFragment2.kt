package eu.mcomputing.mobv.zadanie

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import eu.mcomputing.mobv.zadanie.databinding.FragmentPasswordReset2Binding

class PasswordResetFragment2 : Fragment(R.layout.fragment_password_reset2) {
    private var binding: FragmentPasswordReset2Binding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentPasswordReset2Binding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
        }.also { bnd ->

            bnd.resetPasswdButton.setOnClickListener {
                it.findNavController().navigate(R.id.action_to_loginFragment)
            }
        }

    }
}