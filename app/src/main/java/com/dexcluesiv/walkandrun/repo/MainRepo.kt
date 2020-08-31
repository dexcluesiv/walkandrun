package com.dexcluesiv.walkandrun.repo

import com.dexcluesiv.walkandrun.room.Run
import com.dexcluesiv.walkandrun.room.RunDao
import javax.inject.Inject

class MainRepo @Inject constructor(val runDao: RunDao) {

    suspend fun insertRun(run: Run){
        runDao.insertRun(run)
    }

    suspend fun deleteRun(run: Run) = runDao.deleteRun(run)

    fun getAllRunsSortedByDate() = runDao.getAllRunsSortedByDate()

    fun getAllRunsSortedByTimeInMillis() = runDao.getAllRunsSortedByTimeInMillis()

    fun getAllRunsSortedByDistance() = runDao.getAllRunsSortedByDistance()

    fun getAllRunsSortedByCaloriesBurned() = runDao.getAllRunsSortedByCaloriesBurned()

    fun getAllRunsSortedByAvgSpeed() = runDao.getAllRunsSortedByAvgSpeed()

    fun getTotalDistance() = runDao.getTotalDistance()

    fun getTotalTimeInMillis() = runDao.getTotalDurationInMillis()

    fun getTotalAvgSpeed() = runDao.getTotalAvgSpeed()

    fun getTotalCaloriesBurned() = runDao.getTotalCaloriesBurned()
}