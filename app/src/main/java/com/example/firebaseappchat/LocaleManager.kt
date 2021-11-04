package com.example.firebaseappchat

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import com.bumptech.glide.load.engine.Resource
import java.util.*

object LocaleManager {
    fun setLocale(mContext: Context): Context {
        return if (App.instance!!.getLanguagePref() != null)
            updateResources(mContext, App.instance!!.getLanguagePref()!!)
        else
            mContext
    }

    fun setNewLocale(context: Context, language: String):Context{
        App.instance!!.setLanguagePref(language)
        return updateResources(context,language)
    }

    private fun updateResources(
        context: Context,
        language: String
    ): Context {
        var localContext:Context = context
        val locale = Locale(language)
        Locale.setDefault(locale)
        val res: Resources = context.resources
        val config = Configuration(res.configuration)
        config.setLocale(locale)
        localContext = context.createConfigurationContext(config)
        return localContext
    }
}