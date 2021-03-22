package com.benmohammad.mvitasks.model

sealed class TaskEditorState {
    object Closed : TaskEditorState() {
        fun addTask(task: Task) = Editing(task, true)
        fun editTask(task: Task) = Editing(task)
    }

    data class Editing(val task: Task, val adding: Boolean = false): TaskEditorState() {
        fun edit(block: Task.() -> Task) = copy(task = task.block())
        fun save() = Saving(task)
        fun delete() = Deleting(task.id)
        fun cancel() = Closed
    }

    data class Saving(val task: Task): TaskEditorState() {
        fun saved() = Closed
    }

    data class Deleting(val  taskId: String): TaskEditorState() {
        fun deleted() = Closed
    }


}
