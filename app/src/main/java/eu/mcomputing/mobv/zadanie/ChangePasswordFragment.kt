package eu.mcomputing.mobv.zadanie

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import eu.mcomputing.mobv.zadanie.databinding.FragmentChangePasswordBinding

class ChangePasswordFragment : Fragment(R.layout.fragment_change_password) {
    private var binding: FragmentChangePasswordBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentChangePasswordBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
        }.also { bnd ->

            bnd.resetPasswdButton.setOnClickListener {
                findNavController().navigate(R.id.action_to_profile)
            }
        }

    }
}