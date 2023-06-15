package com.example.application

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.application.data.LockDBSingleton
import com.example.application.data.UserAndLock.UserAndLock
import com.example.application.data.UserAndLock.UserAndLockDao
import com.example.application.data.UserAndLock.UserAndLockDatabase
import com.example.application.data.UserAndLockDBSingleton
import com.example.application.data.UserDBSingleton
import com.example.application.data.lock.Lock
import com.example.application.data.lock.LockDao
import com.example.application.data.user.User
import com.example.application.data.user.UserDatabase
import com.example.application.data.user.UserDao
import com.example.application.databinding.FragmentCreateAccountBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class CreateAccountFragment : Fragment() {
    private var _binding : FragmentCreateAccountBinding? = null

    private val binding get() = _binding!!

    //private lateinit var userDao: UserDao


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCreateAccountBinding.inflate(inflater,container,false)
        return binding.root
    }

    private lateinit var lockDao: LockDao
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //userDao = UserDatabase.getDatabase(requireContext()).userDao()

        binding.hasAccount.setOnClickListener {
            findNavController().navigate(R.id.action_createAccountFragment_to_startFragment)
        }


        // FAZER ISTO PARA CADA FRAGEMENTO ONDE VAMOS CHAMAR UMA FUNÇÃO DA BASE DE DADOS

        val lockDatabase = LockDBSingleton.getInstance(requireContext())
        val lockDao: LockDao? = lockDatabase!!.getAppDatabase().lockDao()

        val userDatabase = UserDBSingleton.getInstance(requireContext())
        val userDao: UserDao = userDatabase!!.getAppDatabase().userDao()

        val userAndLockDatabase = UserAndLockDBSingleton.getInstance(requireContext())
        val userLockDao: UserAndLockDao? = userAndLockDatabase!!.getAppDatabase().userAndLockDao()



        var flagAllowNewAccount : Boolean = false

//        val lock = Lock(lock_name="GYM", last_access= "2022-06-31", user_last_access =  "Logan", number_of_users = 2,comment="New gym lock", eKey = null, lock_state = "open", lock_id=12)
//        val  lock1= Lock(lock_name="HOME", last_access="2023-04-10,2022-09-10,2022-09-04", user_last_access =  "Logan,Manuel,Pedro", number_of_users = 3,comment="Garage", eKey = null, lock_state = "open", lock_id=14)
//
//        val user1= User(username = "pedro01", email= "pedro@gmail.com", allow_notifications = 1, password = "wasd", user_id = 5656)
//        val user2= User(username = "miguelAlmeida", email= "miguel@gmail.com", allow_notifications = 1, password = "wasd", user_id = 7777)


        var username : String
        var email : String
        var password : String
        var passwordConfirmed : String
        val allowNotifications = 1
        binding.signUp.setOnClickListener {
            username = binding.usernameText.text.toString()
            email = binding.emailText.text.toString()
            password = binding.passwordText.text.toString()
            passwordConfirmed = binding.passwordConfirmationText.text.toString()
            if(username.isEmpty() ||
                email.isEmpty() ||
                password.isEmpty() ||
                passwordConfirmed.isEmpty()){
                // Mostrar erro
                Toast.makeText(context, "Error: Fill all entries", Toast.LENGTH_SHORT).show()
            }else if(password != passwordConfirmed){
                // Mostrar erro
                Toast.makeText(context, "Error: password and confirmation don't match", Toast.LENGTH_SHORT).show()
            }else if(!binding.checkBox.isChecked){
                // Mostrar erro
                Toast.makeText(context, "Error: Check Box", Toast.LENGTH_SHORT).show()
            } else if (!validateEmail(email)) {
                Toast.makeText(context, "Error: invalid email format", Toast.LENGTH_SHORT).show()
                binding.usernameText.text?.clear()
                binding.emailText.text?.clear()
                binding.passwordText.text?.clear()
                binding.passwordConfirmationText.text?.clear()
            } else{
                flagAllowNewAccount = true
            }

            ////////// Verificar se e' uma conta permitida [Miguel] //////////

            //val user = User(username, email, password, allowNotifications, userid)


            GlobalScope.launch {
                viewLifecycleOwner.lifecycleScope.launch {
                   //userDao.upsertUser(user)
                }
            }


            if(flagAllowNewAccount){
                // Mostrar algo que confirme o sucesso da criação da conta
                Toast.makeText(context, "SUCESSO: An email was sent to confirm", Toast.LENGTH_SHORT).show()
                flagAllowNewAccount = false
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun validateEmail(email: String): Boolean {
        val regexPattern = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        return regexPattern.matches(email)
    }

}