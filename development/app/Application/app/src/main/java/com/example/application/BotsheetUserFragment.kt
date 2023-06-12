package com.example.application

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.application.adapter.InfoAccountAdapter
import com.example.application.data.LockDataSource
import com.example.application.data.UserDataSource
import com.example.application.databinding.FragmentBotsheetUserBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BotsheetUserFragment : BottomSheetDialogFragment() {
    private var _binding : FragmentBotsheetUserBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentBotsheetUserBinding.inflate(inflater,container,false)
        return binding.root    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val lockList = UserDataSource().loadUserInfo((requireContext()))
        val itemAdapter = InfoAccountAdapter(lockList)

        val recyclerView: RecyclerView =view.findViewById(R.id.recycler_view_access)
        recyclerView.layoutManager = LinearLayoutManager(context)

        recyclerView.adapter = itemAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}