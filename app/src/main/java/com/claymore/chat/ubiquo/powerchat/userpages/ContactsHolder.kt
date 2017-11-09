package com.claymore.chat.ubiquo.powerchat.userpages

import android.support.v7.widget.RecyclerView
import android.view.View
import com.claymore.chat.ubiquo.powerchat.User
import kotlinx.android.synthetic.main.user_card.view.*

class ContactsHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        fun bindView(user : User){
            itemView.name.text = user.provider
            itemView.number.text = user.phone
        }




}
