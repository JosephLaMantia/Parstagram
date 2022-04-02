package com.josephlamantia.parstagram.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.josephlamantia.parstagram.*
import com.parse.FindCallback
import com.parse.ParseException
import com.parse.ParseQuery
import com.parse.ParseUser

open class FeedFragment : Fragment() {

    lateinit var postRecyclerView: RecyclerView

    lateinit var adapter: PostAdapter

    var allPosts: ArrayList<Post> = ArrayList<Post>()

    lateinit var swipeContainer: SwipeRefreshLayout


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //this is where we set up our views and click listeners

        view.findViewById<Button>(R.id.btnLogout).setOnClickListener {
            //Logout user
            ParseUser.logOut()
            val currentUser = ParseUser.getCurrentUser()
            if(currentUser === null){
                goToLoginActivity()
                Toast.makeText(requireContext(), "Successfully logged out!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Error logging out!", Toast.LENGTH_SHORT).show()
            }
        }

        swipeContainer = view.findViewById(R.id.swipeContainer)

        swipeContainer.setOnRefreshListener {
            Log.i(TAG, "refreshing timeline")
            queryPosts()
            adapter.notifyDataSetChanged()
        }

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light);

        postRecyclerView = view.findViewById(R.id.postRecyclerView)

        //steps to populate recycler view
        //1.create layout for each row in list
        //2.create data source for each row (this is the Post class)
        //3.create adapter that will bridge data and row layout (PostAdapter)
        //4.set adapter on recyclerview
        adapter = PostAdapter(requireContext(), allPosts)
        postRecyclerView.adapter = adapter
        //5.set layout manager on recyclerview
        postRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        queryPosts()
    }

    open fun queryPosts() {
        val query: ParseQuery<Post> = ParseQuery.getQuery(Post::class.java)
        //find all Post objects
        query.include(Post.KEY_USER)
        //Return posts in descending order: ie newer posts will appear first
        query.addDescendingOrder("createdAt")

        //only return most recent 20 posts
        query.setLimit(20)

        query.findInBackground(object : FindCallback<Post> {
            override fun done(posts: MutableList<Post>?, e: ParseException?) {
                if(e != null){
                    //something has went wrong
                    Log.e(TAG, "Error fetching posts")
                } else {
                    if(posts != null){
                        for(post in posts){
                            Log.i(TAG, "Post: " + post.getDescription() + " , username: " + post.getUser()?.username)
                        }
                        allPosts.addAll(posts)
                        adapter.notifyDataSetChanged()
                        swipeContainer.setRefreshing(false)
                    }
                }
            }
        })
    }

    private fun goToLoginActivity() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
//        finish()
    }

    companion object {
        const val TAG = "FeedFragment"
    }
}