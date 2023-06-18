package com.example.application

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.application.data.LockDBSingleton
import com.example.application.data.UserAndLock.UserAndLock
import com.example.application.data.UserAndLock.UserAndLockDao
import com.example.application.data.UserAndLockDBSingleton
import com.example.application.data.UserDBSingleton
import com.example.application.data.lock.Lock
import com.example.application.data.lock.LockDao
import com.example.application.data.user.User
import com.example.application.data.user.UserDao
import com.example.application.databinding.FragmentStartBinding
import kotlinx.coroutines.launch
import com.example.myapplication.functions.serverConnectionFunctions
import com.example.myapplication.model.ActiveLocksConn
import com.example.myapplication.model.LockConn
import com.example.myapplication.model.UserConn
import kotlinx.coroutines.*
import java.io.IOException
import kotlin.streams.toList


class StartFragment : Fragment() {
    private var _binding : FragmentStartBinding? = null

    private val binding get() = _binding!!

    private lateinit var userDao: UserDao
    private lateinit var lockDao: LockDao

    private val functionConnection = serverConnectionFunctions()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        _binding = FragmentStartBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.createAccount.setOnClickListener {
            findNavController().navigate(R.id.action_startFragment_to_createAccountFragment)
        }

        var validAccount: Boolean = false
        var username: String?
        var password: String?
        var userId: Int=0

        binding.signIn.setOnClickListener {

            username = binding.usernameText.text.toString()
            password = binding.passwordText.text.toString()

            viewLifecycleOwner.lifecycleScope.launch {
                // Miguel - chamar a funcao autenticacao
                // recebe a informacao, transfere para a base de dados, altera validAccount para true
//            if (username == "beatriz" && password == "wasd"){
//                validAccount = true
//            }

                validAccount = true

                // se flag correta passo para o proximo
                if (validAccount) {
                    validAccount = false
                    binding.usernameText.text?.clear()
                    binding.passwordText.text?.clear()
                    val action =
                        StartFragmentDirections.actionStartFragmentToMainAppActivity(userName = username!!) //userName = "cenas"
                    binding.signIn.findNavController().navigate(action)
                }

            }
        }
    }

    /* As funcoes em baixo sao auxiliares para inserir os dados do objeto para a base de dados */
    private fun setupLogin(userConn : UserConn?, listLocks : List<LockConn?>?) = runBlocking{
        val lockDatabase = LockDBSingleton.getInstance(requireContext())
        val lockDao: LockDao? = lockDatabase!!.getAppDatabase().lockDao()

        val userDatabase = UserDBSingleton.getInstance(requireContext())
        val userDao: UserDao = userDatabase!!.getAppDatabase().userDao()

        val userAndLockDatabase = UserAndLockDBSingleton.getInstance(requireContext())
        val userLockDao: UserAndLockDao? = userAndLockDatabase!!.getAppDatabase().userAndLockDao()

        val user = userConn?.email?.let { userConn.let { it1 -> User(username = it1.username, email = it, password = "null", allow_notifications = 1, user_id = 10) } }

        //println(listLocks?.get(0))
        var numb = 1
        if (listLocks != null) {
            for (i in listLocks){
                val locks = i?.lockId?.let { i.let { Lock(lock_name = i.lockId, last_access = "2023-06-0$numb", user_last_access = "Logan", comment = it.location, eKey = null, lock_state = i.state.toString(), lock_id = numb, number_of_users = i.activeUsers.size) } }

                val userandlock = userConn?.let { userConn.active_locks?.get(0)?.let { it1 -> UserAndLock(user_id = 10, lock_id = numb, lock_access_pin = it.access_pin, permission_level = it1.accessLevel, userLockId = numb) } }

                GlobalScope.launch {
                    if (user != null) {
                        userDao.upsertUser(user)
                        if (lockDao != null) {
                            if (locks != null) {
                                lockDao.upsertLock(locks)
                                if (userLockDao != null) {
                                    if (userandlock != null) {
                                        userLockDao.upsertUserAndLock(userandlock)
                                    }
                                }
                            }
                        }
                    }
                }

                numb++
            }
        }
    }

    private fun loginCheck(username: String, password : String) : Pair<UserConn?, List<LockConn?>?> = runBlocking {

        //verificar conta login
        val userConn = functionConnection.login(username, password)
        //Log.d("example","$userConn")
        val locksList : List<LockConn?>?
        withContext(Dispatchers.IO) {
            locksList =
                userConn?.active_locks?.stream()?.filter{ l -> l.lockId != null }?.map { lock -> lock.lockId?.let {
                    returnsListLocks(username,
                        it
                    )
                } }?.toList()
        }
        Pair(userConn, locksList)
    }

    private fun returnsListLocks(username: String, lockID : String) = runBlocking{
        var value : LockConn? = null
        withContext(Dispatchers.IO) {
            value = functionConnection.getLock(username, lockID)
        }

        value
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}