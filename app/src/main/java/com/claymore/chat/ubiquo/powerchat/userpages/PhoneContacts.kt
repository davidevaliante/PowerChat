package com.claymore.chat.ubiquo.powerchat.userpages


import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat.checkSelfPermission
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import bolts.Continuation
import bolts.Task
import com.claymore.chat.ubiquo.powerchat.*

import kotlinx.android.synthetic.main.activity_main_user_page.*
import kotlinx.android.synthetic.main.phone_card.view.*
import kotlinx.android.synthetic.main.phone_contacts.*
import kotlinx.android.synthetic.main.phone_contacts.view.*
import java.util.concurrent.Callable
import com.github.tamir7.contacts.Contact as CONTACT
import com.github.tamir7.contacts.Contacts as CONTACTS




/**
 * A simple [Fragment] subclass.
 */
class PhoneContacts : Fragment()  {

    var phoneList : ListView? = null
    val PERMISSIONS_REQUEST_READ_CONTACTS = 100

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val viewGroup = inflateInContainer(R.layout.phone_contacts, container)
        phoneList = viewGroup.findViewById(R.id.phoneContactsList)

        val phoneRC = viewGroup.findViewById<RecyclerView>(R.id.phoneRC)

        val items = CONTACTS.getQuery().find() as ArrayList<CONTACT>

        activity.showInfo("Nome : ${items[0].displayName} \n ${items[0].phoneNumbers[0].number}")
        items.forEach { Log.d("Name","Name : ${it.displayName} \n Number : ${if (it.phoneNumbers.size != 0)it.phoneNumbers[0].normalizedNumber else "nope"}") }

        phoneRC.layoutManager = LinearLayoutManager(activity)
        phoneRC.adapter = PhoneRecyclerAdapter(items)

        return viewGroup
    }

    class PhoneRecyclerAdapter (mylist : ArrayList<CONTACT>): RecyclerView.Adapter<PhoneViewHolder>(){
        val contacts = mylist
        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): PhoneViewHolder {
            val view = LayoutInflater.from(parent?.context).inflate(R.layout.phone_card, parent, false)
            return PhoneViewHolder(view)
        }

        override fun onBindViewHolder(holder: PhoneViewHolder?, position: Int) {
            val contact = contacts[position]
            holder?.bind(contact)
        }

        override fun getItemCount(): Int {
           return contacts.size
        }

    }

    class PhoneViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        fun bind(contact : CONTACT){
            itemView.pName.text = contact.displayName
            itemView.pPhone.text = if (contact.phoneNumbers.size != 0) contact.phoneNumbers[0].normalizedNumber else "nope"}
        }




}

