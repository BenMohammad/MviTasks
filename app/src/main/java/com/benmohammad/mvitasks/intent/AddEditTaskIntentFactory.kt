package com.benmohammad.mvitasks.intent

import com.benmohammad.mvitasks.model.Task
import com.benmohammad.mvitasks.model.TaskEditorModelStore
import com.benmohammad.mvitasks.model.TaskEditorState
import com.benmohammad.mvitasks.model.TasksModelStore
import com.benmohammad.mvitasks.view.addedittask.AddEditTaskViewEvent
import java.lang.IllegalStateException
import java.util.concurrent.ConcurrentSkipListMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddEditTaskIntentFactory @Inject constructor(
        private val taskEditorModelStore: TaskEditorModelStore,
        private val tasksModelStore: TasksModelStore
){

    fun process(viewEvent: AddEditTaskViewEvent) {
        taskEditorModelStore.process(toIntent(viewEvent))
    }

    private fun toIntent(viewEvent: AddEditTaskViewEvent): Intent<TaskEditorState> {
        return when(viewEvent) {
            is AddEditTaskViewEvent.TitleChange -> buildEditTitleIntent(viewEvent)
            is AddEditTaskViewEvent.DescriptionChange -> buildEditDescriptionIntent(viewEvent)
            AddEditTaskViewEvent.SaveTaskClick -> buildSaveIntent()
            AddEditTaskViewEvent.DeleteTaskClick -> buildDeleteClick()
            AddEditTaskViewEvent.CancelTaskClick -> buildCancelClick()
        }
    }


    private fun buildSaveIntent() = editorIntent<TaskEditorState.Editing> {
        save().run {
            val intent = TasksIntentFactory.buildAddOrUpdateTaskIntent(task)
            tasksModelStore.process(intent)
            saved()
        }
    }

    private fun buildDeleteClick() = editorIntent<TaskEditorState.Editing> {
        delete().run {
            val intent = TasksIntentFactory.buildDeleteTaskIntent(taskId)
            tasksModelStore.process(intent)
            deleted()
        }
    }

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

        private fun buildEditTitleIntent(viewEvent: AddEditTaskViewEvent.TitleChange) = editorIntent<TaskEditorState.Editing> {
            edit { copy(title = viewEvent.title) }
        }


        private fun buildEditDescriptionIntent(viewEvent: AddEditTaskViewEvent.DescriptionChange) = editorIntent<TaskEditorState.Editing> {
            edit { copy(description = viewEvent.description) }
        }

        private fun buildCancelClick() = editorIntent<TaskEditorState.Editing> { cancel() }
    }


}