package com.something.storageapp

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.barteksc.pdfviewer.PDFView
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class pdfview: AppCompatActivity() {
    private lateinit var pdf:PDFView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pdfview)
        pdf = findViewById(R.id.pdf)
        val num:String = intent.extras.getString("name")
        retrieve().execute(hash.getlist()[num])

    }
    inner class retrieve : AsyncTask<String, Void, InputStream>() {
        override fun doInBackground(vararg p0: String?): InputStream? {
            var input: InputStream? =null
            try {
                val url : URL = URL(p0[0])
                val connection : HttpURLConnection = url.openConnection() as HttpURLConnection
                if(connection.responseCode == 200){
                    input = BufferedInputStream(connection.inputStream)
                }
            } catch (e: IOException){
                    return null
            }
            return input
        }

        override fun onPostExecute(result: InputStream?) {
            pdf.fromStream(result).load()

        }
    }
}