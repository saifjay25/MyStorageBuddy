package com.something.storageapp

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.TextView

class imagelistadapter(con: Context, load: List<upload>) : RecyclerView.Adapter<imagelistadapter.imageholder>() {

    private var context:Context = con
    private var uploads: List<upload> = load
    private lateinit var alistener: OnItemClickListener
    //attaches the row layout to the adapter
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): imageholder {
        val v: View = LayoutInflater.from(context).inflate(R.layout.row_layout, p0, false)
        return imageholder(v)
    }
    //gets size
    override fun getItemCount(): Int {
        return uploads.size
    }
    //binds the bal
    override fun onBindViewHolder(p0: imageholder, p1: Int) {
        val current : upload = uploads.get(p1)
        p0.picname.text = current.getName()
    }
    //has the text of row layout
    inner class imageholder(item: View) : RecyclerView.ViewHolder(item), View.OnClickListener, View.OnCreateContextMenuListener,
    MenuItem.OnMenuItemClickListener{
        var picname:TextView
        init {
            picname= item.findViewById(R.id.text)
            item.setOnClickListener(this)
            item.setOnCreateContextMenuListener(this)
        }
        //on click method for regualr click attached to oon item click in secondpage
        override fun onClick(p0: View?) {
            if(alistener!=null){
               val position:Int = adapterPosition
                if(position!=RecyclerView.NO_POSITION){
                    alistener.itemClick(position, picname.text.toString())
                }
            }
        }
        //creates the menu for long click
        override fun onCreateContextMenu(p0: ContextMenu, p1: View?, p2: ContextMenu.ContextMenuInfo?) {
            val delete: MenuItem = p0.add(Menu.NONE,1,1,"Delete")
            delete.setOnMenuItemClickListener(this)
        }

        override fun onMenuItemClick(p0: MenuItem): Boolean {
            if(alistener!=null) {
                val position: Int = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    if (p0.itemId == 1) {
                        alistener.deleteClick(position)
                        return true
                    }
                }
            }
            return false
        }
    }
    //interface will forward the clicks to underlying activity which is secondpage
    interface OnItemClickListener{
        fun itemClick(position:Int, name:String) // regular clicks
        fun deleteClick(position: Int) // long clicks
    }
    //method is used to set activity as a listener for the interface
    fun setOnItemClickListener(listener: OnItemClickListener){
        alistener= listener
    }
}