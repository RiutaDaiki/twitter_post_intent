package com.example.twitterintent

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import java.io.File
import kotlinx.android.synthetic.main.activity_main.*


private val TWITTER_CODE = 140
private val GALLERY_CODE = 1000
private lateinit var videoPath: String


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= 23) {

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ),
                    GALLERY_CODE
                )
            }
        }

        gallery_btn.setOnClickListener {
            openGallery()
        }

        tweet_btn.setOnClickListener {
            launchTwitter()
        }

    }

    private fun openGallery() {
        Intent(
            Intent.ACTION_PICK,
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        ).also { galleryIntent ->
            galleryIntent.resolveActivity(this.packageManager).also {
                startActivityForResult(
                    galleryIntent,
                    GALLERY_CODE
                )
            }
        }
    }

    private fun launchTwitter() {
        val tweetText = "今回初めて記事を書いてみましたが、解説というより自分のコードをペタペタ貼っていっただけになってしまいました。🦬"

        val intent = Intent(Intent.ACTION_SEND)
            .putExtra(Intent.EXTRA_TEXT, "$tweetText\n#ハッシュタグの前には改行を入れると良い感じ")
            .putExtra(Intent.EXTRA_STREAM, createOutPutFile())
            .setType("video/*")
            .setPackage("com.twitter.android")
        startActivityForResult(intent, TWITTER_CODE)
    }

    private fun createOutPutFile(): Uri {
        val selectedVideo = videoPath
        val file = File(selectedVideo)

        val uri = FileProvider.getUriForFile(
            this,
            this.packageName + ".provider",
            file
        )
        return uri
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            GALLERY_CODE -> for (i in 0..permissions.size) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                } else {
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            GALLERY_CODE ->
                data?.data.also { uri ->
                    val columns: Array<String> = arrayOf(MediaStore.Video.Media.DATA)
                    val cursor = this.contentResolver?.query(uri!!, columns, null, null, null)
                    cursor?.moveToFirst()
                    videoPath = cursor?.getString(0)!!
                }
        }
    }
}
