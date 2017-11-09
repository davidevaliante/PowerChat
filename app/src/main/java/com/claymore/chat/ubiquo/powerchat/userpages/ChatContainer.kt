package com.claymore.chat.ubiquo.powerchat.userpages

import android.Manifest
import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.RelativeLayout
import android.widget.Toast
import com.claymore.chat.ubiquo.powerchat.R
import com.claymore.chat.ubiquo.powerchat.addFragment
import com.claymore.chat.ubiquo.powerchat.showError
import com.claymore.chat.ubiquo.powerchat.showInfo
import kotlinx.android.synthetic.main.activity_chat_container.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnNeverAskAgain
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions


@RuntimePermissions
class ChatContainer : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_container)

        displayChatFlowWithPermissionCheck()




    }

    @SuppressLint("NeedOnRequestPermissionsResult")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    @NeedsPermission(Manifest.permission.READ_CONTACTS)
    fun displayChatFlow(){
        addFragment(chatContainer.id, ChatRoom())
    }

    @OnPermissionDenied(Manifest.permission.READ_CONTACTS)
    fun contactsDeniedFlow(){
        showError("Can't display chat without contacts permission, allow contacts permissions in the phone settings", Toast.LENGTH_LONG)
    }

    @OnNeverAskAgain(Manifest.permission.READ_CONTACTS)
    fun onNeverAskAgainContacts(){
        showInfo("Can't display ANY chat without contacts permission, allow contacts permissions in the phone settings", Toast.LENGTH_LONG)
    }

}
