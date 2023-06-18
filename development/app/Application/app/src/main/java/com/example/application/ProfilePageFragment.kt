package com.example.application

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.application.data.LockDBSingleton
import com.example.application.data.UserAndLock.UserAndLockDao
import com.example.application.data.UserAndLockDBSingleton
import com.example.application.data.UserDBSingleton
import com.example.application.data.lock.LockDao
import com.example.application.data.user.UserDao
import com.example.application.databinding.FragmentProfilePageBinding
import com.example.application.model.AppViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.*

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
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProfilePageBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Obter valor do user neste fragmento
        //println(sharedViewModel.username.value)
        val lockDatabase = LockDBSingleton.getInstance(requireContext())
        val lockDao: LockDao? = lockDatabase!!.getAppDatabase().lockDao()

        val userDatabase = UserDBSingleton.getInstance(requireContext())
        val userDao: UserDao = userDatabase!!.getAppDatabase().userDao()

        val userAndLockDatabase = UserAndLockDBSingleton.getInstance(requireContext())
        val userLockDao: UserAndLockDao? = userAndLockDatabase!!.getAppDatabase().userAndLockDao()

        var userID : Int = userIDFetch(sharedViewModel.username.value.toString())

        binding.welcomeAccount.text = getString(R.string.welcome_to_account, sharedViewModel.username.value.toString())
        binding.yourEmail.text = getString(R.string.your_email_show, userID.toString())


        binding.changePassword.setOnClickListener {

            val action = ProfilePageFragmentDirections.actionProfilePageFragmentToChangePasswordFragment(username = sharedViewModel.username.value.toString())
            binding.changePassword.findNavController().navigate(action)
        }

        binding.changeUsername.setOnClickListener {

            val action = ProfilePageFragmentDirections.actionProfilePageFragmentToChangeUsernameFragment(previousUsername = sharedViewModel.username.value.toString())
            binding.changeUsername.findNavController().navigate(action)
        }


        binding.updatePin.setOnClickListener {
            val action = ProfilePageFragmentDirections.actionProfilePageFragmentToChangePinFragment(username1 = sharedViewModel.username.value.toString())
            binding.updatePin.findNavController().navigate(action)
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

                   // apaga os dados de todas as tabelas
//                   if (lockDao != null) {
//                       lockDao.deleteLockData()
//                   }
//                   userDao.deleteUserData()
//                  if (userLockDao != null) {
//                       userLockDao.deleteUserLockData()
//                   }
                   GlobalScope.launch{
                       deleteLockData();
                   }

                   activity?.finish()

               }
               .show()



        }

        binding.addNewLock.setOnClickListener {
            findNavController().navigate(R.id.action_profilePageFragment_to_addNewLockFragment)

        }
    }

    private fun userIDFetch(username : String) : Int = runBlocking {
        val userDatabase = UserDBSingleton.getInstance(requireContext())
        val userDao: UserDao = userDatabase!!.getAppDatabase().userDao()


        val userID : Int = userDao.getUserIdByUsername(username)

        userID
    }

     suspend fun deleteLockData() {
        val lockDatabase = LockDBSingleton.getInstance(requireContext())
        val lockDao: LockDao? = lockDatabase!!.getAppDatabase().lockDao()

        val userDatabase = UserDBSingleton.getInstance(requireContext())
        val userDao: UserDao = userDatabase!!.getAppDatabase().userDao()

        val userAndLockDatabase = UserAndLockDBSingleton.getInstance(requireContext())
        val userLockDao: UserAndLockDao? = userAndLockDatabase!!.getAppDatabase().userAndLockDao()

        withContext(Dispatchers.IO) {

            if (lockDao != null) {
                lockDao.deleteLockData()
            }
            userDao.deleteUserData()

            if (userLockDao != null) {
                userLockDao.deleteUserLockData()
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}