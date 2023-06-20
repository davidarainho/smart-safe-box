package com.example.application

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.application.data.LockDBSingleton
import com.example.application.data.UserAndLock.UserAndLockDao
import com.example.application.data.UserAndLockDBSingleton
import com.example.application.data.UserDBSingleton
import com.example.application.data.lock.LockDao
import com.example.application.data.user.UserDao
import com.example.application.databinding.FragmentSettingsBinding
import com.example.application.databinding.FragmentStartBinding
import com.example.application.model.AppViewModel
import com.example.myapplication.functions.serverConnectionFunctions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {
    private var _binding : FragmentSettingsBinding? = null

    private val binding get() = _binding!!

    private val sharedViewModel: AppViewModel by activityViewModels()

    private val functionConnection = serverConnectionFunctions()
    private lateinit var username: String



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Obter valor do user neste fragmento
        val lockDatabaseSingleton = LockDBSingleton.getInstance(requireContext())
        val lockDao : LockDao? = lockDatabaseSingleton!!.getAppDatabase().lockDao()

        val userDatabaseSingleton = UserDBSingleton.getInstance(requireContext())
        val userDao : UserDao = userDatabaseSingleton!!.getAppDatabase().userDao()

        val userAndLockDatabase = UserAndLockDBSingleton.getInstance(requireContext())
        val userLockDao : UserAndLockDao? = userAndLockDatabase!!.getAppDatabase().userAndLockDao()
        //println(sharedViewModel.username.value)

        binding.changeEmail.setOnClickListener {

            val action = SettingsFragmentDirections.actionSettingsFragmentToChangeEmailFragment(username = sharedViewModel.username.value.toString())
            binding.changeEmail.findNavController().navigate(action)
        }
        binding.manageNotifications.setOnClickListener {

            val action = SettingsFragmentDirections.actionSettingsFragmentToNotificationsFragment(username = sharedViewModel.username.value.toString())
            binding.manageNotifications.findNavController().navigate(action)
        }
        binding.changeLockName.setOnClickListener {

            val action = SettingsFragmentDirections.actionSettingsFragmentToChangeLockNameFragment(username = sharedViewModel.username.value.toString())
            binding.changeLockName.findNavController().navigate(action)
        }

        binding.deleteaccount.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(resources.getString(R.string.title_delete_account))
                .setMessage(resources.getString(R.string.supporting_text_delete_account))
                .setNeutralButton(resources.getString(R.string.cancel)) { _, _ ->
                    // Respond to neutral button press
                }
//                .setNegativeButton(resources.getString(R.string.decline)) { dialog, which ->
//                    // Respond to negative button press
//                }
                .setPositiveButton(resources.getString(R.string.accept)) { dialog, which ->
                    // Respond to positive button press
                    activity?.finish()
                    // apaga os dados de todas as tabelas
                    viewLifecycleOwner.lifecycleScope.launch {
                        if (!functionConnection.deleteAccount(username)){
                            Toast.makeText(context, "Error: It wasn't possible to delete account", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            lockDao?.deleteLockData()
                            userDao.deleteUserData()
                            userLockDao?.deleteUserLockData()
                        }

                    }
                }
                .show()


        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}