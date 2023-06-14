package com.example.application

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.application.databinding.FragmentLockerPageBinding
import com.example.application.databinding.FragmentMyLocksBinding

class LockerPageFragment : Fragment() {
    // IDLE is closed - should come from a flag/request
    private var state : Boolean = false

    private lateinit var name : String
    private lateinit var lockID : String
    private lateinit var username : String

    private var _binding : FragmentLockerPageBinding? = null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            name = it.getString("name").toString()
            lockID = it.getInt("lockID").toString()
            username = it.getString("username").toString()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLockerPageBinding.inflate(inflater,container,false)

        /////////////////////// [REVER]!! /////////////////////
        binding.lockerName.text = name
        lockID = lockID.padStart(5, '0')
        val idLock = "#$lockID"
        binding.lockerUserIdentifier.text = idLock
        /////////////////////// [REVER]!! /////////////////////

        setLockState(binding.imageView)

        return binding.root
    }

    lateinit var botsheetAccessFragment : BotsheetAccessFragment
    lateinit var botsheetUserFragment : BotsheetUserFragment
    lateinit var botsheetPinFragment: UpdatePinFragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val imageView: ImageView = binding.imageView
        imageView.setOnClickListener{
            setLockState(imageView)
        }

        binding.moreInfoAccesses.setOnClickListener {
            botsheetAccessFragment = BotsheetAccessFragment(lockID.toInt())
            botsheetAccessFragment.show(childFragmentManager,botsheetAccessFragment.tag)
        }

        binding.moreInfoNumber.setOnClickListener {
            botsheetUserFragment = BotsheetUserFragment(lockID.toInt())
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

//    private fun void showBottomSheet{
//        final dialog : Dialog = new DialogFragment(context)
//
//
//    }
}