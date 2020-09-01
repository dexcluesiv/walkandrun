package com.dexcluesiv.walkandrun.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import com.dexcluesiv.walkandrun.R
import com.dexcluesiv.walkandrun.ui.MainActivity
import com.dexcluesiv.walkandrun.utils.Constants.Companion.ACTION_PAUSE_SERVICE
import com.dexcluesiv.walkandrun.utils.Constants.Companion.ACTION_SHOW_TRACKING_FRAGMENT
import com.dexcluesiv.walkandrun.utils.Constants.Companion.ACTION_START_OR_RESUME_SERVICE
import com.dexcluesiv.walkandrun.utils.Constants.Companion.ACTION_STOP_SERVICE
import com.dexcluesiv.walkandrun.utils.Constants.Companion.NOTIFICATION_CHANNEL_ID
import com.dexcluesiv.walkandrun.utils.Constants.Companion.NOTIFICATION_CHANNEL_NAME
import com.dexcluesiv.walkandrun.utils.Constants.Companion.NOTIFICATION_ID
import timber.log.Timber

class Tracker : LifecycleService() {


    private var hasInitialized=true

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        when(intent?.action){

            ACTION_START_OR_RESUME_SERVICE ->

                if(hasInitialized){
                    startForegroundService()
                    hasInitialized=false
                }else Timber.d("Tracker is resuming")

            ACTION_PAUSE_SERVICE -> Timber.d("Tracker Paused")
            ACTION_STOP_SERVICE -> Timber.d("Tracker Stopped")
        }

        return super.onStartCommand(intent, flags, startId)
    }


    private fun startForegroundService(){

        val notificationManager=getSystemService(Context.NOTIFICATION_SERVICE)
        as NotificationManager

        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.O){
            createNotificationChannel(notificationManager)
        }

        val notificationBuilder=NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_directions_run_black_24dp)
            .setContentTitle("WalkingAndRunning")
            .setContentText("hh:mm:ss")
            .setContentIntent(getMainActivityPendingIntent())

        startForeground(NOTIFICATION_ID,notificationBuilder.build())

    }

    private fun getMainActivityPendingIntent()=PendingIntent.getActivity(
        this,
        0,
        Intent(this,MainActivity::class.java).also {
            it.action= ACTION_SHOW_TRACKING_FRAGMENT
        },
        FLAG_UPDATE_CURRENT
    )

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager){

        val channel=NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW
        )

        notificationManager.createNotificationChannel(channel)

    }
}