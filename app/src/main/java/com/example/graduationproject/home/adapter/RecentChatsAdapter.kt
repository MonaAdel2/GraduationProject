package com.example.graduationproject.home.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.graduationproject.R
import com.example.graduationproject.home.model.RecentChat
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RecentChatsAdapter : RecyclerView.Adapter<RecentChatsViewHolder>() {

    private val TAG = "RecentChatsAdapter"

    private var recentChatsList = listOf<RecentChat>()
    private var listener: onRecentChatClicked? = null
    private var recentChats = RecentChat()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentChatsViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.recent_chats_item, parent, false)
        return RecentChatsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return recentChatsList.size
    }

    override fun onBindViewHolder(holder: RecentChatsViewHolder, position: Int) {
        val recentChatsList = recentChatsList[position]
        recentChats = recentChatsList

        val fullName = recentChatsList.person
        val firstName = fullName?.split(" ")?.get(0)

        holder.tvFriendName.text = recentChatsList.name

//        holder.tvMessage.text = "${firstName} : ${recentChatsList.message!!}"

        if(recentChats.image.equals("true")){
            holder.tvMessage.text = "${firstName} : photo"
        }else{
            holder.tvMessage.text = "${firstName} : ${recentChatsList.message!!}"

        }

        Log.d(TAG, "onBindViewHolder: the firend image link is ${recentChatsList.friendImage}")
        Glide.with(holder.itemView.context).load(recentChatsList.friendImage)
            .placeholder(R.drawable.person_icon).into(holder.imgFriend)

        // time of the message
        val dayOfMessage = formattedTime(recentChatsList.time!!)
        holder.tvTime.text = dayOfMessage
        holder.itemView.setOnClickListener {
            listener?.getOnRecentChatClicked(position, recentChatsList)
        }
    }

    fun setOnRecentChatsListener(listener: onRecentChatClicked) {
        this.listener = listener
    }

    fun setRecentChatList(list: List<RecentChat>) {
        this.recentChatsList = list
    }


    private fun formattedTime(dateTimeString: String): String {
        val inputFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val date = inputFormat.parse(dateTimeString)

        val calendar = Calendar.getInstance()
        val todayYear = calendar.get(Calendar.YEAR)
        val todayWeek = calendar.get(Calendar.WEEK_OF_YEAR)
        val today = calendar.get(Calendar.DAY_OF_MONTH)

        calendar.time = date
        val dateYear = calendar.get(Calendar.YEAR)
        val dateWeek = calendar.get(Calendar.WEEK_OF_YEAR)

        val dateTimeFormat = if (todayYear == dateYear) {
            if (todayWeek == dateWeek) {
                if (today == calendar.get(Calendar.DAY_OF_MONTH)) {
                    SimpleDateFormat("hh:mm a", Locale.getDefault())
                } else {
                    SimpleDateFormat("EEE hh:mm a", Locale.getDefault())
                }
            } else {
                SimpleDateFormat("MMM dd hh:mm a", Locale.getDefault())
            }
        } else {
            SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault())
        }

        return dateTimeFormat.format(date)
    }


}

class RecentChatsViewHolder(row: View) : RecyclerView.ViewHolder(row) {
    val imgFriend: CircleImageView = row.findViewById(R.id.img_friend_recent_chat_item)
    val tvFriendName: TextView = row.findViewById(R.id.tv_friend_name_recent_chat_item)
    val tvMessage: TextView = row.findViewById(R.id.tv_message__recent_chat_item)
    val tvTime: TextView = row.findViewById(R.id.tv_time_recent_chat_item)

}

interface onRecentChatClicked {
    fun getOnRecentChatClicked(position: Int, recentChatsList: RecentChat)
}