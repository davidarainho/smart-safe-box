package com.example.application

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.application.data.UserDBSingleton
import com.example.application.data.user.UserDao
import com.example.application.databinding.FragmentChangeUsernameBinding
import kotlinx.coroutines.launch

class ChangeUsernameFragment : Fragment() {
    private var _binding : FragmentChangeUsernameBinding? = null

    private val binding get() = _binding!!

    private lateinit var previousUsername : String
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
                } /*else if ( /* VERFICAR NO SERVIDOR SE EXISTE UM USERNAME IGUAL NA DB DO SERVIDOR */ ) {
                    Toast.makeText(context, "Error: This username is already taken", Toast.LENGTH_SHORT)
                        .show()
                } */ else if (newUsername==previousUsername)
                {
                    Toast.makeText(context, "Error: New username should be different from the previous", Toast.LENGTH_SHORT).show()
                }

                else {
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