package com.example.application

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.application.data.UserAndLock.UserAndLockDao
import com.example.application.data.UserAndLockDBSingleton
import com.example.application.data.UserDBSingleton
import com.example.application.data.user.UserDao
import com.example.application.databinding.FragmentLockerPageBinding
import com.example.application.databinding.FragmentMyLocksBinding
import com.example.application.model.AppViewModel
import kotlinx.coroutines.runBlocking

class LockerPageFragment : Fragment() {
    // IDLE is closed - should come from a flag/request
    private var state : Boolean = false

    private lateinit var name : String
    private lateinit var lockID : String
    private lateinit var username : String

    private var _binding : FragmentLockerPageBinding? = null

    private val sharedViewModel: AppViewModel by activityViewModels()

    private val binding get() = _binding!!
    private var level : Int = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            name = it.getString("name").toString()
            lockID = it.getInt("lockID").toString()
            username = it.getString("username").toString()
        }

        sharedViewModel.setLockID(lockID.toInt())
        level = getPermissionLevel(username, lockID.toInt())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLockerPageBinding.inflate(inflater,container,false)


        binding.lockerName.text = name
        lockID = lockID.padStart(5, '0')
        val idLock = "#$lockID"
        binding.lockerUserIdentifier.text = idLock

        setLockState(binding.imageView)

        return binding.root
    }

    private lateinit var botsheetAccessFragment : BotsheetAccessFragment
    private lateinit var botsheetUserFragment : BotsheetUserFragment
    private lateinit var botsheetPinFragment: UpdatePinFragment
    private lateinit var botsheetShareLockFragment: BotsheetShareLockFragment


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val imageView: ImageView = binding.imageView

        imageView.setOnClickListener{
            setLockState(imageView)
        }

        binding.shareLock.setOnClickListener {
            if(level <= 2){
                botsheetShareLockFragment = BotsheetShareLockFragment(lockID.toInt())
                botsheetShareLockFragment.show(childFragmentManager,botsheetShareLockFragment.tag)
            }
        }

        binding.moreInfoAccesses.setOnClickListener {
            botsheetAccessFragment = BotsheetAccessFragment(lockID.toInt())
            botsheetAccessFragment.show(childFragmentManager,botsheetAccessFragment.tag)
        }

        binding.moreInfoNumber.setOnClickListener {
            botsheetUserFragment = BotsheetUserFragment(lockID.toInt(), username)
            botsheetUserFragment.show(childFragmentManager,botsheetUserFragment.tag)
        }

        binding.changePassword.setOnClickListener {
            botsheetPinFragment = UpdatePinFragment()
            botsheetPinFragment.show(childFragmentManager,botsheetPinFragment.tag)
        }

        binding.exit.setOnClickListener {
            val action = LockerPageFragmentDirections.actionLockerPageFragmentToMyLocksFragment(userName = username)
           // Navigate using that action
            binding.exit.findNavController().navigate(action)
            //findNavController().navigate(R.id.action_lockerPageFragment_to_myLocksFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getPermissionLevel(user : String, lockID : Int) : Int = runBlocking {
        val userDatabaseSingleton = UserDBSingleton.getInstance(requireContext())
        val userDao : UserDao = userDatabaseSingleton!!.getAppDatabase().userDao()

        val userAndLockDatabase = UserAndLockDBSingleton.getInstance(requireContext())
        val userLockDao : UserAndLockDao? = userAndLockDatabase!!.getAppDatabase().userAndLockDao()

        val userId : Int = userDao.getUserIdByUsername(user)

        var lev : Int = 3
        if (userLockDao != null){
            lev = userLockDao.getUserLockPermissionLevel(userId, lockID)
        }

        lev
    }

    private fun setLockState(imageView: ImageView){
        // state = Ir buscar valor do estado na base de dados
        // state == 1 fechado
        state = if(state){
            imageView.setImageResource(R.drawable.lock_closed)
            /// [REVER]!! /// Adicionar mais logica antes de prosseguir para o popup
            Toast.makeText(context, "Lock is now Closed", Toast.LENGTH_SHORT).show()
            false
        }else{
            imageView.setImageResource(R.drawable.lock_open)
            Toast.makeText(context, "Lock is now Open", Toast.LENGTH_SHORT).show()
            true
        }
    }

}