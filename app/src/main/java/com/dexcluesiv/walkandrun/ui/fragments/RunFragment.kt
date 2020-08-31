package com.dexcluesiv.walkandrun.ui.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dexcluesiv.walkandrun.R
import com.dexcluesiv.walkandrun.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RunFragment : Fragment(R.layout.fragment_run) {

    private val viewModel: MainViewModel by viewModels()
}