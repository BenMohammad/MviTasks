package com.benmohammad.mvitasks.di

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import toothpick.Toothpick

class ToothPickFragmentLifeCycleCallbacks: FragmentManager.FragmentLifecycleCallbacks() {

    override fun onFragmentPreAttached(fm: FragmentManager, f: Fragment, context: Context) {
        f?.let { fragment -> Toothpick.inject(fragment, Toothpick.openScopes(context, fragment) ) }
    }

    override fun onFragmentDetached(fm: FragmentManager, f: Fragment) {
        f?.let { fragment -> Toothpick.closeScope(fragment) }
    }
}