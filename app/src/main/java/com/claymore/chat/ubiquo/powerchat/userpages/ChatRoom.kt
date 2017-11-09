package com.claymore.chat.ubiquo.powerchat.userpages

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.claymore.chat.ubiquo.powerchat.*
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.mikhaellopez.circularimageview.CircularImageView
import kotlinx.android.synthetic.main.activity_chat_container.*
import kotlinx.android.synthetic.main.activity_chat_room.*

class ChatRoom : Fragment() {

    var adapter : FirestoreRecyclerAdapter<AbstractMessage, RecyclerView.ViewHolder>? = null
    var custom_adapter : MultiViewAdapter? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val viewGroup = inflateInContainer(R.layout.activity_chat_room, container)

        val chatImage = viewGroup.findViewById<CircularImageView>(R.id.chatImage)
        val chatTitle = viewGroup.findViewById<TextView>(R.id.chatTitle)
        chatImage.load("https://firebasestorage.googleapis.com/v0/b/powerchat-13c7b.appspot.com/o/pic.jpg?alt=media&token=39fac6d4-ca71-44cf-8aff-78761a83d58a")
        chatTitle.text = "Davide Valiante"

        val fireQuery = FirebaseFirestore.getInstance().collection("Messages").orderBy("time")

        val layouts_left = listOf(R.layout.text_message, R.layout.image_message, R.layout.audio_message,  R.layout.audio_message)

        val options = FirestoreRecyclerOptions.Builder<AbstractMessage>().setQuery(fireQuery, AbstractMessage::class.java).build()



        custom_adapter =  MultiViewAdapter(options, layouts_left, activity)



        val rc = viewGroup.findViewById<RecyclerView>(R.id.messagesRv)
        rc.layoutManager = LinearLayoutManager(activity)
        rc?.adapter = custom_adapter


        return viewGroup
    }

    override fun onStart() {
        super.onStart()
        if(custom_adapter != null) custom_adapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        if (custom_adapter != null) custom_adapter!!.stopListening()
    }
}
