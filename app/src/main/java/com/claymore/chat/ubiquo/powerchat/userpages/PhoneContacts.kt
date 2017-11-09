package com.claymore.chat.ubiquo.powerchat.userpages



import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.claymore.chat.ubiquo.powerchat.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.phone_card.view.*
import com.github.tamir7.contacts.Contact as CONTACT
import com.github.tamir7.contacts.Contacts as CONTACTS
import io.branch.indexing.BranchUniversalObject
import io.branch.referral.util.LinkProperties


/**
 * A simple [Fragment] subclass.
 */
class PhoneContacts : Fragment()  {
    private var contacts : ArrayList<CONTACT>? = null
    private var phoneAdapter : PhoneRecyclerAdapter? = null


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val viewGroup = inflateInContainer(R.layout.phone_contacts, container)
        val phoneRC = viewGroup.findViewById<RecyclerView>(R.id.phoneRC)
        val searchBar = viewGroup.findViewById<EditText>(R.id.searchBar)

        //ritorna tutti i contatti
        contacts = CONTACTS.getQuery().find() as ArrayList<CONTACT>
        phoneAdapter = PhoneRecyclerAdapter(contacts as ArrayList<CONTACT>, activity)
        phoneRC.layoutManager = LinearLayoutManager(activity)
        phoneRC.adapter = phoneAdapter
        searchBar.addTextChangedListener(phoneContactsWatcher())

        return viewGroup
    }

    class PhoneRecyclerAdapter (mylist : ArrayList<CONTACT>, ctx : Activity) : RecyclerView.Adapter<PhoneViewHolder>(){
        private var contacts = mylist
        private var letter : Char? = null
        private val c = ctx

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): PhoneViewHolder {
            val view = LayoutInflater.from(parent?.context).inflate(R.layout.phone_card, parent, false)
            return PhoneViewHolder(view)
        }

        override fun onBindViewHolder(holder: PhoneViewHolder?, position: Int) {
            val contact = contacts[position]
            if(holder != null) {
                holder.bind(contact)
                holder.itemView.setOnClickListener {
                    if (!isChatRoomAvaible()) startNewChatRoom()
                }
            }
        }

        fun isChatRoomAvaible() : Boolean = false

        private fun startNewChatRoom(){
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if(userId != null) {
                var user : User? = null
                FirebaseFirestore.getInstance().collection("Users").document(userId)
                .addSnapshotListener{ documentSnapshot, firebaseFirestoreException ->
                                            user = documentSnapshot.toObject(User::class.java)
                    if(user != null) {
                        val users_group = mutableListOf(user as User)
                        val chatRoom = Chat()
                        chatRoom.users = users_group
                        val chats = FirebaseFirestore.getInstance().collection("ChatRooms").add(chatRoom).addOnCompleteListener({
                            val ref = documentSnapshot.id
                            c.showInfo(ref)
                            buildBranchObject(ref, userId, "${user?.phone}", arrayListOf("+393289751097"))
                        })


                        FirebaseFirestore.getInstance().collection("UsersChatRoom").document(userId).set(chatRoom)
                    }else{
                        c.showError("User is Null")
                    }
              }

            }
        }

        fun buildBranchObject(chatRoomId: String, inviterId: String, inviterName: String, targetNumbers: ArrayList<String>)  {
            val branchUniversalObject = BranchUniversalObject()
                    .setCanonicalIdentifier(chatRoomId)
                    .setTitle("$inviterName would like to chat with you on Khat")
                    .setContentDescription("Follow the link to install Khat and start chatting")
                    .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                    .addContentMetadata("inviter_id", inviterId)
                    .addContentMetadata("chat_id", chatRoomId)

            val linkProperties = LinkProperties()
                    .setChannel("basic_invites")
                    .setFeature("sharing")
                    .addControlParameter("\$desktop_url", "http://example.com/home")
                    .addControlParameter("\$ios_url", "http://example.com/ios")

            branchUniversalObject.generateShortUrl(c, linkProperties, { url, error ->
                if (error == null) {
                    for (i in targetNumbers) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + i))
                        intent.putExtra("sms_body", url)
                        c.startActivity(intent)

                        Log.i("MyApp", "got my Branch link to share: " + url)
                    }
                }
            })
        }


        override fun getItemCount(): Int = contacts.size

        fun updateList(newList : ArrayList<CONTACT>){
            contacts = newList
            notifyDataSetChanged()
        }

    }

    class PhoneViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        fun bind(contact : CONTACT){
            itemView.pName.text = contact.displayName
            itemView.pPhone.text = if (contact.phoneNumbers.size != 0) contact.phoneNumbers[0].normalizedNumber else "nope"}
    }


    private fun phoneContactsWatcher() : TextWatcher{
        return object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
            override fun afterTextChanged(s : Editable?) {

                Handler().postDelayed(
                {
                    val temporaryList = contacts?.filter {it.displayName.toLowerCase().contains(s.toString().toLowerCase()) } as ArrayList<CONTACT>
                    phoneAdapter?.updateList(temporaryList)
                }, 300)

            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        }
    }


}

