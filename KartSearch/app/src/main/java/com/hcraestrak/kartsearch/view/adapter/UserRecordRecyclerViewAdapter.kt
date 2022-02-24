package com.hcraestrak.kartsearch.view.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hcraestrak.kartsearch.R
import com.hcraestrak.kartsearch.databinding.ItemLoadMoreBinding
import com.hcraestrak.kartsearch.databinding.ItemRecordBinding
import com.hcraestrak.kartsearch.view.adapter.data.UserInfoData
import com.hcraestrak.kartsearch.view.adapter.listener.OnItemClickListener

class UserRecordRecyclerViewAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOAD = 1

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
        this.data.addAll(data)
        this.data.add(UserInfoData(0, " ", " ", " ", " ", " ", " ", " "))
        notifyDataSetChanged()
    }

    fun clearData() {
        this.data.clear()
        notifyDataSetChanged()
    }

    fun getMatchId() = matchId
    fun getIsWin() = isWin

    class LoadMoreViewHolder(val binding: ItemLoadMoreBinding): RecyclerView.ViewHolder(binding.root) {

    }

    class ViewHolder(val binding: ItemRecordBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: UserInfoData) {
            binding.record = data
            setColor(data)
        }

        private fun setColor(data: UserInfoData) {
            if (data.isRetired == "0" && data.time != "" && data.userRank != "") {
                when (data.isWin) {
                    "0" -> {
                        itemView.setBackgroundResource(R.drawable.background_lose)
                        binding.itemRank.setTextColor(Color.parseColor("#FF8484"))
                    }
                    "1" -> {
                        itemView.setBackgroundResource(R.drawable.background_win)
                        binding.itemRank.setTextColor(Color.parseColor("#7CA8FF"))
                    }
                    else -> {
                        data.isRetired = "1"
                    }
                }
            } else if (data.isRetired == "1" || data.isRetired == "" || data.time == "" || data.userRank == "") {
                itemView.setBackgroundResource(R.drawable.background_none)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_ITEM -> {
                val binding = ItemRecordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ViewHolder(binding)
            }
            else -> {
                val binding = ItemLoadMoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                LoadMoreViewHolder(binding)
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
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
        } else if (holder is LoadMoreViewHolder) {
            holder.binding.loadMoreLayout.setOnClickListener {
                mListener.onClick(2)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (data[position].matchId) {
            " " -> VIEW_TYPE_LOAD
            else -> VIEW_TYPE_ITEM
        }
    }

    override fun getItemCount() = data.size
}