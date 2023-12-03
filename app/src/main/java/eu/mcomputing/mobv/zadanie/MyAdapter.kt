package eu.mcomputing.mobv.zadanie

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import eu.mcomputing.mobv.zadanie.data.db.entities.UserEntity
import eu.mcomputing.mobv.zadanie.utils.ItemDiffCallback
import eu.mcomputing.mobv.zadanie.viewmodels.FeedViewModel

data class MyItem(val id: Int, val imageResource: Int, val text: String){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MyItem

        if (id != other.id) return false
        if (imageResource != other.imageResource) return false
        if (text != other.text) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + imageResource
        result = 31 * result + text.hashCode()
        return result
    }
}

class MyAdapter(private val viewModel: FeedViewModel) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    private var items: List<UserEntity> = listOf()

    // ViewHolder poskytuje odkazy na zobrazenia v každej položke
    //class MyViewHolder(val imageView: ImageView, val textView: TextView) : RecyclerView.ViewHolder(imageView, textView)
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.item_image)
        val textView: TextView = itemView.findViewById(R.id.item_text)
    }

    // Táto metóda vytvára nový ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)

        return MyViewHolder(view)
    }

    // Táto metóda prepojí dáta s ViewHolderom
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.item_text).text = items[position].name
        if(items[position].photo != ""){
            Log.d("maAdapter", items[position].photo)
        }

        Picasso.get()
            .load("https://upload.mcomputing.eu/" + items[position].photo )
            .placeholder(R.drawable.ic_action_account)
            .into(holder.itemView.findViewById<ImageView>(R.id.item_image))

        holder.itemView.setOnClickListener {
            Log.d("Click", "clicked $position , " + items[position].uid)
            viewModel.selectedUser.postValue(items[position])
            it.findNavController().navigate(R.id.action_to_other_profile)
        }

    }

    // Vracia počet položiek v zozname
    override fun getItemCount() = items.size

    /*fun updateItems(newItems: List<Item>) {
        items = newItems
        notifyDataSetChanged()
    }*/

    fun updateItems(newItems: List<UserEntity>) {
        val diffCallback = ItemDiffCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        items = newItems
        diffResult.dispatchUpdatesTo(this)
    }

}

class MyItemDiffCallback(
    private val oldList: List<MyItem>,
    private val newList: List<MyItem>
) : DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}