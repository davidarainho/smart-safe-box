package com.example.application

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.application.databinding.FragmentSettingsBinding
import com.example.application.databinding.FragmentStartBinding
import com.example.application.model.AppViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SettingsFragment : Fragment() {
    private var _binding : FragmentSettingsBinding? = null

    private val binding get() = _binding!!

    private val sharedViewModel: AppViewModel by activityViewModels()

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
        //println(sharedViewModel.username.value)

        binding.changeEmail.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_changeEmailFragment)
        }
        binding.manageNotifications.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_notificationsFragment)
        }
        binding.changeLockName.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_changeLockNameFragment)
        }

        binding.deleteaccount.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(resources.getString(R.string.title_delete_account))
                .setMessage(resources.getString(R.string.supporting_text_delete_account))
                .setNeutralButton(resources.getString(R.string.cancel)) { dialog, which ->
                    // Respond to neutral button press
                }
//                .setNegativeButton(resources.getString(R.string.decline)) { dialog, which ->
//                    // Respond to negative button press
//                }
                .setPositiveButton(resources.getString(R.string.accept)) { dialog, which ->
                    // Respond to positive button press
                    activity?.finish()
                }
                .show()

            // apaga os dados de todas as tabelas
//           if (lockDao != null) {
//               lockDao.deleteLockData()
//           }
//           userDao.deleteUserData()
//           if (userLockDao != null) {
//               userLockDao.deleteUserLockData()
//           }
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}