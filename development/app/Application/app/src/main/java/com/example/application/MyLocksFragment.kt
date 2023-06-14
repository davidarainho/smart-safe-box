package com.example.application

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.application.adapter.ItemAdapter
import com.example.application.data.LockDataSource
import com.example.application.data.lock.Lock
import com.example.application.databinding.FragmentMyLocksBinding
import com.example.application.model.AppViewModel

class MyLocksFragment : Fragment() {
    private var _binding : FragmentMyLocksBinding? = null

    private val binding get() = _binding!!

    private lateinit var username: String

    private val sharedViewModel: AppViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            username = it.getString("userName").toString()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMyLocksBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel.setUsername(username)

        var myList: List<Lock>? = null
        val lockList = LockDataSource().loadUserlockers(requireContext(), username)
        val itemAdapter = ItemAdapter(lockList, username)

        val recyclerView:RecyclerView=view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        recyclerView.adapter = itemAdapter

        val search : EditText = view.findViewById(R.id.username_text)
        search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (lockList != null) {
                    myList = if(s.isNotEmpty()) {
                        lockList.filter { l -> l.lock_name.contains(s.toString(), ignoreCase = true) }
                    }else{
                        lockList
                    }
                }
                recyclerView.adapter = ItemAdapter(myList, username)
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}