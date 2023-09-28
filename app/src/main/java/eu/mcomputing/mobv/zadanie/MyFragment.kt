package eu.mcomputing.mobv.zadanie

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController

class MyFragment : Fragment(R.layout.fragment_login) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Kód z Activity
        val btn: Button = view.findViewById(R.id.submitButton)
        btn.setOnClickListener { view ->
            /*val action = MyFragment2.actionFirstToSecond1("YourStringArgument")
            view.findNavController().navigate(action)*/
           //view.findNavController().navigate(R.id.action_first_to_second2)

            val action = ProfileFragmentDirections.actionFirstToProfil("hello")
            view.findNavController().navigate(action)
        }

    }
}