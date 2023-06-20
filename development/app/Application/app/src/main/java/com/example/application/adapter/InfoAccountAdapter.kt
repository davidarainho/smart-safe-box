package com.example.application.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.application.R
import com.example.application.data.UserAndLock.UserAndLockDao
import com.example.application.data.UserAndLockDBSingleton
import com.example.application.data.UserDBSingleton
import com.example.application.data.user.User
import com.example.application.data.user.UserDao
import com.example.application.databinding.FragmentBotsheetUserBinding
import com.example.application.model.AppViewModel
import com.example.myapplication.functions.serverConnectionFunctions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class InfoAccountAdapter (
    private val dataset: List<String>?,
    private val context : Context,
    private val binding: FragmentBotsheetUserBinding,
    private val username : String,
    private val lockID: Int
) : RecyclerView.Adapter<InfoAccountAdapter.ItemViewHolder>() {

    // Assume menor nivel de permissao por default
    private var level : Int = 3

    private val functionConnection = serverConnectionFunctions()


    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.users_item)
        // Aqui adicionar para a outra caixa de texto
    }

    /**
     * Create new views (invoked by the layout manager)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        level = getPermissionLevel(username, lockID)
        // create a new view
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.drop_down_item, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    /**
     * Replace the contents of a view (invoked by the layout manager)
     */
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val user_to_rmv = dataset?.get(position)
        if (user_to_rmv != null) {
            holder.textView.text = user_to_rmv

            // Senao der da popup de "Nao tens nivel permissao neste lock para usar esta funcao"
            holder.itemView.setOnClickListener {
                if(user_to_rmv != username){
                    MaterialAlertDialogBuilder(context)
                        .setTitle(context.resources.getString(R.string.title_remove_account))
                        .setMessage(context.resources.getString(R.string.supporting_text_remove_account, user_to_rmv))
                        .setNeutralButton(context.resources.getString(R.string.cancel)) { _, _ ->
                            // Respond to neutral button press
                        }
                        .setPositiveButton(context.resources.getString(R.string.accept)) { _, _ ->
                            if (rmvAccount(user_to_rmv) == true){
                                Toast.makeText(context, "The user was removed successfully", Toast.LENGTH_SHORT)
                                    .show()
                            } else{
                                Toast.makeText(context, "You can't remove this user", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                        .show()
                }
            }
        }
    }


    private fun getPermissionLevel(user : String, lockID : Int) : Int = runBlocking {
        val userDatabaseSingleton = UserDBSingleton.getInstance(context)
        val userDao : UserDao = userDatabaseSingleton!!.getAppDatabase().userDao()

        val userAndLockDatabase = UserAndLockDBSingleton.getInstance(context)
        val userLockDao : UserAndLockDao? = userAndLockDatabase!!.getAppDatabase().userAndLockDao()

        val userId : Int = userDao.getUserIdByUsername(user)

        var lev : Int = 3
        if (userLockDao != null){
            lev = userLockDao.getUserLockPermissionLevel(userId, lockID)
        }

        lev
    }

    private fun rmvAccount(username_to_remove : String) : Boolean? = runBlocking{
        val rmv : Boolean?
        withContext(Dispatchers.IO) {
            rmv = functionConnection.removeAccountFromDoor(username, username_to_remove, lockID.toString())
        }
        rmv
    }

    /**
     * Return the size of your dataset (invoked by the layout manager)
     */
    override fun getItemCount() : Int {
        if(dataset != null){
            return dataset.size
        }
        return 0
    }
}