package com.mobigod.statussaver.data.local

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject

class PreferenceManager @Inject constructor(val sPref: SharedPreferences) {

    var isFirstTime: Boolean
    get() {return sPref.getBoolean("isFirstTime", false)}
    set(value) {sPref.edit().putBoolean("isFirstTime", value).apply()}

    var isFirstRun: Boolean
    get() {return sPref.getBoolean("isFirstRun", true)}
    set(value) {sPref.edit().putBoolean("isFirstRun", value).apply()}

    var currentFileRecord: String?
    get() {return sPref.getString("fileRec", "")}
    set(value) {sPref.edit().putString("fileRec", value).apply()}

}