package io.github.secondbrainplanner

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TaskViewModel : ViewModel() {
    private val _tasks = MutableLiveData<List<Task>>().apply { value = mutableListOf() }
    val tasks: LiveData<List<Task>> = _tasks

    fun addTask(task: Task) {
        val currentList = _tasks.value?.toMutableList() ?: mutableListOf()
        currentList.add(task)
        _tasks.value = currentList
    }
}
