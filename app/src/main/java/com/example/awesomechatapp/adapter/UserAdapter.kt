package com.example.awesomechatapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.awesomechatapp.ui.ChatActivity
import com.example.awesomechatapp.R
import com.example.awesomechatapp.model.User


class UserAdapter(val context: Context, val userList: ArrayList<User>):
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private var searchQuery: String = ""
    private var filteredList: ArrayList<User> = userList;

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.user_layout, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: UserViewHolder,
        position: Int
    ) {
        val currentUser = filteredList[position]

        holder.textName.text = currentUser.name

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)

            intent.putExtra("name", currentUser.name)
            intent.putExtra("uid", currentUser.uid)

            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    fun setSearchQuery(searchQuery: String): Unit {
        this.searchQuery = searchQuery

        filteredList = userList.filter { user ->
            user.name!!.contains(searchQuery, ignoreCase = true)
        } as ArrayList<User>

        notifyDataSetChanged()
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textName = itemView.findViewById<TextView>(R.id.txt_name)
    }
}