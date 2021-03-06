package com.dexcluesiv.walkandrun.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.dexcluesiv.walkandrun.R
import com.dexcluesiv.walkandrun.utils.Commons
import com.dexcluesiv.walkandrun.utils.Constants.Companion.ACTION_PAUSE_SERVICE
import com.dexcluesiv.walkandrun.utils.Constants.Companion.ACTION_START_OR_RESUME_SERVICE
import com.dexcluesiv.walkandrun.utils.Constants.Companion.ACTION_STOP_SERVICE
import com.dexcluesiv.walkandrun.utils.Constants.Companion.FASTEST_LOCATION_UPDATE_INTERVAL
import com.dexcluesiv.walkandrun.utils.Constants.Companion.LOCATION_UPDATE_INTERVAL
import com.dexcluesiv.walkandrun.utils.Constants.Companion.NOTIFICATION_CHANNEL_ID
import com.dexcluesiv.walkandrun.utils.Constants.Companion.NOTIFICATION_CHANNEL_NAME
import com.dexcluesiv.walkandrun.utils.Constants.Companion.NOTIFICATION_ID
import com.dexcluesiv.walkandrun.utils.Constants.Companion.TIMER_UPDATE_INTERVAL
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

typealias Polyline = MutableList<LatLng>
typealias Polylines = MutableList<Polyline>

@AndroidEntryPoint
class Tracker : LifecycleService() {

    private var hasInitialized=true

    var trackingStopped = false

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    val timeRunInSeconds = MutableLiveData<Long>()

    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    lateinit var curNotificationBuilder: NotificationCompat.Builder

    companion object{

        val timeRunInMillis = MutableLiveData<Long>()
        val isTracking=MutableLiveData<Boolean>()
        val pathPoints=MutableLiveData<Polylines>()
    }

    private fun stopTracking() {
        trackingStopped = true
        hasInitialized = true
        pauseTracking()
        postInitialValues()
        stopForeground(true)
        stopSelf()
    }

    private fun postInitialValues(){

        timeRunInSeconds.postValue(0L)
        timeRunInMillis.postValue(0L)

        isTracking.postValue(false)
        pathPoints.postValue(mutableListOf())
    }

    override fun onCreate() {
        super.onCreate()

        curNotificationBuilder = baseNotificationBuilder

        postInitialValues()

        isTracking.observe(this, Observer {

            updateNotificationTrackingState(it)
            updateLocationTracking(it)
        })
    }



    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        when(intent?.action){

            ACTION_START_OR_RESUME_SERVICE ->

                if(hasInitialized){
                    startForegroundService()
                    hasInitialized=false
                }else {
                    startTimer()
                    Timber.d("Tracker is resuming")
                }

            ACTION_PAUSE_SERVICE -> {
                pauseTracking()
                Timber.d("Tracker Paused")
            }

            ACTION_STOP_SERVICE -> {
                stopTracking()
                Timber.d("Tracker Stopped")
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }


    private var isTimerEnabled = false
    private var lapTime = 0L
    private var timeRun = 0L
    private var timeStarted = 0L
    private var lastSecondTimestamp = 0L

    private fun startTimer() {
        addEmptyPolyline()
        isTracking.postValue(true)
        timeStarted = System.currentTimeMillis()
        isTimerEnabled = true
        CoroutineScope(Dispatchers.Main).launch {
            while (isTracking.value!!) {
                // time difference between now and timeStarted
                lapTime = System.currentTimeMillis() - timeStarted
                // post the new lapTime
                timeRunInMillis.postValue(timeRun + lapTime)
                if (timeRunInMillis.value!! >= lastSecondTimestamp + 1000L) {
                    timeRunInSeconds.postValue(timeRunInSeconds.value!! + 1)
                    lastSecondTimestamp += 1000L
                }
                delay(TIMER_UPDATE_INTERVAL)
            }
            timeRun += lapTime
        }
    }

    private fun pauseTracking(){

        isTracking.postValue(false)
        isTimerEnabled = false
    }

    private fun addEmptyPolyline()= pathPoints?.value?.apply {

        add(mutableListOf())
        pathPoints.postValue(this)
    }?: pathPoints.postValue(mutableListOf(mutableListOf()))


    private fun addPathPoint(location: Location?) {
        location?.let {
            val pos = LatLng(location.latitude, location.longitude)
            pathPoints.value?.apply {
                last().add(pos)
                pathPoints.postValue(this)
            }
        }
    }


    private fun updateLocationTracking(isTracking:Boolean){

        if(isTracking){

            if(Commons.hasLocationPermissions(this)){

                val request=LocationRequest().apply {

                    interval= LOCATION_UPDATE_INTERVAL
                    fastestInterval= FASTEST_LOCATION_UPDATE_INTERVAL
                    priority=PRIORITY_HIGH_ACCURACY
                }


                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) return

                fusedLocationProviderClient.requestLocationUpdates(
                    request,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        }else fusedLocationProviderClient.removeLocationUpdates(locationCallback)

    }


    val locationCallback=object:LocationCallback(){

        override fun onLocationResult(result: LocationResult?) {
            super.onLocationResult(result)

            if(isTracking.value!!){

                result?.locations?.let{ locations ->

                    for(location in locations){
                        addPathPoint(location)
                        Timber.d("Lat:${location.latitude} Long:${location.longitude}")
                    }
                }
            }
        }
    }

    private fun updateNotificationTrackingState(isTracking: Boolean) {
        val notificationActionText = if(isTracking) "Pause" else "Resume"
        val pendingIntent = if(isTracking) {
            val pauseIntent = Intent(this, Tracker::class.java).apply {
                action = ACTION_PAUSE_SERVICE
            }
            PendingIntent.getService(this, 1, pauseIntent, FLAG_UPDATE_CURRENT)
        } else {
            val resumeIntent = Intent(this, Tracker::class.java).apply {
                action = ACTION_START_OR_RESUME_SERVICE
            }
            PendingIntent.getService(this, 2, resumeIntent, FLAG_UPDATE_CURRENT)
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        curNotificationBuilder.javaClass.getDeclaredField("mActions").apply {
            isAccessible = true
            set(curNotificationBuilder, ArrayList<NotificationCompat.Action>())
        }

        if(!trackingStopped) {
            curNotificationBuilder = baseNotificationBuilder
                .addAction(R.drawable.ic_pause_black_24dp, notificationActionText, pendingIntent)
            notificationManager.notify(NOTIFICATION_ID, curNotificationBuilder.build())
        }
    }


    private fun startForegroundService(){

        startTimer()

        isTracking.postValue(true)

        val notificationManager=getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.O){
            createNotificationChannel(notificationManager)
        }

        startForeground(NOTIFICATION_ID,baseNotificationBuilder.build())

        if(!trackingStopped){
            timeRunInSeconds.observe(this, Observer {
                val notification = curNotificationBuilder
                    .setContentText(Commons.getFormattedStopWatchTime(it * 1000L))
                notificationManager.notify(NOTIFICATION_ID, notification.build())
            })
        }
    }

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