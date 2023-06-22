package com.example.application

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.application.data.LockDBSingleton
import com.example.application.data.UserAndLock.UserAndLock
import com.example.application.data.UserAndLock.UserAndLockDao
import com.example.application.data.UserAndLockDBSingleton
import com.example.application.data.UserDBSingleton
import com.example.application.data.lock.Lock
import com.example.application.data.lock.LockDao
import com.example.application.data.user.UserDao
import com.example.application.databinding.FragmentAddNewLockBinding
import com.example.application.model.AppViewModel
import com.example.myapplication.functions.serverConnectionFunctions
import kotlinx.coroutines.launch

class AddNewLockFragment : Fragment() {

    private var _binding : FragmentAddNewLockBinding? = null

    private val binding get() = _binding!!

    private val sharedViewModel: AppViewModel by activityViewModels()
    private val functionConnection = serverConnectionFunctions()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddNewLockBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val lockDatabaseSingleton = LockDBSingleton.getInstance(requireContext())
        val lockDao : LockDao? = lockDatabaseSingleton!!.getAppDatabase().lockDao()

        val userDatabaseSingleton = UserDBSingleton.getInstance(requireContext())
        val userDao : UserDao = userDatabaseSingleton!!.getAppDatabase().userDao()

        val userAndLockDatabase = UserAndLockDBSingleton.getInstance(requireContext())
        val userLockDao : UserAndLockDao? = userAndLockDatabase!!.getAppDatabase().userAndLockDao()

        val username = sharedViewModel.username.value.toString()
        binding.addlockbutton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val lockCode = binding.lockCodeText.text.toString()
                val userID = userDao.getUserIdByUsername(username)
                val listLock : List<String>? = functionConnection.addNewLock(username, lockCode)

                if (listLock != null) {
                    var addedLock : Any?
                    for (retLock in listLock){
                        addedLock = functionConnection.getLockConnLogin(username, retLock)
                        if (lockCode.isEmpty()) {
                            Toast.makeText(context, "Error: Fill all entries", Toast.LENGTH_SHORT).show()
                        } else {
                            val lock = addedLock?.let { it1 -> Lock(it1.lock_name,addedLock.last_access,addedLock.user_last_access,addedLock.number_of_users,addedLock.comment, eKey = null, addedLock.lock_state, addedLock.lock_id.toInt()) }
                            if (lock != null) {
                                lockDao?.upsertLock(lock)
                                if(userLockDao?.getCountOfUserId() != null && addedLock != null){
                                    val userandlock : UserAndLock = UserAndLock(userID,lock.lock_id, lock_access_pin = "000000", permission_level = addedLock.permission_level, userLockDao.getCountOfUserId() + 1)
                                    userLockDao.upsertUserAndLock(userandlock)
                                }
                            }
                        }
                    }
                    Toast.makeText(context, "SUCCESS: Your lock was added", Toast.LENGTH_SHORT)
                        .show()
                    binding.lockCodeText.text?.clear()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}