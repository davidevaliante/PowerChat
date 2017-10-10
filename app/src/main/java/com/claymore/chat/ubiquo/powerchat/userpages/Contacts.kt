package com.claymore.chat.ubiquo.powerchat.userpages


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.claymore.chat.ubiquo.powerchat.R
import com.claymore.chat.ubiquo.powerchat.User
import com.claymore.chat.ubiquo.powerchat.inflateInContainer
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main_user_page.*
import kotlinx.android.synthetic.main.fragment_contacts.*


/**
 * A simple [Fragment] subclass.
 */
class Contacts : Fragment() {

    var adapter : FirestoreRecyclerAdapter<User, ContactsHolder>? = null
    var contactsList : RecyclerView? = null
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val viewGroup = inflateInContainer(R.layout.fragment_contacts, userContainer)

        val fireQuery = FirebaseFirestore.getInstance().collection("Users").orderBy("phone")

        val options = FirestoreRecyclerOptions.Builder<User>().setQuery(fireQuery, User::class.java).build()

        adapter = object : FirestoreRecyclerAdapter<User, ContactsHolder>(options){
            override fun onBindViewHolder(holder: ContactsHolder?, position: Int, model: User?) {
                if(holder != null && model != null) holder.bindView(model)
            }

            override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ContactsHolder {
                val view = LayoutInflater.from(parent?.context).inflate(R.layout.user_card, parent, false)
                return ContactsHolder(view)
            }

        }
        contactsList = viewGroup.findViewById(R.id.contactsList)
        contactsList?.layoutManager = LinearLayoutManager(activity)
        contactsList?.adapter = adapter

        return viewGroup
    }

    override fun onStart() {
        super.onStart()
        if(adapter != null) adapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        if (adapter != null) adapter!!.stopListening()
    }
}// Required empty public constructor
