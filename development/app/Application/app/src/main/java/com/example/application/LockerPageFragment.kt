package com.example.application

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.example.application.databinding.FragmentLockerPageBinding
import com.example.application.databinding.FragmentMyLocksBinding

class LockerPageFragment : Fragment() {
    // IDLE is closed - should come from a flag/request
    private var state : Boolean = false

    private lateinit var name : String
    private lateinit var lockID : String

    private var _binding : FragmentLockerPageBinding? = null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            name = it.getString("name").toString()
            lockID = it.getInt("lockID").toString()
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
            botsheetAccessFragment = BotsheetAccessFragment()
            botsheetAccessFragment.show(childFragmentManager,botsheetAccessFragment.tag)
        }

        binding.moreInfoNumber.setOnClickListener {
            botsheetUserFragment = BotsheetUserFragment()
            botsheetUserFragment.show(childFragmentManager,botsheetUserFragment.tag)
        }

        binding.changePassword.setOnClickListener {
            botsheetPinFragment = UpdatePinFragment()
            botsheetPinFragment.show(childFragmentManager,botsheetPinFragment.tag)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    private fun setLockState(imageView: ImageView){
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