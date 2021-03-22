package com.benmohammad.mvitasks.view.tasks

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.benmohammad.mvitasks.R
import com.benmohammad.mvitasks.intent.TasksIntentFactory
import com.benmohammad.mvitasks.model.TaskEditorModelStore
import com.benmohammad.mvitasks.model.TaskEditorState
import com.benmohammad.mvitasks.util.replaceFragmentInActivity
import com.benmohammad.mvitasks.util.setupActionBar
import com.benmohammad.mvitasks.view.EventObservable
import com.benmohammad.mvitasks.view.StateSubscriber
import com.benmohammad.mvitasks.view.addedittask.AddEditTaskActivity
import com.benmohammad.mvitasks.view.stats.StatisticsActivity
import com.jakewharton.rxbinding2.support.design.widget.itemSelections
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.ofType
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.tasks_activity.*
import javax.inject.Inject

class TasksActivity: AppCompatActivity(),
    StateSubscriber<TaskEditorState>,
    EventObservable<TasksViewEvent> {

    @Inject
    lateinit var editorModelStore: TaskEditorModelStore

    @Inject lateinit var tasksIntentFactory: TasksIntentFactory


    private val disposables = CompositeDisposable()

    override fun Observable<TaskEditorState>.subscribeToState(): Disposable {
        return ofType<TaskEditorState.Editing>().subscribe {
            val intent = Intent(this@TasksActivity, AddEditTaskActivity::class.java)
            startActivity(intent)

        }
    }

    override fun events(): Observable<TasksViewEvent> {
        return newTaskFloatingActionButton.clicks().map {TasksViewEvent.NewTaskClick}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tasks_activity)
        setupActionBar(R.id.toolbar) {
            setHomeAsUpIndicator(R.drawable.ic_menu)
            setDisplayHomeAsUpEnabled(true)
        }

        drawerLayout.apply {
            setStatusBarBackground(R.color.purple_700)
        }

        supportFragmentManager.findFragmentById(R.id.contentFrame) as TasksFragment
                ?: TasksFragment().also {
                    replaceFragmentInActivity(it, R.id.contentFrame)
                }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        disposables += subscribeNavHandling()
        disposables += events().subscribe(tasksIntentFactory::process)
        disposables += editorModelStore.modelState().subscribeToState()
    }

    private fun subscribeNavHandling(): Disposable {
        return navView.itemSelections().subscribe {
            menuItem -> when(menuItem.itemId) {
                R.id.statistics_navigation_menu_item -> {
                    Intent(this@TasksActivity, StatisticsActivity::class.java)
                            .also { startActivity(it) }
                }
            }
            menuItem.isChecked = false
            drawerLayout.closeDrawers()
        }
    }

    override fun onPause() {
        super.onPause()
        disposables.clear()
    }
}