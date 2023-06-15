package com.example.application.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.application.R
import com.example.application.data.UserAndLock.UserAndLockDao
import com.example.application.data.UserAndLockDBSingleton
import com.example.application.data.UserDBSingleton
import com.example.application.data.user.User
import com.example.application.data.user.UserDao
import com.example.application.databinding.FragmentBotsheetUserBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.runBlocking

class InfoAccountAdapter (
    private val dataset: List<User>?,
    private val context : Context,
    private val binding: FragmentBotsheetUserBinding,
    private val username : String,
    private val lockID: Int
) : RecyclerView.Adapter<InfoAccountAdapter.ItemViewHolder>() {

    // Assume menor nivel de permissao por default
    private var level : Int = 3

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
        val user = dataset?.get(position)
        if (user != null) {
            holder.textView.text = user.username

            //////////////// Verificar nivel do utilizador no lock ////////////////
            // Senao der da popup de "Nao tens nivel permissao neste lock para usar esta funcao"
            holder.itemView.setOnClickListener {
                val optionUserLevel : Int = getPermissionLevel(user.username, lockID)

                if(optionUserLevel < level && user.username != username){
                    MaterialAlertDialogBuilder(context)
                        .setTitle(context.resources.getString(R.string.title_remove_account))
                        .setMessage(context.resources.getString(R.string.supporting_text_remove_account, user.username))
                        .setNeutralButton(context.resources.getString(R.string.cancel)) { _, _ ->
                            // Respond to neutral button press
                        }
                        .setPositiveButton(context.resources.getString(R.string.accept)) { dialog, which ->
                            // Envia pedido para remover o user
                            // Remove dados da base de dados desse utilizador

                            println("Remove dados da base de dados desse utilizador")
                        }
                        .show()
                }


                println(level)


            }


        }
        // Aqui adicionar depois para atribuir à caixa de texto
        // Mais o .userEmail
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