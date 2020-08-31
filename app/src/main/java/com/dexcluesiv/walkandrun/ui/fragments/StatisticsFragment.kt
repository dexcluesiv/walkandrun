package com.dexcluesiv.walkandrun.ui.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dexcluesiv.walkandrun.R
import com.dexcluesiv.walkandrun.ui.viewmodel.StatsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StatisticsFragment : Fragment(R.layout.fragment_statistics) {

    private val viewModel: StatsViewModel by viewModels()
}