package com.something.storageapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.graphics.Color
import android.widget.Toast


class MainActivity : AppCompatActivity(){
    lateinit var auth: FirebaseAuth
    var user =""
    var pass=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        toolbar.setTitleTextColor(Color.BLACK)
        imaging.setImageResource(R.mipmap.newer)
        auth=FirebaseAuth.getInstance()
        login.setOnClickListener{
            signIn(username.text.toString(),password.text.toString())
        }
        signUp.setOnClickListener {
            createAccount(username.text.toString(), password.text.toString())
        }
    }

    public override fun onStart() {
        super.onStart()
        val user = auth.currentUser
        if(user!=null){
            val id= auth.currentUser?.uid
            val intent = Intent (this, secondPage::class.java)
            backpress.user= id!!
            backpress.originaluser=backpress.user
            intent.putExtra("member",0)
            startActivity(intent)
        }
    }

    private fun signIn(email: String, password: String) {
        user = email
        pass = password
        user = email
        pass = password
        if(user == "" || pass == ""){
            Toast.makeText(applicationContext, "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }
        auth.fetchProvidersForEmail(username.text.toString()).addOnCompleteListener {
            val check = it.result?.providers?.isEmpty()
            if (check == false) {// email is found
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    val id= auth.currentUser?.uid
                    val intent = Intent (this, secondPage::class.java)
                    backpress.user= id!!
                    backpress.originaluser=backpress.user
                    intent.putExtra("member",0)
                    startActivity(intent)
                }
            }else{
                Toast.makeText(applicationContext, "email is not found, please click Sign up", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createAccount(email:String, password:String){
        user = email
        pass = password
        if(user == "" || pass == ""){
            Toast.makeText(applicationContext, "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }
        auth.fetchProvidersForEmail(username.text.toString()).addOnCompleteListener {
            val check = it.result?.providers?.isEmpty()
            if (check == true) {// email is not found
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    val id= auth.currentUser?.uid
                    val intent = Intent (this, secondPage::class.java)
                    backpress.user= id!!
                    backpress.originaluser=backpress.user
                    startActivity(intent)
                }
            }else{
                Toast.makeText(applicationContext, "email is found, please click Login", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
