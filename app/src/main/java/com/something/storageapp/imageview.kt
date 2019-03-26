package com.something.storageapp
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import com.squareup.picasso.Picasso

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class imageview: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.imageview)
        val im = findViewById<ImageView>(R.id.imagedisplay)
        val num:String = intent.extras.getString("name")
        Picasso.get().load(hash.getlist()[num]).into(im)
    }
}