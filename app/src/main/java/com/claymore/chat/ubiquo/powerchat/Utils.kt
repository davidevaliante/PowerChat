package com.claymore.chat.ubiquo.powerchat

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.github.tamir7.contacts.Contact
import com.github.tamir7.contacts.Contacts
import com.github.tamir7.contacts.Query
import com.google.firebase.auth.FirebaseAuth
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.audio_message.view.*
import kotlinx.android.synthetic.main.image_message.view.*
import kotlinx.android.synthetic.main.text_message.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

data class User (var phone : String="NA", var provider : String="NA", var registration : Long=0, var last_login : Long=0,var img : String="NA" )

data class Chat(var users : List<User>? = null, var last_message : AbstractMessage? = null )

data class AbstractMessage(var senderNumber : String? = null ,var senderId : String? = null , var text : String?=null , var time : Long=0, var type : Int=0, var imgUri: String?=null, var soundUri : String? = null, var videoUri : String?= null)

sealed class AppPermission(val permissionName: String, val requestCode: Int,  val deniedMessageId: String) {

    object CAMERA:  AppPermission(
            permissionName = Manifest.permission.CAMERA,
            requestCode = 1,
            deniedMessageId = "camera denied")

    object READ_CONTACTS : AppPermission(
            permissionName = Manifest.permission.READ_CONTACTS,
            requestCode = 100,
            deniedMessageId = "Contacts denied")
}

fun AppCompatActivity.addFragment(container : Int, frag : Fragment) {
    supportFragmentManager.customTransaction { add(container, frag) }
}

fun AppCompatActivity.removeFragment(frag : Fragment){
    supportFragmentManager.customTransaction { remove(frag) }
}

inline fun <reified T : Activity> AppCompatActivity.goToPage(bundle : Bundle? = null ){
    val intent = Intent(this, T::class.java)
    if(bundle == null) startActivity(intent) else startActivity(intent, bundle)
}

fun Context.showError (message : CharSequence, duration : Int = Toast.LENGTH_SHORT){
    Toasty.error(this, message, duration).show()
}

fun Context.showSuccess (message : CharSequence, duration : Int = Toast.LENGTH_SHORT){
    Toasty.success(this, message, duration).show()
}

fun Context.showInfo (message : CharSequence, duration : Int = Toast.LENGTH_SHORT){
    Toasty.info(this, message, duration).show()
}

fun ProgressBar.switchVisibility(){
    if (this.visibility == View.VISIBLE) this.visibility = View.INVISIBLE else this.visibility = View.VISIBLE
}

fun userIsLogged(): Boolean = FirebaseAuth.getInstance().currentUser != null

fun Context.addAndCommitPreference(key : String, value : Any?, name : String = "POWER_USER"){
    val prefs_editor = getSharedPreferences(name, Context.MODE_PRIVATE).edit()
    when(value){
        is String -> prefs_editor.putString(key,value).apply()
        is Int -> prefs_editor.putInt(key,value).apply()
        is Boolean -> prefs_editor.putBoolean(key,value).apply()
        is Long -> prefs_editor.putLong(key,value).apply()
        is Float -> prefs_editor.putFloat(key,value).apply()
        else -> throw UnsupportedOperationException("Not yet implemented")
    }
}

inline fun <reified T : Any> Context.getFromPreferences(key : String, default : T? = null, from : String = "POWER_USER") : T?{
    return when (T::class){
        String::class -> this.getSharedPreferences(from, Context.MODE_PRIVATE).getString(key, default as String?) as T?
        Int::class -> this.getSharedPreferences(from, Context.MODE_PRIVATE).getInt(key, default as? Int ?: 0) as T?
        Boolean::class -> this.getSharedPreferences(from, Context.MODE_PRIVATE).getBoolean(key, default as? Boolean ?: false) as T?
        Long::class -> this.getSharedPreferences(from, Context.MODE_PRIVATE).getLong(key, default as? Long ?: 0) as T?
        Float::class -> this.getSharedPreferences(from, Context.MODE_PRIVATE).getFloat(key, default as? Float ?: 0f) as T?
        else -> throw UnsupportedOperationException("Unsupported type")
    }
}

fun Context.takeColor(color : Int) : Int = ContextCompat.getColor(this, color)

fun String.isCurrentUser() : Boolean = this == FirebaseAuth.getInstance().currentUser?.uid

fun Activity.currentUserId() : String? =
        if (FirebaseAuth.getInstance().currentUser != null) FirebaseAuth.getInstance().currentUser?.uid.toString()
        else "User Not Logged"
fun Fragment.currentUserId() : String? = FirebaseAuth.getInstance().currentUser?.uid


fun Long.millisecondToHHMMformat() : String {
    val formatter = SimpleDateFormat("HH:mm")
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this
    return formatter.format(calendar.time)
}

fun Fragment.inflateInContainer(root : Int , container : ViewGroup?) : ViewGroup {
    return this.layoutInflater.inflate(root, container, false) as ViewGroup
}

fun View.setVisible()  { this.visibility = View.VISIBLE }

fun View.setInvisible()  { this.visibility = View.INVISIBLE }

inline fun FragmentManager.customTransaction(func : FragmentTransaction.() -> FragmentTransaction){
    beginTransaction().func().commit()
}

fun Random.inside(range: IntRange): Int {
    return range.start + nextInt(range.last - range.start)
}



fun ImageView.load(uri : String, targetView : ImageView= this){
    Glide.with(context).load(uri).into(targetView)
}



class textMessageViewHolder(itemView: View?, val context: Context) : RecyclerView.ViewHolder(itemView) {
    val card = itemView?.findViewById<CardView>(R.id.text_message_card)
    //dpi
    val four_dpi = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, context.resources.displayMetrics).toInt()
    val sixteen_dpi = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, context.resources.displayMetrics).toInt()
    val fiftyEight_dpi = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 58f, context.resources.displayMetrics).toInt()

    fun bindData(data : AbstractMessage, ownMessage: Boolean){
        fun messageLayout(ownMessage : Boolean) = when(ownMessage) {
            true -> {
                fun ownMessageLayoutParams() : RelativeLayout.LayoutParams{
                    val ownMessageLayoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT)
                    ownMessageLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE)
                    ownMessageLayoutParams.topMargin = four_dpi
                    ownMessageLayoutParams.marginEnd = sixteen_dpi
                    ownMessageLayoutParams.bottomMargin = four_dpi
                    ownMessageLayoutParams.marginStart = fiftyEight_dpi
                    return ownMessageLayoutParams
                }
                card?.layoutParams = ownMessageLayoutParams()
                card?.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
                itemView.tSender.visibility = View.GONE  }
            false -> {  val contacts = Contacts.getQuery().find() as ArrayList<Contact>
                fun receivedMessageLayoutParams() : RelativeLayout.LayoutParams{
                    val ownMessageLayoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT)
                    ownMessageLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE)
                    ownMessageLayoutParams.topMargin = four_dpi
                    ownMessageLayoutParams.marginEnd = fiftyEight_dpi
                    ownMessageLayoutParams.bottomMargin = four_dpi
                    ownMessageLayoutParams.marginStart = sixteen_dpi
                    return ownMessageLayoutParams
                }
                fun isInContacts(number : String?) : String?{
                    val findByNumber = Contacts.getQuery()
                    findByNumber.whereEqualTo(Contact.Field.PhoneNumber, number)
                    val contactsMatchingNumber = findByNumber.find()
                    return if (contactsMatchingNumber.size >= 1) contactsMatchingNumber[0].displayName
                    else number
                }
                card?.layoutParams = receivedMessageLayoutParams()
                card?.setCardBackgroundColor(ContextCompat.getColor(context, R.color.pureWhite))
                itemView.tSender.visibility = View.VISIBLE
                itemView.tSender.text = isInContacts(data.senderNumber)   }
        }
        itemView.tSender.text = data.senderNumber
        itemView.text_body.text = data.text
        itemView.tTime.text = data.time.millisecondToHHMMformat()
        messageLayout(ownMessage)
    }

}

class imageMessageViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
    fun bindViews(data : AbstractMessage){
        itemView.image_title.text = data.text.toString()
    }
}

class audioMessageViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
    fun bindViews(data : AbstractMessage){
        itemView.audio_title.text = data.text.toString()
    }
}

class MultiViewAdapter (query : FirestoreRecyclerOptions<AbstractMessage>, custom_layouts : List<Int>, ctx : Context) :
                             FirestoreRecyclerAdapter<AbstractMessage, RecyclerView.ViewHolder> (query) {
    val layouts = custom_layouts
    val context = ctx

    override fun getItemViewType(position: Int): Int {
        val message = getItem(position)
        return message.type
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        var view : View? = null
        if(parent != null) {
            when (viewType) {
                0 -> { view = LayoutInflater.from(parent.context).inflate(layouts[0], parent, false)
                         return textMessageViewHolder(view, parent.context) }
                1 -> { view = LayoutInflater.from(parent.context).inflate(layouts[1], parent, false)
                         return imageMessageViewHolder(view) }
                2 -> { view = LayoutInflater.from(parent.context).inflate(layouts[2], parent, false)
                        return audioMessageViewHolder(view) }
                3 -> { view = LayoutInflater.from(parent.context).inflate(layouts[3], parent, false)
                        return audioMessageViewHolder(view) }
            }
        }
        return textMessageViewHolder(view, context)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int, message: AbstractMessage?) {
        if(message != null) {
            when (holder) {
                is textMessageViewHolder -> {
                    if(message.senderId!!.isCurrentUser()) holder.bindData(message, true)
                    else holder.bindData(message, false)
                }
                is imageMessageViewHolder -> holder.bindViews(message)
                is audioMessageViewHolder -> holder.bindViews(message)
            }
        }
    }




}











