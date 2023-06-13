package com.example.application.adapter

import android.text.Html
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
    private val dataset: List<Lock>?,
    private val username : String
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just an Affirmation object.
    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val button: Button = view.findViewById(R.id.item)
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
            val name = item.lock_name + "\n" + item.comment
            holder.button.text = name

            holder.button.setOnClickListener {
                // Create an action from WordList to DetailList
                // using the required arguments
                val action = MyLocksFragmentDirections.actionMyLocksFragmentToLockerPageFragment(name = item.lock_name, lockID = item.lock_id, username = username)
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