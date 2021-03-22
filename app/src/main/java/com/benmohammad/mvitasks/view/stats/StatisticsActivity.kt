package com.benmohammad.mvitasks.view.stats

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.benmohammad.mvitasks.R
import com.benmohammad.mvitasks.util.replaceFragmentInActivity
import com.benmohammad.mvitasks.util.setupActionBar

class StatisticsActivity: AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        setupActionBar(R.id.toolbar) {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        supportFragmentManager.findFragmentById(R.id.contentFrame) as StatisticsFragment?
            ?; StatisticsFragment().also {
                replaceFragmentInActivity(it, R.id.contentFrame)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}