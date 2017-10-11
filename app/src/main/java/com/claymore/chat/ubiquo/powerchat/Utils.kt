package com.claymore.chat.ubiquo.powerchat

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import es.dmoral.toasty.Toasty
import java.util.*
import kotlin.collections.ArrayList

data class User (var phone : String="NA", var provider : String="NA", var registration : Long=0, var last_login : Long=0,var img : String="NA" )

sealed class AppPermission(val permissionName: String, val requestCode: Int,  val deniedMessageId: String) {

    object CAMERA:  AppPermission(
            permissionName = Manifest.permission.CAMERA,
            requestCode = 1,
            deniedMessageId = "camera denied")

    object READ_CONTACTS : AppPermission(
            permissionName = Manifest.permission.READ_CONTACTS,
            requestCode = 100,
            deniedMessageId = "Contacts denied")
}

fun AppCompatActivity.addFragment(container : Int, frag : Fragment) {
    supportFragmentManager.customTransaction { add(container, frag) }
}

fun AppCompatActivity.removeFragment(frag : Fragment){
    supportFragmentManager.customTransaction { remove(frag) }
}

inline fun <reified T : Activity> AppCompatActivity.goToPage(bundle : Bundle? = null ){
    val intent = Intent(this, T::class.java)
    if(bundle == null) startActivity(intent) else startActivity(intent, bundle)
}

fun Context.showError (message : CharSequence, duration : Int = Toast.LENGTH_SHORT){
    Toasty.error(this, message, duration).show()
}

fun Context.showSuccess (message : CharSequence, duration : Int = Toast.LENGTH_SHORT){
    Toasty.success(this, message, duration).show()
}

fun Context.showInfo (message : CharSequence, duration : Int = Toast.LENGTH_SHORT){
    Toasty.info(this, message, duration).show()
}

fun ProgressBar.switchVisibility(){
    if (this.visibility == View.VISIBLE) this.visibility = View.INVISIBLE else this.visibility = View.VISIBLE
}

fun userIsLogged(): Boolean = FirebaseAuth.getInstance().currentUser != null

fun Context.addAndCommitPreference(key : String, value : Any?, name : String = "POWER_USER"){
    val prefs_editor = getSharedPreferences(name, Context.MODE_PRIVATE).edit()
    when(value){
        is String -> prefs_editor.putString(key,value).apply()
        is Int -> prefs_editor.putInt(key,value).apply()
        is Boolean -> prefs_editor.putBoolean(key,value).apply()
        is Long -> prefs_editor.putLong(key,value).apply()
        is Float -> prefs_editor.putFloat(key,value).apply()
        else -> throw UnsupportedOperationException("Not yet implemented")
    }
}

inline fun <reified T : Any> Context.getFromPreferences(key : String, default : T? = null, from : String = "POWER_USER") : T?{
    return when (T::class){
        String::class -> this.getSharedPreferences(from, Context.MODE_PRIVATE).getString(key, default as String?) as T?
        Int::class -> this.getSharedPreferences(from, Context.MODE_PRIVATE).getInt(key, default as? Int ?: 0) as T?
        Boolean::class -> this.getSharedPreferences(from, Context.MODE_PRIVATE).getBoolean(key, default as? Boolean ?: false) as T?
        Long::class -> this.getSharedPreferences(from, Context.MODE_PRIVATE).getLong(key, default as? Long ?: 0) as T?
        Float::class -> this.getSharedPreferences(from, Context.MODE_PRIVATE).getFloat(key, default as? Float ?: 0f) as T?
        else -> throw UnsupportedOperationException("Unsupported type")
    }
}

fun Context.takeColor(color : Int) : Int = ContextCompat.getColor(this, color)

fun Fragment.inflateInContainer(root : Int , container : ViewGroup?) : ViewGroup {
    return this.layoutInflater.inflate(root, container, false) as ViewGroup
}

fun View.setVisible()  { this.visibility = View.VISIBLE }

fun View.setInvisible()  { this.visibility = View.INVISIBLE }

inline fun FragmentManager.customTransaction(func : FragmentTransaction.() -> FragmentTransaction){
    beginTransaction().func().commit()
}

fun Random.inside(range: IntRange): Int {
    return range.start + nextInt(range.last - range.start)
}




