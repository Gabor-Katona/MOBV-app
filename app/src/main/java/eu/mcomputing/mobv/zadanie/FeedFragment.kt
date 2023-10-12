package eu.mcomputing.mobv.zadanie

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FeedFragment : Fragment(R.layout.fragment_feed) {
    private lateinit var viewModel: FeedViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializácia ViewModel
        viewModel = ViewModelProvider(requireActivity())[FeedViewModel::class.java]

        // getting the recyclerview by its id
        val recyclerview = view.findViewById<RecyclerView>(R.id.recycler_view)

        // this creates a vertical layout Manager
        val layoutManager = LinearLayoutManager(context)
        recyclerview.layoutManager = layoutManager

        /*val gridLayoutManager = GridLayoutManager(context, 3)
        recyclerview.layoutManager = gridLayoutManager*/

        // This will pass the ArrayList to our Adapter
        val adapter = MyAdapter()

        // Setting the Adapter with the recyclerview
        recyclerview.adapter = adapter

        // Pozorovanie zmeny hodnoty
        viewModel.feed_items.observe(viewLifecycleOwner) { items ->
            // Tu môžete aktualizovať UI podľa hodnoty stringValue
            adapter.updateItems(items)
        }


        // ArrayList of class ItemsViewModel
        val data = ArrayList<MyItem>()

        // This loop will create 10 Views containing
        // the image with the count of view
        for (i in 1..10) {
            data.add(MyItem(i, R.drawable.ic_action_map, "Item " + i))
        }

        viewModel.updateItems(data)

    }
}