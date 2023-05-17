package com.example.application

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.application.databinding.FragmentCreateAccountBinding
import com.example.application.databinding.FragmentRecoverPasswordBinding


class RecoverPasswordFragment : Fragment() {
    private var _binding : FragmentRecoverPasswordBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentRecoverPasswordBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.goBack.setOnClickListener {
            findNavController().navigate(R.id.action_recoverPasswordFragment_to_startFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}