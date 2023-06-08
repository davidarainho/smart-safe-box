package com.example.application.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.application.MyLocksFragmentDirections
import com.example.application.R
import com.example.application.data.lock.Lock

class ItemAdapter(
    private val dataset: List<Lock>?
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just an Affirmation object.
    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val textView: Button = view.findViewById(R.id.item_title)
    }

    /**
     * Create new views (invoked by the layout manager)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // create a new view
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    /**
     * Replace the contents of a view (invoked by the layout manager)
     */
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset?.get(position)
        if (item != null) {
            holder.textView.text = item.lock_name

            holder.textView.setOnClickListener {
                // Create an action from WordList to DetailList
                // using the required arguments
                val action = MyLocksFragmentDirections.actionMyLocksFragmentToLockerPageFragment(name = holder.textView.text.toString(), lockID = position)
                // Navigate using that action
                holder.itemView.findNavController().navigate(action)
            }
        }
    }

    /**
     * Return the size of your dataset (invoked by the layout manager)
     */
    override fun getItemCount(): Int {
        if(dataset != null){
            return dataset.size
        }
        return -1
    }
}