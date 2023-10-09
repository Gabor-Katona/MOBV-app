package eu.mcomputing.mobv.zadanie

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

data class Item(val id: Int ,val imageResource: Int, val text: String)

class MyAdapter() : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    private var items: List<Item> = listOf()

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
        holder.imageView.setImageResource(items[position].imageResource)
        holder.textView.text = items[position].text
    }

    // Vracia počet položiek v zozname
    override fun getItemCount() = items.size

    /*fun updateItems(newItems: List<Item>) {
        items = newItems
        notifyDataSetChanged()
    }*/

    fun updateItems(newItems: List<Item>) {
        val diffCallback = MyItemDiffCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        items = newItems
        diffResult.dispatchUpdatesTo(this)
    }

}

class MyItemDiffCallback(
    private val oldList: List<Item>,
    private val newList: List<Item>
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