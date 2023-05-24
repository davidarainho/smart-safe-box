package com.example.application

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.application.databinding.FragmentBotsheetAccessBinding
import com.example.application.databinding.FragmentLockerPageBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BotsheetAccessFragment : BottomSheetDialogFragment() {
    private var _binding : FragmentBotsheetAccessBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentBotsheetAccessBinding.inflate(inflater,container,false)
        return binding.root    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}