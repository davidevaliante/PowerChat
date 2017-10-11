package com.claymore.chat.ubiquo.powerchat.userpages



import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.claymore.chat.ubiquo.powerchat.*
import kotlinx.android.synthetic.main.phone_card.view.*
import kotlinx.android.synthetic.main.phone_contacts.*
import com.github.tamir7.contacts.Contact as CONTACT
import com.github.tamir7.contacts.Contacts as CONTACTS




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
            holder?.bind(contact)
        }

        override fun getItemCount(): Int {
           return contacts.size
        }

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

