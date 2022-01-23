package com.hcraestrak.kartsearch.view.adapter

import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.hcraestrak.kartsearch.R
import com.hcraestrak.kartsearch.view.adapter.data.UserInfoData
import com.hcraestrak.kartsearch.view.adapter.listener.OnItemClickListener
import java.io.File
import java.io.IOException

class UserInfoRecyclerViewAdapter: RecyclerView.Adapter<UserInfoRecyclerViewAdapter.ViewHolder>() {

    private val data = mutableListOf<UserInfoData>()
    private var matchId: String = ""
    private var isWin: Int = 0 // 0 : Lose, 1 : Win
    private lateinit var mListener: OnItemClickListener

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        mListener = object : OnItemClickListener {
            override fun onClick(id: Int) {
                listener(id)
            }
        }
    }

    fun setData(data: List<UserInfoData>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    fun getMatchId() = matchId
    fun getIsWin() = isWin

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rank: TextView = view.findViewById(R.id.item_rank)
        val kart: ImageView = view.findViewById(R.id.item_kart_img)
        val map: TextView = view.findViewById(R.id.item_map)
        val time: TextView = view.findViewById(R.id.item_time)

        fun bind(data: UserInfoData) {
                Log.d("UserInfoData", data.toString())
            if (data.isRetired == "0" && data.time != "" && data.userRank != "") {
                rank.text = "${data.userRank}/${data.playerCount}"
                time.text = getTime(data.time.toInt())
                when (data.isWin) {
                    "0" -> {
                        itemView.setBackgroundResource(R.drawable.background_lose)
                        rank.setTextColor(Color.parseColor("#FF8484"))
                    }
                    "1" -> {
                        itemView.setBackgroundResource(R.drawable.background_win)
                        rank.setTextColor(Color.parseColor("#7CA8FF"))
                    }
                    else -> {
                        data.isRetired = "1"
                    }
                }
            } else if (data.isRetired == "1" || data.isRetired == "" || data.time != "" && data.userRank != "") {
                retire()
            }
            getImage(data.kart)
            getTrackName(data.track, map)
        }

        private fun retire() {
            rank.text = "Re"
            time.text = "-"
            itemView.setBackgroundResource(R.drawable.background_none)
        }

        private fun getImage(kartId: String) {
            val storageReference = FirebaseStorage.getInstance().getReference("/kart/$kartId.png")
            try {
                val localFile: File = File.createTempFile("tempfile", ".png")
                storageReference.getFile(localFile)
                    .addOnSuccessListener {
                        val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                        Glide.with(itemView.context).load(bitmap).into(kart)
                    }.addOnFailureListener{
                        Glide.with(itemView.context).load(R.drawable.unknownkart).into(kart)
                    }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        private fun getTrackName(trackId: String, view: TextView) {
            val database: DatabaseReference = Firebase.database("https://kartmap.firebaseio.com/").reference
            database.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (postSnapshot in snapshot.children) {
                        val id = postSnapshot.child("id").getValue(String::class.java)
                        val name = postSnapshot.child("name").getValue(String::class.java).toString()
                        if (id == trackId) {
                            view.text = name
                            break
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("error", "${error.code}: ${error.message}")
                }
            })
        }

        private fun getTime(time: Int): String {
            var min: Int = 0
            var sec: Int = time / 1000
            val mSec: Int = time % 1000
            while (sec > 60) {
                sec -= 60
                min++
            }

            return String.format("%02d:%02d.%03d", min, sec, mSec)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_record, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])

        holder.itemView.setOnClickListener {
            matchId = data[position].matchId
            isWin = if (data[position].isWin == "") {
                0
            } else {
                data[position].isWin.toInt()
            }
            mListener.onClick(1)
        }
    }

    override fun getItemCount() = data.size
}