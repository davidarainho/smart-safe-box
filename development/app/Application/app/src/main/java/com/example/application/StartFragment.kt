package com.example.application

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.application.databinding.FragmentStartBinding


/**
 * A simple [Fragment] subclass.
 * Use the [StartFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StartFragment : Fragment() {
    private var _binding : FragmentStartBinding? = null

    private val binding get() = _binding!!

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

        binding.signIn.setOnClickListener{
            // Miguel - chamar a funcao autenticacao
            // usernameText.text.toString() == "batata@sopa.com" && passwordText.text.toString() == "qwerty"
            // se flag correta passo para o proximo
            findNavController().navigate(R.id.action_startFragment_to_mainAppActivity)
            // Sera que devo manter a terminacao dentro do fragmento? Talvez passar para a atividade
        }
        // No Dao
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}