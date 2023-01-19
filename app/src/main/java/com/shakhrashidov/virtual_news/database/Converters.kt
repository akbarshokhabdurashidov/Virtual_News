package com.shakhrashidov.virtual_news.database

import androidx.room.TypeConverter
import com.shakhrashidov.virtual_news.model.Source

class Converters {

    @TypeConverter
    fun fromSource(s:Source):String = s.name

    @TypeConverter
    fun toSource(name:String):Source = Source(name,name)
}