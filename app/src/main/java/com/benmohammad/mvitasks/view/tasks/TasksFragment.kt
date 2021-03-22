package com.benmohammad.mvitasks.view.tasks

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.benmohammad.mvitasks.R
import com.benmohammad.mvitasks.intent.TasksIntentFactory
import com.benmohammad.mvitasks.model.FilterType
import com.benmohammad.mvitasks.model.SyncState
import com.benmohammad.mvitasks.model.SyncState.IDLE.ERROR
import com.benmohammad.mvitasks.model.SyncState.IDLE.PROCESS
import com.benmohammad.mvitasks.model.SyncState.TasksState
import com.benmohammad.mvitasks.model.TasksModelStore
import com.benmohammad.mvitasks.view.EventObservable
import com.benmohammad.mvitasks.view.StateSubscriber
import com.jakewharton.rxbinding2.support.v4.widget.refreshes
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.view.visibility
import com.jakewharton.rxbinding2.widget.text
import com.jakewharton.rxbinding2.widget.textRes
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.ofType
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.task_fragment.*
import kotlinx.android.synthetic.main.task_fragment.view.*
import timber.log.Timber
import javax.inject.Inject

class TasksFragment: Fragment(), StateSubscriber<TasksState>, EventObservable<TasksViewEvent> {

    @Inject
    lateinit var tasksModelStore: TasksModelStore

    @Inject
    lateinit var taskInentFactory: TasksIntentFactory

    private val disposables = CompositeDisposable()
    private val menuDisposables = CompositeDisposable()

    @Inject
    lateinit var tasksAdapter: TasksAdapter

    override fun Observable<TasksState>.subscribeToState(): Disposable {
        return CompositeDisposable(
                map { it.syncState}.ofType<ERROR>().map { it.throwable }.subscribe(Timber::e),
                map {it.syncState is PROCESS}.subscribe(swipeRefreshLayout::setRefreshing),
                map {it.filter.name}.subscribe(filteringLabelTextView.text()),
                map {it.filteredTasks().isEmpty()}.subscribe(noTasksLinearLayout.visibility()),
                map {   tasksState ->
                    when(tasksState.filter) {
                        FilterType.ANY -> R.string.no_tasks_all
                        FilterType.ACTIVE -> R.string.no_tasks_active
                        FilterType.COMPLETE -> R.string.no_tasks_completed
                    }
                }.subscribe(noTasksMainTextView.textRes())
        )
    }

    override fun events(): Observable<TasksViewEvent> {
        return swipeRefreshLayout.refreshes().map {
            TasksViewEvent.RefreshTaskSwipe
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater
                .inflate(R.layout.task_fragment, container, false)
                .also {view -> view.tasksRecyclerView.adapter = tasksAdapter}
    }


    override fun onResume() {
        super.onResume()
        disposables += events().subscribe(taskInentFactory::process)
        disposables += tasksModelStore.modelState().subscribeToState()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.tasks_fragment_menu, menu)
        val menuEvents = Observable.merge(
                menu.findItem(R.id.menu_filter).clicks().map { TasksViewEvent.FilterTypeClick },
                menu.findItem(R.id.menu_refresh).clicks().map { TasksViewEvent.RefreshTaskClick },
                menu.findItem(R.id.menu_clear).clicks().map { TasksViewEvent.ClearCompletedClick }
        )

        menuDisposables += menuEvents.subscribe(taskInentFactory::process)
    }

    override fun onDestroyOptionsMenu() {
        super.onDestroyOptionsMenu()
        menuDisposables.dispose()
    }

    override fun onPause() {
        super.onPause()
        disposables.clear()
    }
}