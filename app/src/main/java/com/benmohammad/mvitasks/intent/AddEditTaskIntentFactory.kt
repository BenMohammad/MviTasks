package com.benmohammad.mvitasks.intent

import com.benmohammad.mvitasks.model.Task
import com.benmohammad.mvitasks.model.TaskEditorModelStore
import com.benmohammad.mvitasks.model.TaskEditorState
import com.benmohammad.mvitasks.model.TasksModelStore
import java.lang.IllegalStateException
import java.util.concurrent.ConcurrentSkipListMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddEditTaskIntentFactory @Inject constructor(
        private val taskEditorModelStore: TaskEditorModelStore,
        private val tasksModelStore: TasksModelStore
){

    companion object {
        inline fun <reified S : TaskEditorState> editorIntent (
                crossinline block: S.() -> TaskEditorState
        ): Intent<TaskEditorState> {
            return intent {
                (this as? S)?.block()
                        ?: throw IllegalStateException("editor Intent encountered a problem")
            }
        }

        fun buildAddTaskIntent(task: Task) = editorIntent<TaskEditorState.Closed> { addTask(task) }
        fun buildEditTaskIntent(task: Task) = editorIntent<TaskEditorState.Closed> { editTask(task) }
    }


}