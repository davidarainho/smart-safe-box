package com.example.application

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.application.databinding.FragmentChangeLockNameBinding
import com.example.application.databinding.FragmentNotificationsBinding


class ChangeLockNameFragment : Fragment() {
    private var _binding : FragmentChangeLockNameBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentChangeLockNameBinding.inflate(inflater,container,false)

        val lockIds = resources.getStringArray(R.array.array_lock_id)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.drop_down_item, lockIds)
        binding.dropdownItems.setAdapter(arrayAdapter)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}