package com.claymore.chat.ubiquo.powerchat.userpages

import android.Manifest
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.view.View
import com.claymore.chat.ubiquo.powerchat.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.github.kbiakov.codeview.adapters.Options
import io.github.kbiakov.codeview.highlight.ColorTheme
import kotlinx.android.synthetic.main.activity_main_user_page.*

class MainUserPage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_user_page)

        fab.setOnClickListener{displayContacts()}


    }

    @Suppress("UNUSED_PARAMETER", "unused")
    private fun showContats(view : View){

    }

    fun handlePermission(permission: AppPermission,
                              onGranted: (AppPermission) -> Unit,
                              onDenied: (AppPermission) -> Unit,
                              onExplanationNeeded: (AppPermission) -> Unit){onDenied(permission)}



    fun displayContacts(){
        handlePermission(AppPermission.READ_CONTACTS,
                onGranted = {
                    /** Permission is granted and we can use camera*inte */
                    val frag = PhoneContacts()
                    addFragment(userContainer.id , frag )
                },
                onDenied = {
                    /** Permission is not granted - we should request permission **/
                    val permission = Array<String>(1){Manifest.permission.READ_CONTACTS}
                    ActivityCompat.requestPermissions(this,
                           permission,
                            it.requestCode)
                    showError("nope")
                },
                onExplanationNeeded = {
                    /** Additional explanation for permission usage needed **/

                })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            100 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    val frag = PhoneContacts()
                    addFragment(userContainer.id , frag )
                }else{
                    showError("Permission denied")
                }
            }
        }
    }

    private fun updateLastLogin(){
        val user_id = FirebaseAuth.getInstance().currentUser?.uid
        if(user_id != null) {
            FirebaseFirestore.getInstance().collection("Users").document(user_id).update("last_login", System.currentTimeMillis())
        }
    }

   /* private fun convertFromTextToCode(code : String?, language : String="java"){
        if(code != null && !code.isEmpty()) {
            code_view.setOptions(Options.Default.get(this)
                    .withLanguage(language)
                    .withCode(code)
                    .withTheme(ColorTheme.MONOKAI))
        }
    } */

   /*fun buildFakeUsers(){
        val db = FirebaseFirestore.getInstance().collection("Users")
        for (i in 1..100){
            val random = Random()
            val providers = listOf("Wind", "Vodafone", "Tim", "3")
            val user = User(buildFakeNumber(), providers[random.inside(0..4)], currentTime(), currentTime(), "something")
            db.document("user_$i").set(user)
        }
    } */
    /* fun buildFakeNumber() : String {
        val random = Random()
        var s = "32"+random.inside(0..10)
        for (x  in 1..7) s += random.inside(0..10)
        return s
    } */
}
