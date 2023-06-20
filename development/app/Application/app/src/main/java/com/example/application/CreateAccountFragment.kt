package com.example.application

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.room.PrimaryKey
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
import com.example.myapplication.functions.serverConnectionFunctions
import kotlinx.coroutines.*


class CreateAccountFragment : Fragment() {
    private var _binding : FragmentCreateAccountBinding? = null

    private val binding get() = _binding!!

    private val functionConnection = serverConnectionFunctions()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCreateAccountBinding.inflate(inflater,container,false)
        return binding.root
    }

    private lateinit var lockDao: LockDao
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.hasAccount.setOnClickListener {
            findNavController().navigate(R.id.action_createAccountFragment_to_startFragment)
        }


        val lockDatabase = LockDBSingleton.getInstance(requireContext())
        val lockDao: LockDao? = lockDatabase!!.getAppDatabase().lockDao()

        val userDatabase = UserDBSingleton.getInstance(requireContext())
        val userDao: UserDao = userDatabase!!.getAppDatabase().userDao()

        val userAndLockDatabase = UserAndLockDBSingleton.getInstance(requireContext())
        val userLockDao: UserAndLockDao? = userAndLockDatabase!!.getAppDatabase().userAndLockDao()

        var flagAllowNewAccount : Boolean = false

        val  lock1= Lock(lock_name="My Lock", last_access="2023-06-16", user_last_access =  "Beatriz", number_of_users = 3,comment="new lock at feup", eKey = null, lock_state = "open", lock_id=85)
        val  lock2= Lock(lock_name="FEUP", last_access="2023-06-16,2022-06-15", user_last_access =  "Francisco, Tomás", number_of_users = 3,comment="lock1", eKey = null, lock_state = "open", lock_id=82)
        val  lock3= Lock(lock_name="GRANDMA", last_access="2022-12-14", user_last_access =  "Manuel", number_of_users = 3,comment="at Porto", eKey = null, lock_state = "open", lock_id=96)
        val  lock4= Lock(lock_name="New Lock", last_access="2023-05-17,2022-07-05, 2022-07-05", user_last_access =  "Francisco, Manuel, Tomás", number_of_users = 3,comment=":)", eKey = null, lock_state = "open", lock_id=93)
        val  lock5= Lock(lock_name="House", last_access="2023-05-17", user_last_access =  "Miguel", number_of_users = 3,comment="<3", eKey = null, lock_state = "open", lock_id=103)

        val user2= User(username = "beatriz", email= "beatriz@gmail.com", allow_notifications = 1, password = "wasd", user_id = 8777)
        val user1= User(username = "Francisco", email= "francisco@gmail.com", allow_notifications = 1, password = "francisco", user_id = 3)

        val userLock2=UserAndLock(user_id=8777, lock_id=82, lock_access_pin="1598", permission_level = 3, userLockId = 10210)
        val userLock3=UserAndLock(user_id=8777, lock_id=96, lock_access_pin="1598", permission_level = 1, userLockId = 10310)
        val userLock4=UserAndLock(user_id=8777, lock_id=93, lock_access_pin="1598", permission_level = 1, userLockId = 10710)
        val userLock5=UserAndLock(user_id=8777, lock_id=103, lock_access_pin="1598", permission_level = 1, userLockId = 10910)


        var username : String
        var email : String
        var password : String
        var passwordConfirmed : String
        val allowNotifications = 1
        val userId= 1204
        var pin:  String

        binding.signUp.setOnClickListener {
            username = binding.usernameText.text.toString()
            email = binding.emailText.text.toString()
            password = binding.passwordText.text.toString()
            passwordConfirmed = binding.passwordConfirmationText.text.toString()
            pin=binding.pinText.text.toString()

            if(username.isEmpty() ||
                email.isEmpty() ||
                password.isEmpty() ||
                passwordConfirmed.isEmpty() || pin.isEmpty()){
                Toast.makeText(context, "Error: Fill all entries", Toast.LENGTH_SHORT).show()
            }else if(password != passwordConfirmed){
                Toast.makeText(context, "Error: password and confirmation don't match", Toast.LENGTH_SHORT).show()
            }/*else if(!binding.checkBox.isChecked){
                // Mostrar erro
                Toast.makeText(context, "Error: Check Box", Toast.LENGTH_SHORT).show()
            } */ else if(pin.length != 6){
                Toast.makeText(context, "Error: pin must have 6 numbers", Toast.LENGTH_SHORT).show()
            } else if (!validateEmail(email)) {
                Toast.makeText(context, "Error: invalid email format", Toast.LENGTH_SHORT).show()
            } else{
                val result = allowCreateAccount(username,password, email, pin)
                println(result)
                if(result == true) {
                    flagAllowNewAccount = true
                }else {
                    Toast.makeText(context, "Error: Wasn't able to create account", Toast.LENGTH_SHORT).show()
                }
            }

            if(flagAllowNewAccount){
                // Mostrar algo que confirme o sucesso da criação da conta
                Toast.makeText(context, "SUCESSO: An email was sent to confirm", Toast.LENGTH_SHORT).show()
                binding.usernameText.text?.clear()
                binding.emailText.text?.clear()
                binding.passwordText.text?.clear()
                binding.passwordConfirmationText.text?.clear()
                binding.pinText.text?.clear()
                flagAllowNewAccount = false
                findNavController().navigate(R.id.action_createAccountFragment_to_startFragment)
            }
        }
    }

    fun allowCreateAccount(username : String, password : String, email: String, pin : String) : Boolean? = runBlocking{
        var create : Boolean? = false
        viewLifecycleOwner.lifecycleScope.launch() {
            create = functionConnection.createAccount(username=username, password=password, email=email, pincode=pin)
        }
        println(create)
        create
    }

    fun validateEmail(email: String): Boolean {
        val regexPattern = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        return regexPattern.matches(email)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}