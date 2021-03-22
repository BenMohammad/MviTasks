package com.benmohammad.mvitasks.view.tasks

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.benmohammad.mvitasks.model.Task
import com.benmohammad.mvitasks.view.EventObservable
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.checkedChanges
import io.reactivex.Observable
import kotlinx.android.synthetic.main.task_item.view.*

class TaskViewHolder(view: View): RecyclerView.ViewHolder(view), EventObservable<TasksViewEvent> {
    private lateinit var currentTask: Task

    fun bind(task: Task) {
        currentTask = task
        itemView.title.text = task.title
        itemView.completeCheckBox.isChecked = task.completed
    }

    override fun events(): Observable<TasksViewEvent> {
        return Observable.merge(
                itemView.completeCheckBox.checkedChanges().skipInitialValue().map {
                    checked ->
                            TasksViewEvent.CompleteTaskClick(currentTask,checked)
                },
                itemView.title.clicks().map {
                    TasksViewEvent.EditTaskClick(currentTask)
                }


        )
    }
}