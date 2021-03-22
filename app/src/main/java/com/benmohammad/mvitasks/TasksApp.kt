package com.benmohammad.mvitasks

import android.app.Application
import com.benmohammad.mvitasks.di.ToothPickActivityLifeCycleCallbacks
import com.benmohammad.mvitasks.model.backend.TasksRestApi
import com.benmohammad.mvitasks.model.backend.TasksRestApiModule
import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieApplicationModule

class TasksApp: Application() {
    override fun onCreate() {
        super.onCreate()
        Toothpick.inject(this, openApplicationScope(this))
        registerActivityLifecycleCallbacks(ToothPickActivityLifeCycleCallbacks())
    }


    private fun openApplicationScope(app: Application): Scope {
        return Toothpick.openScope(app).apply {
            installModules(
                    SmoothieApplicationModule(app),
                    TasksRestApiModule
            )
        }
    }
}