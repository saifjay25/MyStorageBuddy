package com.something.storageapp

import com.google.firebase.database.Exclude

class upload {
    private lateinit var image: String
    private lateinit var name: String
    private lateinit var key: String
    private lateinit var directory: String

    constructor(name:String, image:String,directory:String) {
        this.name = name
        this.image = image
        this.directory= directory
    }

    constructor(){

    }

    fun getName(): String {
        return name
    }
    fun setName(name:String){
        this.name = name
    }
    fun getImage():String{
        return image
    }
    fun setImage(image:String){
        this.image = image
    }
    fun getdirectory():String{
        return directory
    }
    fun setdirectory(directory:String){
        this.directory = directory
    }

    @Exclude
    fun getkey():String{
        return key
    }
    @Exclude
    fun setkey(key:String){
         this.key=key
    }

}