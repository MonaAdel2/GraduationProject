package com.example.graduationproject.company

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.graduationproject.R
import com.example.graduationproject.authentication.signup.model.UserData
import com.example.graduationproject.company.model.CompanyData


class CompaniesAdapter(private val data: List<CompanyData>, private val context: Context): RecyclerView.Adapter<CompaniesAdapter.myHolder>() {
   // private lateinit var onItemClickListener: CompaniesAdapter.OnItemClickListener

  /* interface  OnItemClickListener{
        fun onItemClicked(companyData: CompanyData)
    }
    fun setOnClickListener(listener: OnItemClickListener){
        onItemClickListener = listener
    }*/
    class myHolder(row: View/*, onItemClickListener: OnItemClickListener*/): RecyclerView.ViewHolder(row){
        var companyName= row.findViewById<TextView>(R.id.tv_company_name)
        var viewCompany= row.findViewById<ImageView>(R.id.iv_view_company)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myHolder {
        val row = LayoutInflater.from(parent.context).inflate(R.layout.company_item,parent,false)
        return myHolder(row /*onItemClickListener*/)
    }

    override fun getItemCount(): Int {
        Log.d("companiesAdapter", "getItemSize: ${data}")
        return data.size
    }

    override fun onBindViewHolder(holder: myHolder, position: Int) {

        holder.companyName.text= data[position].Name


    /*   holder.viewCompany.setOnClickListener {
            onItemClickListener.onItemClicked(data[position])
        }*/
    }
}
