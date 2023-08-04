package com.dicoding.todoapp.ui.detail

import android.os.Bundle
import android.text.Editable
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.todoapp.R
import com.dicoding.todoapp.data.Task
import com.dicoding.todoapp.ui.ViewModelFactory
import com.dicoding.todoapp.utils.DateConverter
import com.dicoding.todoapp.utils.TASK_ID
import com.google.android.material.textfield.TextInputEditText

class DetailTaskActivity : AppCompatActivity() {
    private lateinit var viewModel : DetailTaskViewModel
    private lateinit var title : TextInputEditText
    private lateinit var description : TextInputEditText
    private lateinit var dueDate : TextInputEditText
    lateinit var task : Task

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)

        val id = intent.getIntExtra(TASK_ID, 0)

        var factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[DetailTaskViewModel::class.java]

        viewModel.setTaskId(id)

        //TODO 11 : Show detail task and implement delete action
        title = findViewById(R.id.detail_ed_title)
        description = findViewById(R.id.detail_ed_description)
        dueDate = findViewById(R.id.detail_ed_due_date)

        viewModel.task.observe(this) { task ->
            if (task != null) {
                title.text = Editable.Factory.getInstance().newEditable(task.title)
                description.text = Editable.Factory.getInstance().newEditable(task.description)
                dueDate.text = Editable.Factory.getInstance()
                    .newEditable(DateConverter.convertMillisToString(task.dueDateMillis))
            }
        }

        findViewById<Button>(R.id.btn_delete_task).setOnClickListener {
            viewModel.deleteTask()
            onBackPressed()
            finish()
        }
    }
}