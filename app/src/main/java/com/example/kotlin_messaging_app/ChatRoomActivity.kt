package com.example.kotlin_messaging_app

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.example.kotlin_messaging_app.VO.MessageVO
import com.example.kotlin_messaging_app.VO.UserVO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_room.*
import kotlinx.android.synthetic.main.message_row_receiver.view.*
import kotlinx.android.synthetic.main.message_row_sender.view.*

class ChatRoomActivity : AppCompatActivity()
{
    companion object
    {
        const val TAG = "ChatRoomActivity"
    }

    val adapter = GroupAdapter<ViewHolder>()
    var user : UserVO? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)

        recyclerView_chat_room.adapter = adapter

//        val usernameTitle = intent.getStringExtra(NewMessageActivity.USER_KEY) //For Username Only
        user = intent.getParcelableExtra<UserVO>(NewMessageActivity.USER_KEY)
        supportActionBar?.title = user?.username//usernameTitle

        messagesList()

        btn_send_message_chat_room.setOnClickListener {
            Log.d(TAG, "Sending message....")
            performSendMessage()
        }
    }

    private fun messagesList()
    {
        val fromId = FirebaseAuth.getInstance().uid
        val toId = user?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        ref.addChildEventListener(object: ChildEventListener
        {
            override fun onChildAdded(p0: DataSnapshot, p1: String?)
            {
                val messageFromChat = p0.getValue(MessageVO::class.java)

                if (messageFromChat != null)
                {
                    Log.d(TAG, messageFromChat.text)
                    Log.d("Desmond Debug: ", messageFromChat.text)

                    if (messageFromChat.fromId == FirebaseAuth.getInstance().uid)
                    {
                        val getCurrentUser = LatestMessagesActivity.currentUser ?: return
                        adapter.add(ChatItemSender(messageFromChat.text, getCurrentUser))
                    }
                    else
                    {
                        adapter.add(ChatItemReceiver(messageFromChat.text, user!!))
                    }
                }

                recyclerView_chat_room.scrollToPosition(adapter.itemCount -1)
            }

            override fun onCancelled(p0: DatabaseError) {}

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}

            override fun onChildRemoved(p0: DataSnapshot) {}
        })
    }

    private fun performSendMessage()
    {
        //val ref = FirebaseDatabase.getInstance().getReference("/messages").push()

        val text = txt_enter_message_chat_room.text.toString()
        val user = intent.getParcelableExtra<UserVO>(NewMessageActivity.USER_KEY)
        val fromId = FirebaseAuth.getInstance().uid
        val toId = user.uid
        val timestamp = System.currentTimeMillis() / 1000

        Log.d("Desmond Debug:", toId)

        if (fromId == null) return

        val refFromSender = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        val refFromReceiver = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        val message = MessageVO(refFromSender.key!!, text, fromId, toId, timestamp)

        refFromSender.setValue(message)
            .addOnSuccessListener{
                Log.d(TAG, "Successfully saved message to Firebase from Sender: ${refFromSender.key}")
                txt_enter_message_chat_room.text.clear()
                recyclerView_chat_room.scrollToPosition(adapter.itemCount -1)
            }
            .addOnFailureListener{
                Log.d(TAG, "Failed to save message to Firebase from Sender: ${refFromSender.key}")
            }

        refFromReceiver.setValue(message)
            .addOnSuccessListener{
                Log.d(TAG, "Successfully saved message to Firebase from Receiver: ${refFromReceiver.key}")
            }
            .addOnFailureListener{
                Log.d(TAG, "Failed to save message to Firebase from Receiver: ${refFromReceiver.key}")
            }

        val latestMessageRefFromId = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestMessageRefFromId.setValue(message)

        val latestMessageRefToId = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestMessageRefToId.setValue(message)
    }
}

class ChatItemSender(val text: String, val userProfile: UserVO): Item<ViewHolder>()
{
    override fun bind(viewHolder: ViewHolder, position: Int)
    {
        viewHolder.itemView.txt_message_content_message_row_sender.text = text

        //Retrieving Receiver Profile Image into the Chat
        val url = userProfile.profileimageUrl
        val senderProfileImage = viewHolder.itemView.img_message_row_sender
        Picasso.get().load(url).into(senderProfileImage)
    }

    override fun getLayout(): Int
    {
        return R.layout.message_row_sender
    }
}

class ChatItemReceiver(val text: String, val userProfile: UserVO): Item<ViewHolder>()
{
    override fun bind(viewHolder: ViewHolder, position: Int)
    {
        viewHolder.itemView.txt_message_content_message_row_receiver.text = text

        //Retrieving User Profile Image into the Chat
        val url = userProfile.profileimageUrl
        val senderProfileImage = viewHolder.itemView.img_message_row_receiver
        Picasso.get().load(url).into(senderProfileImage)
    }

    override fun getLayout(): Int
    {
        return R.layout.message_row_receiver
    }
}