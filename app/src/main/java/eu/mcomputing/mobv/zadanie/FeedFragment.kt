package eu.mcomputing.mobv.zadanie

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FeedFragment : Fragment(R.layout.fragment_feed) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // getting the recyclerview by its id
        val recyclerview = view.findViewById<RecyclerView>(R.id.recycler_view)

        // this creates a vertical layout Manager
        val layoutManager = LinearLayoutManager(context)
        recyclerview.layoutManager = layoutManager

        /*val gridLayoutManager = GridLayoutManager(context, 3)
        recyclerview.layoutManager = gridLayoutManager*/

        // ArrayList of class ItemsViewModel
        val data = ArrayList<Item>()

        // This loop will create 20 Views containing
        // the image with the count of view
        for (i in 1..10) {
            data.add(Item(i, R.drawable.ic_action_map, "Item " + i))
        }

        // This will pass the ArrayList to our Adapter
        val adapter = MyAdapter()

        adapter.updateItems(data)

        // Setting the Adapter with the recyclerview
        recyclerview.adapter = adapter

    }
}