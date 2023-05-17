package com.example.application

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.application.adapter.ItemAdapter
import com.example.application.data.Datasource
import com.example.application.databinding.FragmentMyLocksBinding
import com.example.application.model.Affirmation

class MyLocksFragment : Fragment() {
    private var _binding : FragmentMyLocksBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMyLocksBinding.inflate(inflater,container,false)
        return binding.root
    }

}