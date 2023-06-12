package com.example.application

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.application.data.LockDBSingleton
import com.example.application.data.UserAndLock.UserAndLock
import com.example.application.data.UserAndLock.UserAndLockDao
import com.example.application.data.UserAndLock.UserAndLockDatabase
import com.example.application.data.UserAndLockDBSingleton
import com.example.application.data.UserDBSingleton
import com.example.application.data.lock.Lock
import com.example.application.data.lock.LockDao
import com.example.application.data.user.User
import com.example.application.data.user.UserDatabase
import com.example.application.data.user.UserDao
import com.example.application.databinding.FragmentCreateAccountBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class CreateAccountFragment : Fragment() {
    private var _binding : FragmentCreateAccountBinding? = null

    private val binding get() = _binding!!

    //private lateinit var userDao: UserDao


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCreateAccountBinding.inflate(inflater,container,false)
        return binding.root
    }

    private lateinit var lockDao: LockDao
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //userDao = UserDatabase.getDatabase(requireContext()).userDao()

        binding.hasAccount.setOnClickListener {
            findNavController().navigate(R.id.action_createAccountFragment_to_startFragment)
        }


        // FAZER ISTO PARA CADA FRAGEMENTO ONDE VAMOS CHAMAR UMA FUNÇÃO DA BASE DE DADOS

        val lockDatabase = LockDBSingleton.getInstance(requireContext())
        val lockDao: LockDao? = lockDatabase!!.getAppDatabase().lockDao()

        val userDatabase = UserDBSingleton.getInstance(requireContext())
        val userDao: UserDao = userDatabase!!.getAppDatabase().userDao()

        val userAndLockDatabase = UserAndLockDBSingleton.getInstance(requireContext())
        val userLockDao: UserAndLockDao? = userAndLockDatabase!!.getAppDatabase().userAndLockDao()


        val lock = Lock(lock_name="CARLOTA", last_access= "2027-06-31", user_last_access =  "Logan", number_of_users = 2,comment="new", eKey = null, lock_state = "open", lock_id=5658)
        val  lock1= Lock(lock_name="RUCA", last_access="2027-04-10", user_last_access =  "Logan", number_of_users = 1,comment="old", eKey = null, lock_state = "open", lock_id=863)

        binding.signUp.setOnClickListener {
//            val username = binding.usernameText.text.toString()
//            val email = binding.emailText.text.toString()
//            val password = binding.passwordText.text.toString()
//            val allowNotifications = 1
//            val userID= 4444
//
//            val user = User(username, email, password, allowNotifications, userID)
//            val userLock_association= UserAndLock(user_id = 741258, 7, userLockId = 91, permission_level = 4, lock_access_pin= "1234" )
//            val userLock_association1= UserAndLock(user_id = 741258, 9, userLockId = 94, permission_level = 4, lock_access_pin= "1234" )
//
//
//            GlobalScope.launch {
//                viewLifecycleOwner.lifecycleScope.launch {
//
//                    if (lockDao != null) {
//                        lockDao.upsertLock(lock)
//                        lockDao.upsertLock(lock1)
//                    }
//
//                    userDao.upsertUser(user)
//
//                    if (userLockDao != null) {
//                        userLockDao.upsertUserAndLock(userLock_association)
//                        userLockDao.upsertUserAndLock(userLock_association1)
//                    }
//
//
//                }
//            }
            findNavController().navigate(R.id.action_createAccountFragment_to_startFragment)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}