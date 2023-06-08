package com.example.application

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.application.data.lock.Lock
import com.example.application.data.lock.LockDao
import com.example.application.data.lock.LockDatabase
import com.example.application.data.user.User
import com.example.application.data.user.UserDatabase
import com.example.application.data.user.UserDao
import com.example.application.databinding.FragmentCreateAccountBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class CreateAccountFragment : Fragment() {
    private var _binding : FragmentCreateAccountBinding? = null

    private val binding get() = _binding!!

    private lateinit var userDao: UserDao


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

        userDao = UserDatabase.getDatabase(requireContext()).userDao()

        binding.hasAccount.setOnClickListener {
            findNavController().navigate(R.id.action_createAccountFragment_to_startFragment)
        }

        lockDao = LockDatabase.getLockDatabase(requireContext()).lockDao()
        val lock = Lock("Back Door", "2023-05-31", "John Doe", 1,comment="old", eKey = null, lock_state = "open")


        binding.signUp.setOnClickListener {
            val username = binding.usernameText.text.toString()
            val email = binding.emailText.text.toString()
            val password = binding.passwordText.text.toString()
            val allowNotifications = 1
            val userID= 45

            val user = User(username, email, password, allowNotifications, userID)


            GlobalScope.launch {
                lockDao.upsertLock(lock)
                val lockid= lockDao.getFirstLockId()

                userDao.upsertUser(user)
            }

            findNavController().navigate(R.id.action_createAccountFragment_to_startFragment)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}