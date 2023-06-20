package com.example.application

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.application.data.UserDBSingleton
import com.example.application.data.user.UserDao
import com.example.application.databinding.FragmentChangeUsernameBinding
import com.example.application.model.AppViewModel
import com.example.myapplication.functions.serverConnectionFunctions
import kotlinx.coroutines.launch

class ChangeUsernameFragment : Fragment() {
    private var _binding : FragmentChangeUsernameBinding? = null

    private val binding get() = _binding!!

    private lateinit var previousUsername : String
    private val sharedViewModel: AppViewModel by activityViewModels()

    private val functionConnection = serverConnectionFunctions()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            previousUsername = it.getString("previousUsername").toString()
        }

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentChangeUsernameBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userDatabase = UserDBSingleton.getInstance(requireContext())
        val userDao: UserDao = userDatabase!!.getAppDatabase().userDao()
        var userId: Int = 0

        binding.changeusernamebutto.setOnClickListener {
            val newUsername = binding.usernameText.text.toString()

            viewLifecycleOwner.lifecycleScope.launch {

                userId=userDao.getUserIdByUsername(previousUsername)

                if (newUsername.isEmpty()) {
                    Toast.makeText(context, "Error: Fill all entries", Toast.LENGTH_SHORT).show()
                }else if (newUsername==previousUsername)
                {
                    Toast.makeText(context, "Error: New username should be different from the previous", Toast.LENGTH_SHORT).show()
                } else if (newUsername.length > 18)
                {
                    Toast.makeText(context, "Error: New username must have 18 or less characters", Toast.LENGTH_SHORT).show()
                } else if ( !functionConnection.changeUsername(newUsername,previousUsername) ) { // Melhorar condicoes e feedback para o utilizador
                    Toast.makeText(context, "Error: Server wasn't able to change username", Toast.LENGTH_SHORT)
                        .show()
                }
                else {
                    sharedViewModel.setUsername(newUsername)
                    userDao.updateUsername(userId, newUsername)
                    Toast.makeText(context, "SUCCESS: Your username was updated", Toast.LENGTH_SHORT)
                        .show()

                    binding.usernameText.text?.clear()

                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}