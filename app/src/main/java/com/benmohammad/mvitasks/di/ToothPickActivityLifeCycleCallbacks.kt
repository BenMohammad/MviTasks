package com.benmohammad.mvitasks.di

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule

class ToothPickActivityLifeCycleCallbacks: Application.ActivityLifecycleCallbacks {

    private val fragmentCallbacks= ToothPickFragmentLifeCycleCallbacks()

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        activity?.apply {
            Toothpick.inject(this, openActivityScope(this))
        }

        if(activity is FragmentActivity) {
            activity.supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentCallbacks, true)
        }
    }

    private fun openActivityScope(activity: Activity): Scope {
        return Toothpick.openScopes(activity.application, activity).apply {
            installModules(SmoothieActivityModule(activity))
        }
    }



    override fun onActivityDestroyed(activity: Activity) {
        if(activity is FragmentActivity) {
            activity.supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentCallbacks)
        }

        activity?.apply {
            Toothpick.closeScope(this)
        }
    }

    override fun onActivityStarted(activity: Activity) {

    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }
}