package com.dexcluesiv.walkandrun.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.dexcluesiv.walkandrun.R
import com.dexcluesiv.walkandrun.utils.Constants.Companion.ACTION_SHOW_TRACKING_FRAGMENT
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigateToTracker(intent)

        setSupportActionBar(toolbar)
        bottomNavigationView.setupWithNavController(navHostFragment.findNavController())

        navHostFragment.findNavController().addOnDestinationChangedListener { controller, destination, arguments ->

            when(destination.id){

                R.id.setupFragment,R.id.trackingFragment ->
                    bottomNavigationView.visibility= View.GONE

                else -> bottomNavigationView.visibility= View.VISIBLE
            }
        }
    }


    private fun navigateToTracker(intent: Intent?){

        if(intent?.action==ACTION_SHOW_TRACKING_FRAGMENT){

            navHostFragment.findNavController().navigate(R.id.action_global_launch_trackkingFragment)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        navigateToTracker(intent)
    }
}