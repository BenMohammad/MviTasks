package com.benmohammad.mvitasks.view.tasks

import com.benmohammad.mvitasks.model.Task

sealed class TasksViewEvent {
    object NewTaskClick: TasksViewEvent()
    object FilterTypeClick: TasksViewEvent()
    object ClearCompletedClick: TasksViewEvent()
    object RefreshTaskClick: TasksViewEvent()
    object RefreshTaskSwipe: TasksViewEvent()
    data class CompleteTaskClick(val task: Task, val checked: Boolean): TasksViewEvent()
    data class EditTaskClick(val task: Task): TasksViewEvent()
}
