package com.arconsis.mvvmnotesample.util

import android.content.Context
import android.support.v4.app.Fragment
import android.widget.Toast
import org.androidobjectherder.ObjectHerder
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Created by Alexander on 04.05.2017.
 */
fun Context.toast(text: String, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, text, duration).show()
}

class Herder<out T>(private val id: String?, private val block: () -> T) : ReadOnlyProperty<Fragment, T> {

    private var value: T? = null

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        if (value == null) {
            value = ObjectHerder.createOrReuseObject(thisRef, id) {
                block()
            }
        }
        return value!!
    }

}