package com.example.graduationproject

import android.content.Context
import android.content.SharedPreferences

class SharedPrefs(val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)

    fun setValue(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun getValue(key: String): String? {
        return prefs.getString(key, null)
    }

    fun setIntValue(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

    fun getIntValue(key: String, defaultValue: Int): Int {
        return prefs.getInt(key, defaultValue)
    }

}