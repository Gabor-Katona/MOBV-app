package eu.mcomputing.mobv.zadanie

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import eu.mcomputing.mobv.zadanie.databinding.FragmentFeedBinding
import eu.mcomputing.mobv.zadanie.viewmodels.FeedViewModel

class FeedFragment : Fragment(R.layout.fragment_feed) {
    private lateinit var viewModel: FeedViewModel
    private var binding: FragmentFeedBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializácia ViewModel
        viewModel = ViewModelProvider(requireActivity())[FeedViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentFeedBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
        }.also { bnd ->

            // this creates a vertical layout Manager
            bnd.recyclerView.layoutManager = LinearLayoutManager(context)

            // Setting the Adapter with the recyclerview
            val feedAdapter = MyAdapter()
            bnd.recyclerView.adapter = feedAdapter

            /*// ArrayList of class ItemsViewModel
            val data = ArrayList<MyItem>()

            // This loop will create 10 Views containing
            // the image with the count of view
            for (i in 1..10) {
                data.add(MyItem(i, R.drawable.ic_action_map, "Item " + i))
            }

            viewModel.updateItems(data)*/

            // Pozorovanie zmeny hodnoty
            viewModel.feed_items.observe(viewLifecycleOwner) { items ->
                Log.d("FeedFragment", "nove hodnoty $items")
                feedAdapter.updateItems(items)
            }

            bnd.btnGenerate.setOnClickListener {
                viewModel.updateItems()
            }
        }

    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}