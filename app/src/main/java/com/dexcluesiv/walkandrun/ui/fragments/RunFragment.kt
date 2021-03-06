package com.dexcluesiv.walkandrun.ui.fragments

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dexcluesiv.walkandrun.R
import com.dexcluesiv.walkandrun.ui.viewmodel.MainViewModel
import com.dexcluesiv.walkandrun.utils.Commons
import com.dexcluesiv.walkandrun.utils.Constants.Companion.REQUEST_CODE_LOCATION_PERMISSION
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_run.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class RunFragment : Fragment(R.layout.fragment_run), EasyPermissions.PermissionCallbacks {

    private val viewModel: MainViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fab.setOnClickListener {

            findNavController().navigate(R.id.action_runFragment_to_trackingFragment)
        }

        requestPermissions()
    }

    private fun requestPermissions(){

        if(Commons.hasLocationPermissions(requireContext())){
            return
        }

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.Q){

            EasyPermissions.requestPermissions(this,
                "You are requested to enable ALL THE TIME Location Permission before running/walking.",
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }else{

            EasyPermissions.requestPermissions(this,
                "You are requested to enable ALL THE TIME Location Permission before running/walking.",
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {

        if(EasyPermissions.somePermissionPermanentlyDenied(this,perms)){

            AppSettingsDialog.Builder(this).build().show()

        }else requestPermissions()
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}
}