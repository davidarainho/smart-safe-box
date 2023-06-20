package com.example.application

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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
import com.example.myapplication.functions.serverConnectionFunctions
import com.example.myapplication.model.LockConn
import com.example.myapplication.model.UserConn
import kotlinx.coroutines.*
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
//            context?.deleteDatabase("new_userANDlock_database")
//            context?.deleteDatabase("new_lock_database")
//            context?.deleteDatabase("new_user_database")
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
                /*if (username == "beatriz" && password == "pass"){
                    validAccount = true
                }*/
                val (user, lockList) = loginCheck(username!!, password!!)
                if (user != null){
                    println(user)
                    println(lockList)
                    setupLogin(user,lockList)
                    validAccount = true
                }

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

        val user = userConn?.email?.let { userConn.let { it1 -> User(username = it1.username, email = it, password = it1.password, allow_notifications = it1.allow_notifications, user_id = it1.user_id.toInt()) } }

        //println(listLocks?.get(0))
        var numb = 1
        if (user != null) {
            userDao.upsertUser(user)
            if (listLocks != null) {
                for (i in listLocks){
                    val locks = i?.lock_id?.let { i.let { Lock(lock_name = i.lock_name, last_access = i.last_access, user_last_access = i.user_last_access, comment = i.comment, eKey = null, lock_state = i.lock_state, lock_id = i.lock_id.toInt(), number_of_users = i.number_of_users) } }
                    val userandlock = userConn.let { userConn.active_doors?.get(0)?.let { it1 -> i?.let { it2 -> i.lock_id.let { it3 -> UserAndLock(user_id = userConn.user_id.toInt(), lock_id = it3.toInt(), lock_access_pin = userConn.access_pin, permission_level = it2.permission_level, userLockId = numb) } } } }
                    withContext(Dispatchers.IO) {
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
                    numb++
                }
            }
        }
    }

    private fun loginCheck(username: String, password : String) : Pair<UserConn?, List<LockConn?>?> = runBlocking {

        //verificar conta login
        val userConn = functionConnection.getUserConnLogin(username, password)
        //Log.d("example","$userConn")
        var locksList : List<LockConn?>? = null
        if(userConn!=null){
            locksList = withContext(Dispatchers.IO) {
                userConn.active_doors?.stream()?.filter { l -> l != null }
                    ?.map { l -> returnsListLocks(username, l) }?.toList()
            }
        }
        Pair(userConn, locksList)
    }

    private fun returnsListLocks(username: String, lockID : String) = runBlocking{
        var value : LockConn? = null
        withContext(Dispatchers.IO) {
            value = functionConnection.getLockConnLogin(username, lockID)
        }

        value
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}