package com.claymore.chat.ubiquo.powerchat.userpages

import android.Manifest
import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.claymore.chat.ubiquo.powerchat.*
import com.claymore.chat.ubiquo.powerchat.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main_user_page.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import permissions.dispatcher.*


@RuntimePermissions
class MainUserPage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_user_page)

        fab.setOnClickListener{ displayContactsWithPermissionCheck() }

    }

    @SuppressLint("NeedOnRequestPermissionsResult")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    @NeedsPermission(Manifest.permission.READ_CONTACTS)
    fun displayContacts(){
        addFragment(userContainer.id, PhoneContacts())
    }

    @OnPermissionDenied(Manifest.permission.READ_CONTACTS)
    fun onPermissionDeniedContacts(){
        showInfo("We cannot load your contacts without permission")
    }

    @OnNeverAskAgain(Manifest.permission.READ_CONTACTS)
    fun onNeverAskAgainContacts(){
        showInfo("We cannot load your contacts without permission")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(supportFragmentManager.fragments.contains(PhoneContacts())) removeFragment(PhoneContacts())
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
