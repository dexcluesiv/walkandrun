package com.dexcluesiv.walkandrun.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dexcluesiv.walkandrun.utils.Converters

@Database(
    entities = [Run::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class RunDb: RoomDatabase() {

    abstract fun getRunDao(): RunDao
}