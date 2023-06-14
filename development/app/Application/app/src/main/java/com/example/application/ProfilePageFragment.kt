package com.example.application

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigator
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.application.data.LockDBSingleton
import com.example.application.data.UserAndLock.UserAndLockDao
import com.example.application.data.UserAndLockDBSingleton
import com.example.application.data.UserDBSingleton
import com.example.application.data.lock.LockDao
import com.example.application.data.user.UserDao
import androidx.navigation.navGraphViewModels
import com.example.application.databinding.FragmentProfilePageBinding
import com.example.application.model.AppViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.jar.Attributes.Name

/**
 * A simple [Fragment] subclass.
 * Use the [ProfilePageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfilePageFragment : Fragment() {

//    companion object {
//        const val NAME = "name"
//    }

    private var _binding : FragmentProfilePageBinding? = null

    private val binding get() = _binding!!

    private val sharedViewModel: AppViewModel by activityViewModels()

    private lateinit var username: String
    private lateinit var name : String
//    private lateinit var name: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfilePageBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val lockDatabase = LockDBSingleton.getInstance(requireContext())
        val lockDao: LockDao? = lockDatabase!!.getAppDatabase().lockDao()

        val userDatabase = UserDBSingleton.getInstance(requireContext())
        val userDao: UserDao = userDatabase!!.getAppDatabase().userDao()

        val userAndLockDatabase = UserAndLockDBSingleton.getInstance(requireContext())
        val userLockDao: UserAndLockDao? = userAndLockDatabase!!.getAppDatabase().userAndLockDao()

        //println(name)
        //val username = args.userName
        //println(username)

        binding.changePassword.setOnClickListener {
            findNavController().navigate(R.id.action_profilePageFragment_to_changePasswordFragment)
        }

        binding.changeUsername.setOnClickListener {
            findNavController().navigate(R.id.action_profilePageFragment_to_changeUsernameFragment)
        }

       binding.logoutButton.setOnClickListener {
           MaterialAlertDialogBuilder(requireContext())
               .setTitle(resources.getString(R.string.title_logout_account))
               .setMessage(resources.getString(R.string.supporting_text_logout_account))
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