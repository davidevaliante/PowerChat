package com.claymore.chat.ubiquo.powerchat
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.claymore.chat.ubiquo.powerchat.loginfragments.Registration
import com.claymore.chat.ubiquo.powerchat.userpages.MainUserPage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_launcher.*





class Launcher : AppCompatActivity() {
    private val NOT_LOGGED = 0
    private val LOGGED = 1
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)
        logOut.setOnClickListener({ auth.signOut()})
        if(userIsLogged()){
            updateLastLogin()
            goToPage<MainUserPage>()
        }else{
           loadUiBasedOn(userAuthStateFrom(auth))
        }
    }





    private fun userAuthStateFrom(auth : FirebaseAuth) : Int = if(auth.currentUser == null) NOT_LOGGED else LOGGED

    private fun loadUiBasedOn (state : Int){
        when(state){
            NOT_LOGGED -> { val registration = Registration()
                                    addFragment(authFrame.id, registration)
                                    }
            LOGGED -> goToPage<MainUserPage>()

        }
    }

    private fun updateLastLogin(){
        val user_id = FirebaseAuth.getInstance().currentUser?.uid
        if(user_id != null) {
            FirebaseFirestore.getInstance().collection("Users").document(user_id).update("last_login", System.currentTimeMillis())
        }

    }


}

