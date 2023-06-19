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
import com.example.application.data.LockDBSingleton
import com.example.application.data.UserAndLock.UserAndLockDao
import com.example.application.data.UserAndLockDBSingleton
import com.example.application.data.UserDBSingleton
import com.example.application.data.lock.LockDao
import com.example.application.data.user.UserDao
import com.example.application.databinding.FragmentLockerPageBinding
import com.example.application.databinding.FragmentMyLocksBinding
import com.example.application.model.AppViewModel
import com.example.myapplication.functions.serverConnectionFunctions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class LockerPageFragment : Fragment() {
    // IDLE is closed - should come from a flag/request
    private var state : Int = 0

    private lateinit var name : String
    private lateinit var lockID : String
    private lateinit var username : String

    private var _binding : FragmentLockerPageBinding? = null

    //private val functionConnection = serverConnectionFunctions()

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

        setLockState(binding.imageView, 0)

        return binding.root
    }

    private lateinit var botsheetAccessFragment : BotsheetAccessFragment
    private lateinit var botsheetUserFragment : BotsheetUserFragment
    //private lateinit var botsheetPinFragment: UpdatePinFragment
    private lateinit var botsheetShareLockFragment: BotsheetShareLockFragment
    private lateinit var botsheetChangeCommentFragment: BotsheetChangeCommentFragment


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val imageView: ImageView = binding.imageView

        imageView.setOnClickListener{
            // Colocar o pedido para abertura se sucesso e depois sim chamar a função
            setLockState(imageView, 1)
        }

        binding.shareLock.setOnClickListener {
            if(level <= 2){
                botsheetShareLockFragment = BotsheetShareLockFragment(lockID.toInt())
                botsheetShareLockFragment.show(childFragmentManager,botsheetShareLockFragment.tag)
            } else {
                Toast.makeText(context, "You don't have permission to share this lock", Toast.LENGTH_SHORT)
                .show()
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

        binding.updateComment.setOnClickListener {
            botsheetChangeCommentFragment = BotsheetChangeCommentFragment()
            botsheetChangeCommentFragment.show(childFragmentManager,botsheetChangeCommentFragment.tag)
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


    private fun setLockState(imageView: ImageView, open : Int) = runBlocking{
        val lockDatabaseSingleton = LockDBSingleton.getInstance(requireContext())
        val lockDao : LockDao? = lockDatabaseSingleton!!.getAppDatabase().lockDao()

        coroutineScope {
            state = if (lockDao?.getLockState()=="opened"){ 1 }else{ 0 }
        }

        if (state == 0 && open == 1){ state = 1 }

        //Verifica na base de dados
        if(state == 0){
            imageView.setImageResource(R.drawable.lock_closed)
            Toast.makeText(context, "Lock is Closed", Toast.LENGTH_SHORT).show()
        }else{
            imageView.setImageResource(R.drawable.lock_open)
            Toast.makeText(context, "Lock is Open", Toast.LENGTH_SHORT).show()
        }
    }

    private fun changeState() = runBlocking {
        withContext(Dispatchers.IO) {
            //functionConnection.openLocks(username)
        }
    }

    private fun getState() : Int = runBlocking{
        val lockDatabase = LockDBSingleton.getInstance(requireContext())
        val lockDao: LockDao? = lockDatabase!!.getAppDatabase().lockDao()

        var estado : Int = 0
        withContext(Dispatchers.IO) {
            if (lockDao != null) {
                //estado = lockDao.getLockStateOpen(lockID.toInt()).toInt()
            }
        }

        estado
    }

}