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
import com.example.application.databinding.FragmentChangeEmailBinding
import com.example.application.databinding.FragmentNotificationsBinding
import kotlinx.coroutines.launch


class ChangeEmailFragment : Fragment() {
    private var _binding : FragmentChangeEmailBinding? = null

    private val binding get() = _binding!!

    private lateinit var username : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            username = it.getString("username").toString()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentChangeEmailBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userDatabase = UserDBSingleton.getInstance(requireContext())
        val userDao: UserDao = userDatabase!!.getAppDatabase().userDao()
        var userId: Int = 0

        binding.changeemailbutton.setOnClickListener {
            val oldEmail = binding.oldEmailText.text.toString()
            val newEmail = binding.newEmailText.text.toString()


            viewLifecycleOwner.lifecycleScope.launch {
                userId=userDao.getUserIdByUsername(username)
                val userEmail = userDao.getEmailByUserId(userId)

                if (oldEmail.isEmpty() ||
                    newEmail.isEmpty()
                ) {
                    Toast.makeText(context, "Error: Fill all entries", Toast.LENGTH_SHORT).show()
                } else if (oldEmail != userEmail) {
                    Toast.makeText(context, "Error: Old e-mail is not correct", Toast.LENGTH_SHORT)
                        .show()
                }  /*else if ( /* VERFICAR NO SERVIDOR SE EXISTE UM EMAIL IGUAL NA DB DO SERVIDOR */ ) {
                    Toast.makeText(context, "Error: This e-mail is already registered", Toast.LENGTH_SHORT)
                        .show()
                } */
                else {
                    userDao.updateEmail(userId, newEmail)
                    Toast.makeText(context, "SUCCESS: Your e-mail was updated", Toast.LENGTH_SHORT)
                        .show()

                    binding.oldEmailText.text?.clear()
                    binding.newEmailText.text?.clear()

                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}