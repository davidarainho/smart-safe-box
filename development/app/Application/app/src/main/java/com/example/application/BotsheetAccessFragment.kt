package com.example.application

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.application.adapter.AccessHistoryAdapter
import com.example.application.data.AccessesDataSource
import com.example.application.data.LockDataSource
import com.example.application.databinding.FragmentBotsheetAccessBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BotsheetAccessFragment(private val lockID : Int) : BottomSheetDialogFragment() {
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

        val lockList = AccessesDataSource().loadAccesseslockers(requireContext(), lockID)
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