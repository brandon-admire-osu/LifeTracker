package com.example.lifetracker.ui

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lifetracker.data.TaskRecord
import com.example.lifetracker.data.TaskTemplate
import com.example.myapplication.R

class OverviewFragment : Fragment(R.layout.overview_fragment) {
    private val TAG = "OverviewFragment"

    // ViewModel and adapter containers
    private val viewModel: TaskViewModel by viewModels()
    private val taskAdapter = TaskAdapter(::onTaskItemClick,::onIconItemClick)

    // RecyclerView container
    private lateinit var taskListRV: RecyclerView


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up RecyclerView.
        taskListRV = view.findViewById(R.id.rv_task_list)
        taskListRV.layoutManager = LinearLayoutManager(requireContext())
        taskListRV.setHasFixedSize(true)
        taskListRV.adapter = taskAdapter

        // Call on fragment to put buttons in action bar
        setHasOptionsMenu(true)

        // Set observer for task data. If the taskTemplates list is not null, pass to UI
        viewModel.taskTemplates.observe(viewLifecycleOwner) { taskTemplates ->
            taskAdapter.updateTaskTemplates(taskTemplates)
            taskListRV.visibility = View.VISIBLE
            taskListRV.scrollToPosition(0)
        }

        // Set observer for record data. If the taskTemplates list is not null, pass to UI
        viewModel.taskRecords.observe(viewLifecycleOwner) { taskRecords ->
            taskAdapter.updateTaskRecords(taskRecords)
        }

        viewModel.latestRecordsFlow.observe(viewLifecycleOwner) { map ->
            taskAdapter.updateLatestRecords(map)
        }

//        viewModel.debugHardcode()
    }

    // Function called when task is clicked on from overview
    private fun onTaskItemClick(taskTemplate: TaskTemplate) {
        Log.d(TAG, "onTaskItemClick() called, task: $taskTemplate")
        val directions = OverviewFragmentDirections.navigateToTask(taskTemplate)
        findNavController().navigate(directions)
    }

    private fun onIconItemClick(taskTemplate: TaskTemplate) {
        viewModel.addTaskRecord(
            TaskRecord(
                System.currentTimeMillis(),
                taskTemplate.name,
                1
            )
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        Log.d(TAG, "${menu.size}")
        inflater.inflate(R.menu.activity_main,menu)
    }

    // Define Action Bar behavior
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
          R.id.add_task -> {
              val directions = OverviewFragmentDirections.navigateToAddTask()
              findNavController().navigate(directions)
              true
          }
            else -> return super.onOptionsItemSelected(item)
        }

    }
}