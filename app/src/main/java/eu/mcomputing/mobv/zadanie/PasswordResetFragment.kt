package eu.mcomputing.mobv.zadanie

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import eu.mcomputing.mobv.zadanie.databinding.FragmentPasswordResetBinding

class PasswordResetFragment : Fragment(R.layout.fragment_password_reset) {
    private var binding: FragmentPasswordResetBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentPasswordResetBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
        }.also { bnd ->

            bnd.sendEmailButton.setOnClickListener {
                it.findNavController().navigate(R.id.action_to_passwordResetFragment2)
            }
        }

    }
}