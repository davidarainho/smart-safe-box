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
import com.example.application.databinding.FragmentBotsheetAccessBinding
import com.example.application.databinding.FragmentUpdatePinBinding
import com.example.application.model.AppViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class UpdatePinFragment : BottomSheetDialogFragment() {
    private var _binding : FragmentUpdatePinBinding? = null

    private val binding get() = _binding!!

    private val sharedViewModel: AppViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentUpdatePinBinding.inflate(inflater,container,false)
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

        val userId: Int = 8777
        val lockId: Int = 12
        val userLockID: Int = 5
        var userLockPin: String=""

        //println(sharedViewModel.lockID.value)

        binding.confirm.setOnClickListener {
            val oldPin = binding.oldPinText.text.toString()
            val newPin= binding.newPinText.text.toString()
            val newPinConfirmed = binding.pinConfirmationText.text.toString()

            viewLifecycleOwner.lifecycleScope.launch {

                if (userLockDao != null) {
                    userLockPin = userLockDao.getLockPin(userId, lockId)
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
                        userLockDao.updateLockPin(userId, lockId, newPin)
                    }
                    Toast.makeText(context, "SUCCESS: Your pin was updated", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun containsOnlyNumbers(input: String): Boolean {
        val regex = Regex("^[0-9]+$")
        return regex.matches(input)
    }
}