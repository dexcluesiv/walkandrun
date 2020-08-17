package com.dexcluesiv.walkandrun.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dexcluesiv.walkandrun.R
import com.dexcluesiv.walkandrun.room.RunDao
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var runDao: RunDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Timber.tag("RunDao").d("${runDao.hashCode()}")
    }
}