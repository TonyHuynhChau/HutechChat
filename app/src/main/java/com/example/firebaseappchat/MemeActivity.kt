package com.example.firebaseappchat

import android.app.DownloadManager
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

class MemeActivity : AppCompatActivity() {
    var memeUrl : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meme)
        loadMeme()
    }

    fun loadMeme(){

        val loading : ProgressBar = findViewById(R.id.progress)
        loading.visibility = View.VISIBLE

        val queue = Volley.newRequestQueue(this)
        val url = "https://meme-api.herokuapp.com/gimme"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,url,null,
            Response.Listener {
                memeUrl = it.getString("url")
                val memeImage : ImageView = findViewById(R.id.image)

                Glide.with(this).load(memeUrl).listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        loading.visibility = View.VISIBLE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        loading.visibility = View.GONE
                        return false
                    }

                }).into(memeImage)
            },
            Response.ErrorListener {

            }
        )
        queue.add(jsonObjectRequest)
    }

    fun shareMeme(view: View) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra("Check this meme",memeUrl)
        val chooser = Intent.createChooser(intent,"Share meme using....")
        startActivity(chooser)
    }

    fun nextMeme(view: View) {
        loadMeme()
    }

    fun downloadMeme(view: View) {
        val request = DownloadManager.Request(Uri.parse(memeUrl))
            .setTitle("Meme")
            .setDescription("Downloading")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverMetered(true)

        val dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        dm.enqueue(request)
        Toast.makeText(this, "Tải thành công", Toast.LENGTH_SHORT).show()
    }
}