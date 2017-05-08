package com.arconsis.mvvmnotesample.data

import android.content.Context
import android.preference.PreferenceManager

/**
 * Created by Alexander on 05.05.2017.
 */

val SAVED_USER_ID = "userId"
val SAVED_USER_NAME = "username"
val SAVED_USER_PASSWORD = "password"

fun Context.isLocalUserPresent(): Boolean {
    val preferences = getPreferences()
    return preferences.contains(SAVED_USER_ID) && preferences.contains(SAVED_USER_NAME) && preferences.contains(SAVED_USER_PASSWORD)
}

fun Context.getLocalUser(): User {
    val preferences = getPreferences()
    return User(preferences.getInt(SAVED_USER_ID, -1), preferences.getString(SAVED_USER_NAME, ""), preferences.getString(SAVED_USER_PASSWORD, ""))
}

fun Context.saveLocalUser(user: User) {
    getPreferences().edit().putInt(SAVED_USER_ID, user.id).putString(SAVED_USER_NAME, user.username).putString(SAVED_USER_PASSWORD, user.password).apply()
}

fun Context.removeLocalUser() {
    getPreferences().edit().remove(SAVED_USER_ID).remove(SAVED_USER_NAME).remove(SAVED_USER_PASSWORD).apply()
}

private fun Context.getPreferences() = PreferenceManager.getDefaultSharedPreferences(this)

