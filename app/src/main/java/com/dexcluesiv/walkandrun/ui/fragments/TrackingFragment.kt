package com.dexcluesiv.walkandrun.ui.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dexcluesiv.walkandrun.R
import com.dexcluesiv.walkandrun.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackingFragment : Fragment(R.layout.fragment_tracking) {

    private val viewModel:MainViewModel by viewModels()

}