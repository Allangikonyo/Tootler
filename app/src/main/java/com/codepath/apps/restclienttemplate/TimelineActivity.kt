package com.codepath.apps.restclienttemplate

import android.content.Intent
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import org.json.JSONException

class TimelineActivity : AppCompatActivity() {

    lateinit var client: TwitterClient

    lateinit var rvTweets: RecyclerView

    lateinit var adapter: TweetsAdapter

    lateinit var swipeContainer: SwipeRefreshLayout

    val tweets = ArrayList<Tweet>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline)

        client = TwitterApplication.getRestClient(this)

        swipeContainer = findViewById(R.id.swipe_container)

        swipeContainer.setOnRefreshListener {
            Log.i(TAG, "Refreshing the timeline")
            populateHomeTimeline()
        }

        swipeContainer.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light

        )

        rvTweets = findViewById(R.id.rvTweets)
        adapter = TweetsAdapter(tweets)

        rvTweets.layoutManager = LinearLayoutManager(this)
        rvTweets.adapter = adapter

        populateHomeTimeline()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // Handles click on menu item
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.compose) {
            // Navigate to compose screen
            val intent = Intent(this, ComposeActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE)
        }
        return super.onOptionsItemSelected(item)
    }

//     override fun OnActivityResult(requestCode:Int, resultCode:Int, data:Intent?){
//        // Request_code is defined above
//        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
//
//            // Get data from our intent (our tweet)
//            val tweet = data?.getParcelableExtra("tweet") as Tweet
//
//            // Update timeline
//            // Modifying the data source of out tweets
//            tweets.add(0, tweet)
//
//            // Update adapter
//            adapter.notifyItemInserted(0)
//            rvTweets.smoothScrollToPosition(0)
//        }
//        super.onActivityResult(requestCode, resultCode, data)
//    }

    fun populateHomeTimeline(){
        client.populateHomeTimeline(object : JsonHttpResponseHandler (){

            override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                Log.i(TAG , "onSuccess!")

                val jsonArray = json.jsonArray
                try {

                    adapter.clear()
                    val listofNewTweetsRetrieved = Tweet.fromJsonArray(jsonArray)
                    tweets.addAll(listofNewTweetsRetrieved)
                    adapter.notifyDataSetChanged()
                    swipeContainer.setRefreshing(false)
                    
                } catch (e: JSONException) {
                    Log.e(TAG, "JSON Exception")
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                response: String?,
                throwable: Throwable?
            ) {
                Log.i(TAG , "onFailure $statusCode")
            }

        })
    }

    companion object {
        val TAG = "TimelineActivity"
        val REQUEST_CODE = 10
    }
}