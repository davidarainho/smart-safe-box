package com.example.application

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.application.data.lock.LockDao
import com.example.application.data.user.UserDao
import com.example.application.databinding.FragmentStartBinding


class StartFragment : Fragment() {
    private var _binding : FragmentStartBinding? = null

    private val binding get() = _binding!!

    private lateinit var userDao: UserDao
    private lateinit var lockDao: LockDao

    var utilizadorIdentificador : Int? = null

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

//        userDao = UserDatabase.getDatabase(requireContext()).userDao()
//        lockDao = LockDatabase.getLockDatabase(requireContext()).lockDao()
//
//        val lock = Lock("Back Door", "2023-05-31", "John Doe", 1,comment="old", eKey = null, lock_state = "open")
//
//
//        val username = binding.usernameText.text.toString()
//        val password = binding.passwordText.text.toString()

        // para retornar o userID do user ativo
//        GlobalScope.launch {
//
//
////            lockDao.upsertLock(lock)
////            val lockid= lockDao.getFirstLockId()

//
////            utilizadorIdentificador = userDao.getUserIdByUsername(username)
////            println(utilizadorIdentificador)
//        }
        // guardar o userID do user que está logged in numa variavel local para saber

        binding.signIn.setOnClickListener{
            // Miguel - chamar a funcao autenticacao
            //val user = User("johnDoe", "john@example.com", "password", allow_notifications=1)

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