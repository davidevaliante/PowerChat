package com.claymore.chat.ubiquo.powerchat

import android.Manifest

sealed class AppPermission(val permissionName: String,
                           val requestCode: Int,
                           val deniedMessageId: String,
                           val explanationMessageId: String) {

    object CAMERA:  AppPermission(permissionName = Manifest.permission.CAMERA,
            requestCode = 1,
            deniedMessageId = "camera denied",
            explanationMessageId = "you refused to provide camera permission")

    object READ_CONTACTS : AppPermission(
            permissionName = Manifest.permission.READ_CONTACTS,
            requestCode = 100,
            deniedMessageId = "Contacts denied",
            explanationMessageId = "you denied contacts reads")

}
