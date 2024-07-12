package io.github.secondbrainplanner

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.github.secondbrainplanner.databinding.FragmentNewTaskSheetBinding

class NewTaskSheet : BottomSheetDialogFragment()
{
   private lateinit var binding: FragmentNewTaskSheetBinding
   private lateinit var taskViewModel: TaskViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity()
        taskViewModel = ViewModelProvider(activity).get(TaskViewModel::class.java)
        binding.newTaskAddButton.setOnClickListener {
            saveAction()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentNewTaskSheetBinding.inflate(inflater, container, false)
        return binding.root
    }


    private fun saveAction()
    {
        taskViewModel.name.value = binding.newTaskName.text.toString()
        taskViewModel.description.value = binding.newTaskDescription.text.toString()
        binding.newTaskName.setText("")
        binding.newTaskDescription.setText("")
        dismiss()
    }


}