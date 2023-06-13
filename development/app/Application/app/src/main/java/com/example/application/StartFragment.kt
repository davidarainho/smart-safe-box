package com.example.application

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.application.data.lock.LockDao
import com.example.application.data.user.UserDao
import com.example.application.databinding.FragmentStartBinding


class StartFragment : Fragment() {
    private var _binding : FragmentStartBinding? = null

    private val binding get() = _binding!!

    private lateinit var userDao: UserDao
    private lateinit var lockDao: LockDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentStartBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.createAccount.setOnClickListener {
            findNavController().navigate(R.id.action_startFragment_to_createAccountFragment)
        }

        binding.forgotPassword.setOnClickListener {
            findNavController().navigate(R.id.action_startFragment_to_recoverPasswordFragment)
        }

        var validAccount : Boolean = false
        var username : String?
        var password : String?
        binding.signIn.setOnClickListener{
            username = binding.usernameText.text.toString()
            password = binding.passwordText.text.toString()

            // Miguel - chamar a funcao autenticacao
            // recebe a informacao, transfere para a base de dados, altera validAccount para true
            if (username == "teste" && password == "pass"){
                validAccount = true
            }


            // se flag correta passo para o proximo
            if (validAccount){
                validAccount = false
                val action = StartFragmentDirections.actionStartFragmentToMainAppActivity(userName = username!!) //userName = "cenas"
                binding.signIn.findNavController().navigate(action)
            }

        }
        // No Dao
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}