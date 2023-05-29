package com.example.application.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.application.MyLocksFragmentDirections
import com.example.application.R
import com.example.application.model.AccountInfo
import com.example.application.model.Userlockers

class InfoAccountAdapter (
    private val dataset: List<AccountInfo>
) : RecyclerView.Adapter<InfoAccountAdapter.ItemViewHolder>() {

    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.users_item)
        // Aqui adicionar para a outra caixa de texto
    }

    /**
     * Create new views (invoked by the layout manager)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // create a new view
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.drop_down_item, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    /**
     * Replace the contents of a view (invoked by the layout manager)
     */
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val userinfo = dataset[position]
        holder.textView.text = userinfo.userName
        // Aqui adicionar depois para atribuir à caixa de texto
    }

    /**
     * Return the size of your dataset (invoked by the layout manager)
     */
    override fun getItemCount() = dataset.size
}