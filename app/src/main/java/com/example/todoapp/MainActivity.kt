package com.example.todoapp
import android.app.AlertDialog
import android.app.ProgressDialog.show
import android.widget.Toast
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapp.databinding.ActivityMainBinding
import com.example.todoapp.ui.TaskAdapter
import com.example.todoapp.ui.ViewModel.TaskViewModel
import com.example.todoapp.data.entity.Task
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: TaskViewModel by viewModels()
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupObservers()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(
            onTaskCheckedChanged = { task: Task ->
                viewModel.toggleTaskCompletion(task)
            },
            onTaskDeleted = { task: Task ->
                viewModel.deleteTask(task)
            }
        )
        binding.recyclerViewTasks.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = taskAdapter
            // Assuming this is inside a task update method, like when an item is clicked or completed:


        }
    }

    private fun setupObservers() {
        viewModel.allTasks.observe(this) { tasks: List<Task> ->
            taskAdapter.submitList(tasks)
        }
    }

    private fun setupClickListeners() {
        binding.addButton.setOnClickListener {
            showAddTaskDialog()
        }
    }


    private fun showAddTaskDialog() {
        val view = layoutInflater.inflate(R.layout.add_item, null) // Inflate the layout

        val titleEdit = view.findViewById<TextInputEditText>(R.id.titleEdit)
        val descriptionEdit = view.findViewById<TextInputEditText>(R.id.descriptionEdit)

        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle("Add Task")
            .setView(view) // Set the inflated layout as the dialog's view
            .setPositiveButton("Add") { dialogInterface, _ ->
                val title = titleEdit?.text.toString().trim()
                val description = descriptionEdit?.text.toString().trim()

                if (title.isNotEmpty()) {
                    viewModel.addTask(title, description)
                    Snackbar.make(binding.root, "Task added", Snackbar.LENGTH_SHORT).apply {
                        setBackgroundTint(ContextCompat.getColor(this@MainActivity, R.color.purple_box))
                        setTextColor(ContextCompat.getColor(this@MainActivity, R.color.white))
                        show()
                    }

                }
                dialogInterface.dismiss()
            }
            .setNegativeButton("Cancel") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }
}