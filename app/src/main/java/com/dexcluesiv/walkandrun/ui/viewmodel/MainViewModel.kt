package com.dexcluesiv.walkandrun.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.dexcluesiv.walkandrun.repo.MainRepo

public class MainViewModel @ViewModelInject constructor(
    val mainRepo: MainRepo
): ViewModel(){}