package com.example.kotlin_messaging_app

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.kotlin_messaging_app.VO.UserVO
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class NewMessageActivity : AppCompatActivity()
{
    companion object
    {
        val USER_KEY = "USER_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        supportActionBar?.title = "Select User"

        fetchUserFromFirebase()
    }

    private fun fetchUserFromFirebase()
    {
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object: ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                val adapter = GroupAdapter<ViewHolder>()

                p0.children.forEach {
                    Log.d("NewMessageActivity", it.toString())
                    val user = it.getValue(UserVO::class.java)
                    if (user != null)
                    {
                        adapter.add(UserItem(user))
                    }
                }

                adapter.setOnItemClickListener{ item, view ->
                    val userItem = item as UserItem
                    val intent = Intent(view.context, ChatRoomActivity::class.java)
//                    intent.putExtra(USER_KEY,userItem.user.username) //For Username Only
                    intent.putExtra(USER_KEY, userItem.user)
                    startActivity(intent)
                    finish()
                }

                recyclerView_newMessage.adapter = adapter
            }

            override fun onCancelled(p0: DatabaseError)
            {

            }
        })
    }
}

class UserItem(val user: UserVO): Item<ViewHolder>()
{
    override fun bind(viewHolder: ViewHolder, position: Int)
    {
        viewHolder.itemView.lbl_username.text = user.username
        Picasso.get().load(user.profileimageUrl).into(viewHolder.itemView.img_user_row)
    }

    override fun getLayout(): Int
    {
        return R.layout.user_row_new_message
    }
}
