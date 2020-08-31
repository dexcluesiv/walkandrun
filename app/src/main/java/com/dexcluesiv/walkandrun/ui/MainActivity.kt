package com.dexcluesiv.walkandrun.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.dexcluesiv.walkandrun.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
}