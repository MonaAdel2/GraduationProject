package com.example.graduationproject.aboutUs.view

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.graduationproject.R
import com.example.graduationproject.aboutUs.AboutUsData
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.hdodenhof.circleimageview.CircleImageView

class AboutUsAdapter(var data: List<AboutUsData>): RecyclerView.Adapter<AboutUSViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AboutUSViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.about_us_item, parent, false)
        return AboutUSViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: AboutUSViewHolder, position: Int) {
        holder.name.text = data[position].name
        holder.email.text = data[position].email
        holder.photo.setImageResource(data[position].photo)
        holder.linkedin.setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(data[position].linkedin))
            holder.itemView.context.startActivity(intent)
        }
    }
}

class AboutUSViewHolder(row: View) : RecyclerView.ViewHolder(row) {
    val photo: CircleImageView = row.findViewById(R.id.img_photo)
    val name: TextView = row.findViewById(R.id.tv_name_about)
    val email: TextView = row.findViewById(R.id.tv_email_about)
    val linkedin: FloatingActionButton = row.findViewById(R.id.btn_linkedin)

}
