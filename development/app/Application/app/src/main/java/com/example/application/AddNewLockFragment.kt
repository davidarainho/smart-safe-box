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
import com.example.application.databinding.FragmentAddNewLockBinding
import com.example.application.databinding.FragmentChangeUsernameBinding
import kotlinx.coroutines.launch

class AddNewLockFragment : Fragment() {

    private var _binding : FragmentAddNewLockBinding? = null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddNewLockBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.addlockbutton.setOnClickListener {

            val lockCode = binding.lockCodeText.text.toString()

           // no caso de sucesso:
           // binding.lockCodeText.text?.clear()
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}