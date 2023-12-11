package eu.mcomputing.mobv.zadanie

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import eu.mcomputing.mobv.zadanie.data.PreferenceData
import eu.mcomputing.mobv.zadanie.data.api.DataRepository
import eu.mcomputing.mobv.zadanie.databinding.FragmentFeedBinding
import eu.mcomputing.mobv.zadanie.viewmodels.FeedViewModel
import java.lang.Thread.sleep
import java.time.LocalTime

class FeedFragment : Fragment() {
    private lateinit var viewModel: FeedViewModel
    private lateinit var binding: FragmentFeedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializácia ViewModel
        viewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FeedViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[FeedViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            model = viewModel
        }.also { bnd ->

            val sharing = PreferenceData.getInstance().getSharing(requireContext())

            val startTime = PreferenceData.getInstance().getStartSharingTime(context)
            val endTime = PreferenceData.getInstance().getEndSharingTime(context)

            var isBetween = true

            // start and end times are set
            if (startTime != null && endTime != null) {
                // calculate interval
                val currentTime = LocalTime.now()
                isBetween = currentTime.isAfter(startTime) && currentTime.isBefore(endTime)
            }

            // check if sharing is on
            if (sharing && isBetween) {

                // this creates a vertical layout Manager
                bnd.feedRecyclerview.layoutManager = LinearLayoutManager(context)

                // Setting the Adapter with the recyclerview
                val feedAdapter = MyAdapter(viewModel)
                bnd.feedRecyclerview.adapter = feedAdapter

                // Pozorovanie zmeny hodnoty
                viewModel.feed_items.observe(viewLifecycleOwner) { items ->
                    Log.d("FeedFragment", "nove hodnoty $items")
                    feedAdapter.updateItems(items ?: emptyList())
                }

                bnd.pullRefresh.setOnRefreshListener {
                    viewModel.updateItems()
                }

                viewModel.loading.observe(viewLifecycleOwner) {
                    bnd.pullRefresh.isRefreshing = it
                }
            } else{
                bnd.pullRefresh.setOnRefreshListener {
                    val snackbar = Snackbar
                        .make(view, "Turn on sharing to show users", Snackbar.LENGTH_LONG)
                        .setAction("Turn on") {
                            findNavController(view).navigate(R.id.action_to_profile)
                        }
                        .show()
                    bnd.pullRefresh.isRefreshing = false
                }
            }
        }

    }

    /*override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }*/
}