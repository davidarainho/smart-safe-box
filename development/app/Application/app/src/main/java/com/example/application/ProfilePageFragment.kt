package com.example.application

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigator
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.application.data.LockDBSingleton
import com.example.application.data.UserAndLock.UserAndLockDao
import com.example.application.data.UserAndLockDBSingleton
import com.example.application.data.UserDBSingleton
import com.example.application.data.lock.LockDao
import com.example.application.data.user.UserDao
import com.example.application.databinding.FragmentProfilePageBinding
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

    private lateinit var name : String
    private val args by navArgs<ProfilePageFragmentArgs>()

//    private lateinit var name: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            name = it.getString("userName").toString()
        }
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

        println(name)
        //val username = args.userName
        //println(username)

        binding.changePassword.setOnClickListener {
            findNavController().navigate(R.id.action_profilePageFragment_to_changePasswordFragment)
        }

        binding.changeUsername.setOnClickListener {
            findNavController().navigate(R.id.action_profilePageFragment_to_changeUsernameFragment)
        }

       binding.logoutButton.setOnClickListener {
//            // [REVER] - possivelmente libertar memoria e outras cenas
//            //activity?.finish()
//            findNavController().navigate(R.id.xml)

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