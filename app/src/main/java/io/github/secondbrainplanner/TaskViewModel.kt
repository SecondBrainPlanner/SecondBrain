package io.github.secondbrainplanner

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class TaskViewModel : ViewModel() {
    var name = MutableLiveData<String>()
    var description = MutableLiveData<String>()
}