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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.streams.toList


class ChangeLockNameFragment : Fragment() {
    private var _binding : FragmentChangeLockNameBinding? = null

    private val binding get() = _binding!!

    private lateinit var username : String
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

        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.drop_down_item, loadLockIds(requireContext()))
        binding.dropdownItems.setAdapter(arrayAdapter)

        return binding.root
    }

    fun loadLockIds(context: Context): List<Int> = runBlocking {

        val userDatabaseSingleton = UserDBSingleton.getInstance(context)
        val userDao : UserDao = userDatabaseSingleton!!.getAppDatabase().userDao()

        val userAndLockDatabase = UserAndLockDBSingleton.getInstance(context)
        val userLockDao : UserAndLockDao? = userAndLockDatabase!!.getAppDatabase().userAndLockDao()

        var userID: Int

        var listOfLockIDs: List<Int> = emptyList()


        withContext(Dispatchers.IO) {
            userID=userDao.getUserIdByUsername(username)
            if (userLockDao != null) {
                listOfLockIDs = userLockDao.getLocksIDByUserId(userID)
            }
        }

        listOfLockIDs
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val lockDatabaseSingleton = LockDBSingleton.getInstance(requireContext())
        val lockDao : LockDao? = lockDatabaseSingleton!!.getAppDatabase().lockDao()
        var lockId: Int = 0

        binding.dropdownItems.setOnItemClickListener { parent, _, position, id ->
            val selectedItem = parent.getItemAtPosition(position)
            lockId = selectedItem as Int

        }


        binding.changelocknamebutton.setOnClickListener {
            val newLockName = binding.newLockNameText.text.toString()

            viewLifecycleOwner.lifecycleScope.launch {

                if (newLockName.isEmpty() || (lockId==0)) {
                    Toast.makeText(context, "Error: Fill all entries", Toast.LENGTH_SHORT).show()
                } else
                    if (lockDao != null) {
                        lockDao.updateLockName(lockId, newLockName)
                        Toast.makeText(context, "SUCCESS: Your lock name was updated", Toast.LENGTH_SHORT).show()
                        binding.newLockNameText.text?.clear()
                    }

                }
            }

        }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}