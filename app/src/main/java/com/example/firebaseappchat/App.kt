package com.example.firebaseappchat

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import okhttp3.internal.Internal.instance

class App: Application() {
    companion object{
        var instance: App?= null
        const val PREFS:String = "Shared_Prefs"
        const val LOCALE: String = "LOCALE"
    }

    override fun onCreate(){
        super.onCreate()
        instance = this
    }

    fun  setLanguagePref(localeKey: String){
        val pref: SharedPreferences.Editor? = getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
        pref?.putString(LOCALE,localeKey)
        pref?.apply()
    }

    fun getLanguagePref():String?{
        val pref:SharedPreferences? = getSharedPreferences(PREFS,Context.MODE_PRIVATE)
        return pref?.getString(LOCALE,"en")
    }
}