package com.example.application

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
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
import com.example.application.data.UserAndLock.UserAndLockDao
import com.example.application.data.UserAndLockDBSingleton
import com.example.application.data.UserDBSingleton
import com.example.application.data.lock.Lock
import com.example.application.data.user.UserDao
import com.example.application.databinding.FragmentMyLocksBinding
import com.example.application.model.AppViewModel
import kotlinx.coroutines.runBlocking

class MyLocksFragment : Fragment() {
    private var _binding : FragmentMyLocksBinding? = null

    private val binding get() = _binding!!

    private lateinit var username: String
    private val sharedViewModel: AppViewModel by activityViewModels() // Tem de estar aqui!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            username = it.getString("userName").toString()
        }

        sharedViewModel.setUsername(username)
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

        // Remove Back button option
        val callback = object : OnBackPressedCallback(true ) { override fun handleOnBackPressed(){}}
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,callback)

        // println(username)
        // Set string
        val count : String = lockCount(username).toString()
        binding.welcomeText.text = getString(R.string.active_locks, count)

        // Set List of Locks
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

    private fun lockCount(name : String) : Int? = runBlocking {
        val userDatabaseSingleton = UserDBSingleton.getInstance(requireContext())
        val userDao : UserDao = userDatabaseSingleton!!.getAppDatabase().userDao()

        val userAndLockDatabase = UserAndLockDBSingleton.getInstance(requireContext())
        val userLockDao : UserAndLockDao? = userAndLockDatabase!!.getAppDatabase().userAndLockDao()

        val idUser : Int = userDao.getUserIdByUsername(name)
        val count : Int? = userLockDao?.getLocksIDCountByUserId(idUser)

        count
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}