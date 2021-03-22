package com.benmohammad.mvitasks.view.tasks

import android.view.LayoutInflater
import android.view.View.inflate
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.benmohammad.mvitasks.R
import com.benmohammad.mvitasks.intent.Intent
import com.benmohammad.mvitasks.intent.TasksIntentFactory
import com.benmohammad.mvitasks.model.SyncState
import com.benmohammad.mvitasks.model.SyncState.TasksState
import com.benmohammad.mvitasks.model.Task
import com.benmohammad.mvitasks.model.TasksModelStore
import com.benmohammad.mvitasks.view.StateSubscriber
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign
import javax.inject.Inject

class TasksAdapter @Inject constructor(
        private val layoutInflater: LayoutInflater,
        private val tasksIntent: TasksIntentFactory,
        private val tasksModelStore: TasksModelStore
): RecyclerView.Adapter<TaskViewHolder>(), StateSubscriber<TasksState> {

    private lateinit var filteredTasks: List<Task>
    private var disposables = CompositeDisposable()

    init {
        setHasStableIds(true)
    }

    override fun Observable<TasksState>.subscribeToState(): Disposable {
        return this
                .map(TasksState::filteredTasks)
                .distinctUntilChanged()
                .subscribe{
                    updatedTasks -> filteredTasks = updatedTasks
                    notifyDataSetChanged()
                }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val inflateView = layoutInflater.inflate(R.layout.task_item, parent, false)
        return TaskViewHolder(inflateView).apply {
            events().subscribe(tasksIntent::process)
        }
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(filteredTasks[position])
    }

    override fun getItemCount(): Int {
        return filteredTasks.size
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        disposables += tasksModelStore.modelState().subscribeToState()
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        disposables.clear()
    }
}