package com.benmohammad.mvitasks.view.addedittask

import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.benmohammad.mvitasks.R
import com.benmohammad.mvitasks.intent.AddEditTaskIntentFactory
import com.benmohammad.mvitasks.model.TaskEditorModelStore
import com.benmohammad.mvitasks.model.TaskEditorState
import com.benmohammad.mvitasks.model.TaskEditorState.Editing
import com.benmohammad.mvitasks.model.TasksModelStore
import com.benmohammad.mvitasks.util.replaceFragmentInActivity
import com.benmohammad.mvitasks.util.setupActionBar
import com.benmohammad.mvitasks.view.EventObservable
import com.benmohammad.mvitasks.view.StateSubscriber
import com.benmohammad.mvitasks.view.addedittask.AddEditTaskViewEvent.DeleteTaskClick
import com.benmohammad.mvitasks.view.addedittask.AddEditTaskViewEvent.SaveTaskClick
import com.jakewharton.rxbinding2.support.v7.widget.itemClicks
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.addtask_activity.*
import kotlinx.android.synthetic.main.tasks_activity.*
import kotlinx.android.synthetic.main.tasks_activity.toolbar
import timber.log.Timber
import javax.inject.Inject

class AddEditTaskActivity: AppCompatActivity(),
        StateSubscriber<TaskEditorState>,
        EventObservable<AddEditTaskViewEvent> {

    @Inject lateinit var editModelStore: TaskEditorModelStore
    @Inject lateinit var intentFactory: AddEditTaskIntentFactory

    private val navigateUpRelay = PublishRelay.create<AddEditTaskViewEvent.CancelTaskClick>()
    private val disposables = CompositeDisposable()
    override fun Observable<TaskEditorState>.subscribeToState(): Disposable {
        return CompositeDisposable().also {
            innerDisposables ->
            innerDisposables += subscribe { Timber.i("$it")}
            innerDisposables += subscribe {
                when(it) {
                    is Editing -> {
                        fab_edit_task_done.isEnabled = true
                        busy.visibility = View.GONE
                    }
                    is TaskEditorState.Saving -> {
                        invalidateOptionsMenu()
                        fab_edit_task_done.isEnabled = false
                        busy.visibility = View.VISIBLE
                    }
                    TaskEditorState.Closed -> {
                        onBackPressed()
                    }
                }
            }
        }
    }



    override fun events(): Observable<AddEditTaskViewEvent> {
        return Observable.merge(
            toolbar.itemClicks()
                .filter {it.itemId == R.id.menu_delete}
                .map { DeleteTaskClick },
            fab_edit_task_done.clicks().map { SaveTaskClick },
            navigateUpRelay
        )
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        disposables += editModelStore.modelState()
            .firstElement()
            .subscribe {state ->
                menu.findItem(R.id.menu_delete).apply {
                    val itemEnabled = state is Editing && !state.adding
                    isVisible = itemEnabled
                    isEnabled = itemEnabled
                }
            }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onSupportNavigateUp(): Boolean {
        navigateUpRelay.accept(AddEditTaskViewEvent.CancelTaskClick)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addtask_activity)
        fab_edit_task_done?.apply {
            setImageResource(R.drawable.ic_done)
        }

        setupActionBar(R.id.toolbar) {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        supportFragmentManager.findFragmentById(R.id.contentFrame) as AddEditTaskFragment?
            ?: AddEditTaskFragment().also {
                replaceFragmentInActivity(it, R.id.contentFrame)
            }
    }

    override fun onResume() {
        super.onResume()
        disposables += editModelStore.modelState().subscribeToState()
        disposables += events().subscribe(intentFactory::process)
    }

    override fun onPause() {
        super.onPause()
        disposables.clear()
    }
}