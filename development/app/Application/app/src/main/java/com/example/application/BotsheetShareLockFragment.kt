package com.example.application

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.application.databinding.FragmentBotsheetShareLockBinding
import com.example.application.model.AppViewModel
import com.example.myapplication.functions.serverConnectionFunctions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class BotsheetShareLockFragment(private val lockID: Int) : BottomSheetDialogFragment() {
    private var _binding: FragmentBotsheetShareLockBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: AppViewModel by activityViewModels()
    private val functionConnection = serverConnectionFunctions()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBotsheetShareLockBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var duration: String = ""
/*
        val durationList: Array<String> by lazy {
            resources.getStringArray(R.array.array_share_durarion)
        }

        val durationAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, durationList)
        binding.dropdownItems.setAdapter(durationAdapter)

        binding.dropdownItems.setOnItemClickListener { parent, _, position, id ->
            val selectedItem = parent.getItemAtPosition(position)
            duration = selectedItem.toString()
            // enviar a duração escolhida para o server
        }*/

        binding.sharebutton.setOnClickListener {
            val userToAdd = binding.usernameText.text.toString()

            if (userToAdd.isEmpty() /*|| duration==""*/) {
                Toast.makeText(context, "Error: Fill all entries", Toast.LENGTH_SHORT).show()
            } else if (!successShared(sharedViewModel.username.value.toString(),userToAdd, sharedViewModel.lockID.value.toString())) {
                Toast.makeText(context, "Error: Wasn't able to share Lock", Toast.LENGTH_SHORT).show()
            } else {
                // se tiver sucesso
                Toast.makeText(context, "SUCCESS: Lock was shared", Toast.LENGTH_SHORT).show()
                binding.usernameText.text?.clear()
            }
        }
    }

    private fun successShared(username : String, userToAdd : String, lock_id : String) : Boolean = runBlocking{
        var success : Boolean = false
        withContext(Dispatchers.IO) {
             success = functionConnection.shareLock(username, userToAdd, lock_id)
        }
        success
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
