package com.benmohammad.mvitasks.model

import java.util.*

fun main() {
    val task = Task(title = "Milk", completed = false)
    val updatedTask = task.copy(completed = true)
    println("final result: $updatedTask")
}

data class Task(
        val id: String = UUID.randomUUID().toString(),
        val lastUpdate: Long = 1,
        val title: String = "New Task",
        val description: String = "",
        val completed: Boolean = false
)

enum class FilterType {
    ANY,
    ACTIVE,
    COMPLETE;

    fun filter(task: Task): Boolean {
        return when (this) {
            ANY -> true
            ACTIVE -> !task.completed
            COMPLETE -> task.completed
        }
    }
}

sealed class SyncState {
    object IDLE: SyncState() {
        override fun toString(): String {
            return "IDLE"
        }

        data class PROCESS(val type: Type, val cancel: () -> Unit): SyncState() {
            enum class Type {
                REFRESH, CHECK
            }
        }
        data class ERROR(val throwable: Throwable): SyncState()
    }

    data class TasksState(
            val tasks: List<Task>,
            val filter: FilterType,
            val syncState: SyncState
    ) {
        fun filteredTasks(): List<Task> {
            return tasks.filter { task -> filter.filter(task) }
        }
    }
}

