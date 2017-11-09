package com.claymore.chat.ubiquo.powerchat.userpages

import android.Manifest
import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.claymore.chat.ubiquo.powerchat.*
import com.claymore.chat.ubiquo.powerchat.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.github.kbiakov.codeview.adapters.Options
import io.github.kbiakov.codeview.highlight.ColorTheme
import kotlinx.android.synthetic.main.activity_main_user_page.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import permissions.dispatcher.*
import java.util.*


@RuntimePermissions
class MainUserPage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_user_page)
        //buildFakeUsers()

       displayContactsWithPermissionCheck()

        fab.setOnClickListener{
            //displayContactsWithPermissionCheck()
            goToPage<ChatContainer>()
            }

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

/*   private fun convertFromTextToCode(code : String?, language : String="java"){
        if(code != null && !code.isEmpty()) {
            code_view.setOptions(Options.Default.get(this)
                    .withLanguage(language)
                    .withCode(code)
                    .withTheme(ColorTheme.MONOKAI))
        }
    }*/

   fun buildFakeUsers(){
        val db = FirebaseFirestore.getInstance().collection("Messages")
        for (i in 1..100){
            val random = Random()
            val providers = listOf(0, 1,2, 3)
            val numbers = listOf("+393711357716", "+393289751097", "3284017594")
             var message : AbstractMessage? = null
            val numberPicked = random.inside(0..2)

            message = when(numberPicked){
                1 -> AbstractMessage(
                        senderId = FirebaseAuth.getInstance().currentUser?.uid ,
                        senderNumber = numbers[numberPicked],
                        text = "some random text message number+$i",
                        time = System.currentTimeMillis(),
                        type = 0 )

                else -> AbstractMessage(
                        senderId = numberPicked.toString() ,
                        senderNumber = numbers[numberPicked],
                        text = "some random text message number+$i",
                        time = System.currentTimeMillis(),
                        type = 0 )
            }


           /* when(providers[random.inside(0..4)]){
                0 -> message =
                        AbstractMessage(
                                  sender = random.inside(0..10).toString() ,
                                  text = "some random text message number+$i",
                                  time = System.currentTimeMillis(),
                                  type = 0 )
                1 ->  message =
                        AbstractMessage(
                        sender = random.inside(0..10).toString() ,
                        text = "some random text message number+$i",
                        time = System.currentTimeMillis(),
                        type = 1 )

                2 ->  message =
                        AbstractMessage(
                                sender = random.inside(0..10).toString() ,

                                text = "some random text message number+$i",
                        time = System.currentTimeMillis(),
                        type = 2 )

                3 ->  message =
                        AbstractMessage(
                                sender = random.inside(0..10).toString() ,
                                text = "some random text message number+$i",
                        time = System.currentTimeMillis(),
                        type = 3 )
            }*/


            if(message != null) db.document("message_$i").set(message)
        }
    }
    fun buildFakeNumber() : String {
        val random = Random()
        var s = "32"+random.inside(0..10)
        for (x  in 1..7) s += random.inside(0..10)
        return s
    }
}
