package com.example.awesomechatapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.awesomechatapp.R
import com.example.awesomechatapp.adapter.UserAdapter
import com.example.awesomechatapp.model.User
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MainActivity : AppCompatActivity() {

    private lateinit var searchBar: SearchBar
    private lateinit var searchView: SearchView
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var searchContent: RecyclerView
    private lateinit var nestedUserView: NestedScrollView
    private lateinit var emptyView: View
    private lateinit var userList: ArrayList<User>
    private lateinit var userAdapter: UserAdapter
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().reference

        userList = ArrayList()
        userAdapter = UserAdapter(this, userList)

        searchBar = findViewById(R.id.search_bar)

        searchView = findViewById(R.id.search_view)
        searchView
            .getEditText()
            .setOnEditorActionListener { v: TextView?, actionId: Int, event: KeyEvent? ->
                Log.d("DEBUGGING", v!!.text.toString())
                searchBar.setText(v.text)
                userAdapter.setSearchQuery(v.text.toString())

                if (userAdapter.hasNoUsers()) {
                    emptyView.visibility = View.VISIBLE
                    nestedUserView.visibility = View.GONE
                } else {
                    emptyView.visibility = View.GONE
                    nestedUserView.visibility = View.VISIBLE
                }

                searchView.hide()
                false
            }

        searchView
            .getEditText()
            .addTextChangedListener { text ->
                Log.d("DEBUGGING", text.toString())
                userAdapter.setSearchQuery(text.toString())

                if (userAdapter.hasNoUsers()) {
                    emptyView.visibility = View.VISIBLE
                    nestedUserView.visibility = View.GONE
                } else {
                    emptyView.visibility = View.GONE
                    nestedUserView.visibility = View.VISIBLE
                }
            }

        userRecyclerView = findViewById(R.id.user_recycler_view)
        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.adapter = userAdapter

        searchContent = findViewById(R.id.search_content)
        searchContent.layoutManager = LinearLayoutManager(this)
        searchContent.adapter = userAdapter

        nestedUserView = findViewById(R.id.nested_user_view)
        emptyView = findViewById(R.id.empty_view)

        mDbRef.child("user").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (postSnapshot in snapshot.children) {
                    val currentUser = postSnapshot.getValue(User::class.java)

                    if (currentUser!!.uid != mAuth.uid) {
                        userList.add(currentUser!!)
                    }
                }
                userAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // left empty on purpose
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout) {
            mAuth.signOut()
            val intent = Intent(this@MainActivity, Login::class.java)
            finish()
            startActivity(intent)
        }
        return true
    }
}
