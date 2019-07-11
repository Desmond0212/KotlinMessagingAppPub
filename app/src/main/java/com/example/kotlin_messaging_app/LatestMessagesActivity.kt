package com.example.kotlin_messaging_app

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.example.kotlin_messaging_app.VO.LatestMessageRowVO
import com.example.kotlin_messaging_app.VO.MessageVO
import com.example.kotlin_messaging_app.VO.UserVO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_latest_messages.*
import kotlinx.android.synthetic.main.user_row_latest_message.view.*

class LatestMessagesActivity : AppCompatActivity()
{
    val adapter = GroupAdapter<ViewHolder>()
    val latestMessageMap = HashMap<String, MessageVO>()

    companion object
    {
        const val TAG = "LatestMessageActivity"
        var currentUser : UserVO? = null
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)

        supportActionBar?.title = "Messages"
        recyclerView_latest_message.adapter = adapter
        recyclerView_latest_message.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        //Adapter Click Listener
        adapter.setOnItemClickListener { item, view ->
            val intent = Intent(this, ChatRoomActivity::class.java)
            val row = item as LatestMessageRowVO

            intent.putExtra(NewMessageActivity.USER_KEY, row.chatPartnerUser)
            startActivity(intent)
        }

        verifyUserIsLoggedIn()
        fetchCurrentUser()
        latestMessageListener()
    }

    private fun refreshLatestMessageRecyclerView()
    {
        adapter.clear()
        latestMessageMap.values.forEach {
            adapter.add(LatestMessageRowVO(it))
        }
    }

    private fun latestMessageListener()
    {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
        ref.addChildEventListener(object: ChildEventListener
        {
            override fun onChildAdded(p0: DataSnapshot, p1: String?)
            {
                val chatMessage = p0.getValue(MessageVO::class.java) ?: return
                adapter.add(LatestMessageRowVO(chatMessage))
                latestMessageMap[p0.key!!] = chatMessage
                refreshLatestMessageRecyclerView()
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?)
            {
                val chatMessage = p0.getValue(MessageVO::class.java) ?: return
                adapter.add(LatestMessageRowVO(chatMessage))
                latestMessageMap[p0.key!!] = chatMessage
                refreshLatestMessageRecyclerView()
            }

            override fun onCancelled(p0: DatabaseError) {}
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
            override fun onChildRemoved(p0: DataSnapshot) {}
        })
    }

    private fun fetchCurrentUser()
    {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                currentUser = p0.getValue(UserVO::class.java)
                Log.d(TAG, "Current User: ${currentUser?.profileimageUrl}")
            }

            override fun onCancelled(p0: DatabaseError) {}
        })
    }

    private fun verifyUserIsLoggedIn()
    {
        val uid = FirebaseAuth.getInstance().uid

        if (uid == null)
        {
            val intent = Intent(this, RegistrationActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean
    {
        when(item?.itemId)
        {
            R.id.menu_sign_out ->
            {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

            R.id.menu_new_message ->
            {
                val intent = Intent(this, NewMessageActivity::class.java)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean
    {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}
