package com.example.application

import android.os.Bundle
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
import com.example.application.databinding.FragmentBotsheetChangeCommentBinding


import com.example.application.model.AppViewModel
import com.example.myapplication.functions.serverConnectionFunctions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class BotsheetChangeCommentFragment : BottomSheetDialogFragment() {
    private var _binding : FragmentBotsheetChangeCommentBinding? = null

    private val binding get() = _binding!!

    private val sharedViewModel: AppViewModel by activityViewModels()

    private val functionConnection = serverConnectionFunctions()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBotsheetChangeCommentBinding.inflate(inflater,container,false)
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

        val userId: Int = 0
        var lockId: Int = 0
        val userLockID: Int = 0

        val username = sharedViewModel.username.value
        lockId= sharedViewModel.lockID.value!!

        //println(sharedViewModel.lockID.value)

        binding.confirm.setOnClickListener {
            val newComment= binding.commentText.text.toString()


            viewLifecycleOwner.lifecycleScope.launch {

                if (newComment.isEmpty()) {
                    Toast.makeText(context, "Error: Fill all entries", Toast.LENGTH_SHORT).show()
                } else if (username?.let { it1 -> functionConnection.changeComment(it1, lockId.toString(), newComment) } == false) {
                    Toast.makeText(context, "Error: Server wasn't able to change comment", Toast.LENGTH_SHORT).show()
                }else {
                    if (lockDao != null) {
                        lockDao.updateLockComment(lockId, newComment)
                        Toast.makeText(context, "SUCCESS: Your comment was updated", Toast.LENGTH_SHORT)
                            .show()

                        binding.commentText.text?.clear()
                    }

                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}