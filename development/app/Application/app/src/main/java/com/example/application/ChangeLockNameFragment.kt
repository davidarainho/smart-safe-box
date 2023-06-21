package com.example.application

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.application.data.LockDBSingleton
import com.example.application.data.UserAndLock.UserAndLockDao
import com.example.application.data.UserAndLockDBSingleton
import com.example.application.data.UserDBSingleton
import com.example.application.data.lock.Lock
import com.example.application.data.lock.LockDao
import com.example.application.data.user.UserDao
import com.example.application.databinding.FragmentChangeLockNameBinding
import com.example.application.databinding.FragmentNotificationsBinding
import com.example.myapplication.functions.serverConnectionFunctions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.streams.toList


class ChangeLockNameFragment : Fragment() {
    private var _binding : FragmentChangeLockNameBinding? = null

    private val binding get() = _binding!!

    private lateinit var username : String
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
        // Inflate the layout for this fragment
        _binding = FragmentChangeLockNameBinding.inflate(inflater,container,false)

        //var userID= 0;

        val arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, loadLockIds(requireContext()))
        binding.dropdownItems.setAdapter(arrayAdapter)


        return binding.root


    }

    fun loadLockIds(context: Context): List<String> = runBlocking {
        val userDatabaseSingleton = UserDBSingleton.getInstance(context)
        val userDao: UserDao = userDatabaseSingleton!!.getAppDatabase().userDao()

        val userAndLockDatabase = UserAndLockDBSingleton.getInstance(context)
        val userLockDao: UserAndLockDao? = userAndLockDatabase!!.getAppDatabase().userAndLockDao()

        val lockDatabase = LockDBSingleton.getInstance(context)
        val lockDao: LockDao? = lockDatabase!!.getAppDatabase().lockDao()

        var userID: Int
        val lockNames = mutableListOf<String>()

        withContext(Dispatchers.IO) {
            userID = userDao.getUserIdByUsername(username)
            if (userLockDao != null) {
                val listOfLockIDs = userLockDao.getLocksIDByUserId(userID)

                listOfLockIDs.forEach { lockId ->
                    val lockName = lockDao?.getLocknameByLockID(lockId)
                    lockName?.let { lockNames.add(it) }
                }
            }
        }

        lockNames
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val lockDatabaseSingleton = LockDBSingleton.getInstance(requireContext())
        val lockDao : LockDao? = lockDatabaseSingleton!!.getAppDatabase().lockDao()
        var lockId: Int = 0
        var lockName: String

        binding.dropdownItems.setOnItemClickListener { parent, _, position, id ->
            val selectedItem = parent.getItemAtPosition(position)
            lockName = selectedItem as String

            if (lockDao != null) {
                viewLifecycleOwner.lifecycleScope.launch {
                    val id = withContext(Dispatchers.IO) { getLockId(lockName) }
                    lockId = id
                }
            }
        }

        binding.changelocknamebutton.setOnClickListener {
            val newLockName = binding.newLockNameText.text.toString()

            viewLifecycleOwner.lifecycleScope.launch {

                if (newLockName.isEmpty() || (lockId==0)) {
                    Toast.makeText(context, "Error: Fill all entries", Toast.LENGTH_SHORT).show()
                } else
                    if (lockDao != null && functionConnection.changeLockName(username,newLockName,lockId.toString())) {
                        lockDao.updateLockName(lockId, newLockName)
                        Toast.makeText(context, "SUCCESS: Your lock name was updated", Toast.LENGTH_SHORT).show()
                        binding.newLockNameText.text?.clear()
                    }
                }
            }

        }

    private suspend fun getLockId(lockname: String): Int = withContext(Dispatchers.IO) {
        val lockDatabase = context?.let { LockDBSingleton.getInstance(it) }
        val lockDao: LockDao? = lockDatabase!!.getAppDatabase().lockDao()

        lockDao?.getLockIdByLockname(lockname) ?: 0
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}