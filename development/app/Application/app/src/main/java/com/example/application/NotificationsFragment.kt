package com.example.application

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.lifecycleScope
import com.example.application.data.UserDBSingleton
import com.example.application.data.user.UserDao
import com.example.application.databinding.FragmentNotificationsBinding
import com.example.application.databinding.FragmentSettingsBinding
import com.example.myapplication.functions.serverConnectionFunctions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class NotificationsFragment : Fragment() {
    private var _binding : FragmentNotificationsBinding? = null

    private val binding get() = _binding!!
    private lateinit var username : String
    private var notifications: Int = -1

    private val functionConnection = serverConnectionFunctions()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            username = it.getString("username").toString()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        notifications = getCurrentNotPref()
        // Inflate the layout for this fragment
        _binding = FragmentNotificationsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val switchCompat = view.findViewById<SwitchCompat>(R.id.allownotifications)
        // Inicializa o estado correto
        switchCompat.setChecked(notifications == 1)

        // Permite futuras alteracoes ao estado
        switchCompat.setOnClickListener {
            switchCompat.setChecked(switchOption())
        }
    }

    private fun switchOption() : Boolean = runBlocking{
        val userDatabase = UserDBSingleton.getInstance(requireContext())
        val userDao: UserDao = userDatabase!!.getAppDatabase().userDao()
        var userId: Int = 0
        withContext(Dispatchers.IO){
            if (changeNotification()) {
                notifications = if (notifications==1) { 0 } else { 1 }
                // Fazer o pedido
                userId=userDao.getUserIdByUsername(username)
                userDao.updateNotificationPreference(userId, notifications)
            }
            val valor = userDao.getNotificationPreferenceByUserId(userId)
            println(valor)
        }
        notifications == 1
    }

    private fun getCurrentNotPref() : Int = runBlocking {
        val current : Int

        val userDatabase = UserDBSingleton.getInstance(requireContext())
        val userDao: UserDao = userDatabase!!.getAppDatabase().userDao()
        var userId: Int = 0
        var notifications: Int = 1

        withContext(Dispatchers.IO){
            userId=userDao.getUserIdByUsername(username)
            current = userDao.getNotificationPreferenceByUserId(userId)
        }

        current
    }

    private fun changeNotification() : Boolean = runBlocking {
        val change : Boolean
        withContext(Dispatchers.IO){
            change = functionConnection.changeNotifications(username)
        }
        change
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}