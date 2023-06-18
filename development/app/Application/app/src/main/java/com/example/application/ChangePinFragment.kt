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
import com.example.application.data.UserAndLock.UserAndLockDao
import com.example.application.data.UserAndLockDBSingleton
import com.example.application.data.UserDBSingleton
import com.example.application.data.lock.LockDao
import com.example.application.data.user.UserDao
import com.example.application.databinding.FragmentChangeEmailBinding
import com.example.application.databinding.FragmentChangePinBinding
import com.example.application.databinding.FragmentNotificationsBinding
import com.example.application.model.AppViewModel
import kotlinx.coroutines.launch


class ChangePinFragment : Fragment() {
    private var _binding : FragmentChangePinBinding? = null

    private val binding get() = _binding!!

    private lateinit var username1 : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            username1 = it.getString("username1").toString()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentChangePinBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val lockDatabase = LockDBSingleton.getInstance(requireContext())
        val lockDao: LockDao? = lockDatabase!!.getAppDatabase().lockDao()

        val userDatabase = UserDBSingleton.getInstance(requireContext())
        val userDao: UserDao = userDatabase!!.getAppDatabase().userDao()

        val userAndLockDatabase = UserAndLockDBSingleton.getInstance(requireContext())
        val userLockDao: UserAndLockDao? = userAndLockDatabase!!.getAppDatabase().userAndLockDao()

        var userId: Int = 0
        //val lockId: Int = 0
        val userLockID: Int = 0
        var userLockPin: String=""

        binding.confirm.setOnClickListener {
            val oldPin = binding.oldPinText.text.toString()
            val newPin= binding.newPinText.text.toString()
            val newPinConfirmed = binding.pinConfirmationText.text.toString()

            viewLifecycleOwner.lifecycleScope.launch {

                println("USERNAME " + username1)

                if (userLockDao != null) {

                    userId=userDao.getUserIdByUsername(username1)
                    userLockPin = userLockDao.getLockPin(userId)
                }

                if (oldPin.isEmpty() ||
                    newPin.isEmpty() ||
                    newPinConfirmed.isEmpty()
                ) {
                    Toast.makeText(context, "Error: Fill all entries", Toast.LENGTH_SHORT).show()
                }else if (newPin.length != 6) {
                    Toast.makeText(context, "Error: The pin must have four characters", Toast.LENGTH_SHORT)
                        .show()
                }
                else if (!containsOnlyNumbers(newPin)) {
                    Toast.makeText(context, "Error: The pin must have only numeric characters", Toast.LENGTH_SHORT)
                        .show()
                }
                else if (oldPin != userLockPin) {
                    Toast.makeText(context, "Error: Old pin is not correct", Toast.LENGTH_SHORT)
                        .show()
                }  else if (newPin != newPinConfirmed) {
                    Toast.makeText(
                        context,
                        "Error: Pin and Confirmation don't match",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    if (userLockDao != null) {
                        userLockDao.updateLockPin(userId, newPin)
                    }
                    Toast.makeText(context, "SUCCESS: Your pin was updated", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    fun containsOnlyNumbers(input: String): Boolean {
        val regex = Regex("^[0-9]+$")
        return regex.matches(input)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}