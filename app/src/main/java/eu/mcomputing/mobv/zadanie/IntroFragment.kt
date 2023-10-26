package eu.mcomputing.mobv.zadanie

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import eu.mcomputing.mobv.zadanie.databinding.FragmentIntroBinding

class IntroFragment : Fragment(R.layout.fragment_intro) {
    private var binding: FragmentIntroBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentIntroBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
        }.also { bnd ->
            bnd.login.apply {
                setOnClickListener {
                    it.findNavController().navigate(R.id.action_intro_login)
                }
            }

            bnd.signup.apply {
                setOnClickListener {
                    it.findNavController().navigate(R.id.action_intro_signup)
                }
            }
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}