package com.something.storageapp

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.webkit.MimeTypeMap
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.second.*
import com.google.firebase.storage.StorageReference
import com.google.firebase.database.*


@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "DEPRECATED_IDENTITY_EQUALS",
    "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS"
)
class secondPage: AppCompatActivity(), imagelistadapter.OnItemClickListener {

    private lateinit var storage: StorageReference
    private lateinit var data: DatabaseReference
    private  var url: Uri? =null
    private lateinit var recycle: RecyclerView
    private lateinit var adapter: imagelistadapter
    private lateinit var load: MutableList<upload>
    private lateinit var store: FirebaseStorage
    private lateinit var open : Animation
    private lateinit var close : Animation
    private lateinit var rotate : Animation
    private lateinit var antirotate : Animation
    private var isopen :Boolean = false
    private lateinit var get : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.second)
        open = AnimationUtils.loadAnimation(applicationContext,R.anim.opentab)
        close = AnimationUtils.loadAnimation(applicationContext,R.anim.tabclose)
        rotate = AnimationUtils.loadAnimation(applicationContext,R.anim.rotate)
        antirotate = AnimationUtils.loadAnimation(applicationContext,R.anim.rotateback)

        floating.setOnClickListener{
            if(isopen){
                upload.startAnimation(close)
                folder.startAnimation(close)
                signout.startAnimation(close)
                floating.startAnimation(antirotate)
                upload.isClickable = false
                signout.isClickable = false
                folder.isClickable = false
                isopen = false
            }else{
                upload.startAnimation(open)
                signout.startAnimation(open)
                folder.startAnimation(open)
                floating.startAnimation(rotate)
                upload.isClickable = true
                signout.isClickable = true
                folder.isClickable =true
                isopen = true
            }
        }

        recycle =recycler
        recycle.setHasFixedSize(true)
        recycle.layoutManager = LinearLayoutManager(this)
        load = mutableListOf()
        adapter = imagelistadapter(applicationContext,load)
        recycle.adapter = adapter
        adapter.setOnItemClickListener(this@secondPage)
        store= FirebaseStorage.getInstance()
        data = FirebaseDatabase.getInstance().getReference(backpress.originaluser)

        signout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            finish()
            val intent = Intent (this, MainActivity::class.java)
            startActivity(intent)
            Toast.makeText(applicationContext,"Logout Successful",Toast.LENGTH_SHORT).show()

        }
        upload.setOnClickListener{
            val intent= Intent().setType("*/*").setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent, "select file"),111)
        }

        folder.setOnClickListener {
            val build :AlertDialog.Builder = AlertDialog.Builder(this,AlertDialog.THEME_DEVICE_DEFAULT_DARK)
            val layout: LayoutInflater = this.layoutInflater
            val view: View = layout.inflate(R.layout.dialog, null)
            build.setTitle("Create folder name")

            build.setView(view).setNegativeButton("cancel"){ dialogInterface: DialogInterface, i: Int ->
            }
            build.setView(view).setPositiveButton("create"){dialogInterface: DialogInterface, i: Int ->
                get = view.findViewById(R.id.photoname)
                val getter: String = get.text.toString()
                //upload is referred here
                val loadee = upload(getter,"folder",backpress.user) //just for database
                val string: String = data.push().key!!
                data.child(string).setValue(loadee)
            }
            val alert = build.create()
            alert.show()
        }
        //get data out of database folder
        //will be called automatically if item is deleted
        val postListener = object : ValueEventListener {
            //data snapshot contains all data of user and will be called when there are any changes of the database reference or called onCreate
            //data snapshot is a list represents data from the database
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                load.clear()
                hash.getlist().clear()
                for (h in dataSnapshot.children) {
                    val loading: upload = h.getValue(com.something.storageapp.upload::class.java)!!
                    if (loading.getdirectory() == backpress.user) {
                        loading.setkey(h.key!!)
                        load.add(loading)
                        hash.getlist().put(loading.getName(), loading.getImage())
                    }
                }
                adapter.notifyDataSetChanged()

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(applicationContext,databaseError.message, Toast.LENGTH_SHORT).show()
            }
        }
        data.addValueEventListener(postListener)
        storage = FirebaseStorage.getInstance().reference
    }

    override fun itemClick(position: Int, name:String) {
        if(hash.getlist()[name]=="folder"){
            val intent = Intent (this, secondPage::class.java)
            backpress.user=backpress.user+"/"+name
            startActivity(intent)
        }else if(hash.getlist()[name]!!.indexOf(".pdf")>=0){
            val intent = Intent (this, pdfview::class.java)
            intent.putExtra("name",name)
            startActivity(intent)
        }else{
            val intent = Intent (this, imageview::class.java)
            intent.putExtra("name",name)
            startActivity(intent)
        }
    }

    override fun deleteClick(position: Int) {
        val item: upload = load[position]
        System.out.println(item.getName())
        val string: String = item.getkey()
        if(item.getImage()=="folder"){
            data.child(string).removeValue()
            Toast.makeText(applicationContext, "deleted", Toast.LENGTH_SHORT).show()
        }else {
            val imageref: StorageReference = store.getReferenceFromUrl(item.getImage())
            imageref.delete().addOnSuccessListener {
                data.child(string).removeValue()
                Toast.makeText(applicationContext, "deleted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode === 111 && resultCode == Activity.RESULT_OK && data!=null && data.data!=null){
            url = data.data
        }
        if(url!=null) {
            uploadFile()
        }
    }

    private fun extension( uri: Uri): String? {
        val cr : ContentResolver = contentResolver
        val mime: MimeTypeMap = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cr.getType(uri))
    }

    fun uploadFile(){
        val user= backpress.originaluser
        var getter: String=""
        val build :AlertDialog.Builder = AlertDialog.Builder(this,AlertDialog.THEME_DEVICE_DEFAULT_DARK)
        val layout: LayoutInflater = this.layoutInflater
        val view: View = layout.inflate(R.layout.namedialog, null)
        build.setTitle("Create name")
        //upload is referred here
        build.setView(view).setNegativeButton("cancel"){ dialogInterface: DialogInterface, i: Int ->
        }
        build.setView(view).setPositiveButton("create"){dialogInterface: DialogInterface, i: Int ->
            get = view.findViewById(R.id.pname)
            getter= get.text.toString()
            if(url!=null && getter!="") {
                val ref: StorageReference = storage.child(user + "/" + System.currentTimeMillis().toString() + "." + extension(url!!))
                ref.putFile(url!!).addOnSuccessListener {
                    // successlistner waits to retrieve downloadurl and then gets the string instead of doing it all at once
                    ref.downloadUrl.addOnSuccessListener {
                        Toast.makeText(this, "upload successful", Toast.LENGTH_SHORT).show()
                        //upload is referred here
                        val load = upload(getter+"."+extension(url!!).toString(), it.toString(),backpress.user)
                        val string: String = data.push().key!!
                        data.child(string).setValue(load)
                        url=null
                    }
                }
            }
        }
        val alert = build.create()
        alert.show()
    }

    override fun onBackPressed() {
        if(backpress.user.indexOf('/')<0){
            Toast.makeText(applicationContext,"Please tap logout to sign out",Toast.LENGTH_SHORT).show()
            return
        }
        super.onBackPressed()
        backpress.user=backpress.user.substring(0,backpress.user.lastIndexOf('/'))
        val loadee = upload("","","") //just for database
        val string: String = data.push().key!!
        data.child(string).setValue(loadee)
    }
}