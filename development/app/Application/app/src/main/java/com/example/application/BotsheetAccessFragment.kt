package com.example.application

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.application.adapter.AccessHistoryAdapter
import com.example.application.adapter.InfoAccountAdapter
import com.example.application.data.DataInfo
import com.example.application.databinding.FragmentBotsheetAccessBinding
import com.example.application.databinding.FragmentLockerPageBinding
import com.example.application.model.AccountInfo
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var myList: List<AccountInfo>
        val lockList = DataInfo().loadUserInfo()
        val itemAdapter = AccessHistoryAdapter(lockList)

        val recyclerView: RecyclerView =view.findViewById(R.id.recycler_view_history)
        recyclerView.layoutManager = LinearLayoutManager(context)

        recyclerView.adapter = itemAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}