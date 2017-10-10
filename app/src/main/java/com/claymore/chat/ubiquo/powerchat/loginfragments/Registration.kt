package com.claymore.chat.ubiquo.powerchat.loginfragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.claymore.chat.ubiquo.powerchat.*
import com.claymore.chat.ubiquo.powerchat.userpages.MainUserPage
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.fragment_registration.*
import java.util.concurrent.TimeUnit
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.firestore.FirebaseFirestore

class Registration : Fragment() {
    private val auth = FirebaseAuth.getInstance()
    private var messageSent = false
    private var verificationId : String? = null
    private var progressBar : ProgressBar? = null
    private var reg : TextView? = null
    private var authListener : FirebaseAuth.AuthStateListener? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val viewGroup = inflateInContainer(R.layout.fragment_registration,container)
        reg  = viewGroup.findViewById(R.id.register)
        colorTextView(reg as TextView,"Register")
        progressBar = viewGroup.findViewById(R.id.progressBar)
        progressBar?.setInvisible()

        return  viewGroup
    }

    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener { initializeAuthListener() }
    }

    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener { authListener }
    }



    private fun signInWithPhoneNumber(credential : PhoneAuthCredential){
        auth.signInWithCredential(credential).addOnCompleteListener({
            if (it.isSuccessful && it.isComplete) {
            }else activity.showError("Log In failed")
        })
    }

    private fun colorTextView(button : TextView, string : String, code : Boolean = false){
        if(!code) {
            val stringToSpan = SpannableString(string)
            val colorPrimary: ForegroundColorSpan = object : ForegroundColorSpan(ContextCompat.getColor(activity, R.color.colorPrimary)) {}
            val colorAccent: ForegroundColorSpan = object : ForegroundColorSpan(ContextCompat.getColor(activity, R.color.colorAccent)) {}
            val colorYellow: ForegroundColorSpan = object : ForegroundColorSpan(ContextCompat.getColor(activity, R.color.yellow)) {}
            stringToSpan.setSpan(colorAccent, 0, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            stringToSpan.setSpan(colorYellow, 2, 4, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            stringToSpan.setSpan(colorPrimary, 4, 6, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            button.text = stringToSpan
        }else{
            val stringToSpan = SpannableString(string)
            val colorPrimary: ForegroundColorSpan = object : ForegroundColorSpan(ContextCompat.getColor(activity, R.color.colorPrimary)) {}
            val colorAccent: ForegroundColorSpan = object : ForegroundColorSpan(ContextCompat.getColor(activity, R.color.colorAccent)) {}
            val colorYellow: ForegroundColorSpan = object : ForegroundColorSpan(ContextCompat.getColor(activity, R.color.yellow)) {}
            stringToSpan.setSpan(colorAccent, 0, 3, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            stringToSpan.setSpan(colorYellow, 2, 4, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            stringToSpan.setSpan(colorPrimary, 4, 6, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            button.text = stringToSpan
        }

    }

    private fun registrationRequest(phone : String?){
        if(phone != null && phone.isNotEmpty() ) {
           PhoneAuthProvider.getInstance().verifyPhoneNumber(
           phone,60, TimeUnit.SECONDS, activity, myPhoneAuthProvider())
        } else {
            activity.showError("Please insert a phone number")
        }
    }

    private fun manuallySendCode(code : String? , id : String?){
        if (code != null && id != null && code.isNotEmpty() && id.isNotEmpty()){
            val credential = PhoneAuthProvider.getCredential(id, code)
            signInWithPhoneNumber(credential)
        }
    }

    private fun myPhoneAuthProvider() : PhoneAuthProvider.OnVerificationStateChangedCallbacks{
        return object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(p0: PhoneAuthCredential?) {
                if(p0 != null) {
                    signInWithPhoneNumber(p0)
                }
            }

            override fun onVerificationFailed(exception : FirebaseException?) {
                progressBar?.switchVisibility()
                when(exception){
                    is FirebaseAuthInvalidCredentialsException -> activity.showError("Wrong credentials, try again")
                    is FirebaseTooManyRequestsException -> activity.showError("Too many requested, wait before trying again")
                    else -> activity.showError("An unkonwn error has occurred")
                }
            }

            override fun onCodeSent(verification: String?, p1: PhoneAuthProvider.ForceResendingToken?) {
                super.onCodeSent(verification, p1)
                if(verification != null) {
                    verificationId = verification
                    messageSent = true
                    activity.showSuccess("Wait for the code SMS")
                    colorTextView(reg as TextView, "Confirm", code = true)
                    phone.setText("")
                    phone.hint = "Insert the secret code here"
                    activity.addAndCommitPreference("REGISTERED", true)
                }else{
                    activity.showError("Error while sending message, please try again")
                }
            }

            override fun onCodeAutoRetrievalTimeOut(p0: String?) {
                super.onCodeAutoRetrievalTimeOut(p0)
                progressBar?.switchVisibility()
                activity.showError("Message time has timed out, please try again")
                activity.addAndCommitPreference("REGISTERED", false)
            }
        }
    }

    private fun initializeAuthListener() : FirebaseAuth.AuthStateListener {
        return FirebaseAuth.AuthStateListener {
            val user = auth.currentUser
            if(user != null && user.phoneNumber != null){
                val db = FirebaseFirestore.getInstance()
                val id = user.uid
                //new user
                val newUser = User(user.phoneNumber as String, user.providerId, System.currentTimeMillis(), System.currentTimeMillis(), "something")
                //upload new user
                db.collection("Users").document(id).set(newUser).addOnCompleteListener({
                    progressBar?.switchVisibility()
                    val toUserPage = Intent(activity, MainUserPage::class.java)
                    activity.showSuccess("Benvenuto !")
                    startActivity(toUserPage)
                })
            }
        }
    }

    @Suppress("UNUSED_PARAMETER", "unused")
    private fun sendMessageOrRequest(view : View){
        when (messageSent){
            false ->{ registrationRequest(phone.text?.toString())
                progressBar?.visibility = View.VISIBLE      }
            true -> manuallySendCode(phone.text?.toString(), verificationId)
        }
    }

}

