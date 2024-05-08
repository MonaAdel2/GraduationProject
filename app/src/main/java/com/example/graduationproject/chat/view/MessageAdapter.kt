package com.example.graduationproject.chat.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.graduationproject.R
import com.example.graduationproject.Utils
import com.example.graduationproject.chat.model.Message
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MessageAdapter : RecyclerView.Adapter<MyViewHolder>() {

    private val TAG = "MessageAdapter"
    var messagesList = listOf<Message>()
    val SENDER = 0
    val RECIEVER = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var inflater = LayoutInflater.from(parent.context)
        return if (viewType == SENDER) {
            val view = inflater.inflate(R.layout.sender_row, parent, false)
            MyViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.reciever_row, parent, false)
            MyViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return messagesList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val message = messagesList[position]
        holder.message.visibility = View.VISIBLE
        holder.time.visibility = View.VISIBLE

        holder.message.text = message.message
        val dayOfMessage = formattedTime(message.time!!)
        holder.time.text = dayOfMessage

    }

    fun setMessageList(list: List<Message>) {
        this.messagesList = list
    }

    override fun getItemViewType(position: Int): Int =
        if (messagesList[position].sender == Utils.getUidLoggedIn()) SENDER else RECIEVER


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

class MyViewHolder(row: View) : RecyclerView.ViewHolder(row) {
    val message: TextView = row.findViewById(R.id.tv_message_item)
    val time: TextView = row.findViewById(R.id.tv_msg_time)
}