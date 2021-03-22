package com.benmohammad.mvitasks.intent

import com.benmohammad.mvitasks.model.*
import com.benmohammad.mvitasks.model.SyncState.IDLE.PROCESS.Type.REFRESH
import com.benmohammad.mvitasks.model.SyncState.TasksState
import com.benmohammad.mvitasks.model.backend.TasksRestApi
import com.benmohammad.mvitasks.view.tasks.TasksViewEvent
import com.benmohammad.mvitasks.view.tasks.TasksViewEvent.*
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TasksIntentFactory @Inject constructor(
        private val tasksModelStore: TasksModelStore,
        private val tasksEditorModelStore: TaskEditorModelStore,
        private val taskRestApi: TasksRestApi
) {

    fun process(event: TasksViewEvent) {
        tasksModelStore.process(toIntent(event))
    }

    private fun toIntent(viewEvent: TasksViewEvent): Intent<TasksState> {
        return when(viewEvent) {
            ClearCompletedClick -> buildClearCompletedIntent()
            FilterTypeClick -> buildCycleFilterIntent()
            RefreshTaskClick , RefreshTaskSwipe -> buildReloadTaskIntent()
            NewTaskClick -> buildNewTaskIntent()
            is CompleteTaskClick -> buildCompleteTaskClick(viewEvent)
            is EditTaskClick -> buildEditTaskClick(viewEvent)
        }
    }

    private fun buildEditTaskClick(viewEvent: EditTaskClick): Intent<TasksState> {
        return sideEffect {
            assert (tasks.contains(viewEvent.task))

            val intent = AddEditTaskIntentFactory.buildEditTaskIntent(viewEvent.task)
            tasksEditorModelStore.process(intent)
        }
    }

    private fun buildNewTaskIntent(): Intent<TasksState> = sideEffect {
        val addIntent = AddEditTaskIntentFactory.buildAddTaskIntent(Task())
        tasksEditorModelStore.process(addIntent)
    }

    private fun buildCompleteTaskClick(viewEvent: CompleteTaskClick): Intent<TasksState> {
        return intent {
            val mutableList = tasks.toMutableList()
            mutableList[tasks.indexOf(viewEvent.task)] =
                    viewEvent.task.copy(completed = viewEvent.checked)

            copy(tasks = mutableList)
        }
    }

    private fun chainedIntent(block: TasksState.() -> TasksState) =
            tasksModelStore.process(intent(block))

    private fun buildReloadTaskIntent(): Intent<TasksState> {
        return intent {
            assert (syncState == SyncState.IDLE)
            fun retrofitSuccess(loadedTasks: List<Task>) = chainedIntent {
                assert(syncState is SyncState.IDLE.PROCESS && syncState.type == REFRESH)
                copy(tasks = loadedTasks, syncState = SyncState.IDLE)
            }

            fun retrofitError(throwable: Throwable) = chainedIntent {
                assert(syncState is SyncState.IDLE.PROCESS && syncState.type == REFRESH)
                copy(syncState = SyncState.IDLE.ERROR(throwable))
            }

            val disposable = taskRestApi.getTasks()
                    .map { it.values.toList() }
                    .subscribeOn(Schedulers.io())
                    .subscribe(::retrofitSuccess, ::retrofitError)

            copy(syncState = SyncState.IDLE.PROCESS(REFRESH, disposable::dispose))
        }
    }

    private fun buildCycleFilterIntent(): Intent<TasksState> {
        return intent {
            copy (
                filter = when (filter) {
                    FilterType.ANY -> FilterType.ACTIVE
                    FilterType.ACTIVE -> FilterType.COMPLETE
                    FilterType.COMPLETE -> FilterType.ANY
                })
            }
        }


    private fun buildClearCompletedIntent(): Intent<TasksState> {
        return intent {
            copy(tasks = tasks.filter {!it.completed}.toList())
        }
    }

    companion object {
        fun buildAddOrUpdateTaskIntent(task: Task): Intent<TasksState> = intent {
            tasks.toMutableList().let {
                newList ->
                newList.find {
                    task.id == it.id
                }?.let {
                    newList[newList.indexOf(it)] = task
                }?: newList.add(task)
                copy(tasks = newList)
            }
        }

        fun buildDeleteTaskIntent(taskId: String): Intent<TasksState> = intent {
            copy(tasks = tasks.toMutableList().apply {
                find { it.id == taskId }?.also { remove(it) }
            })
        }
    }
}