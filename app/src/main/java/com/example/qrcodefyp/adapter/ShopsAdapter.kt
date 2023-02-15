package com.example.qrcodefyp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.qrcodefyp.R
import com.example.qrcodefyp.model.ReceiptModel
import com.example.qrcodefyp.model.ShopModel

class ShopsAdapter(context: Context, function: () -> Unit): RecyclerView.Adapter<ShopsAdapter.ReceiptViewHolder>() {
    private var myReceiptsList:MutableList<ShopModel> = ArrayList()
    private val mContext=context

    class ReceiptViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        val name: TextView = itemView.findViewById(R.id.name)
        val discount: TextView = itemView.findViewById(R.id.discount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.shop_item_view, parent, false)

        return ReceiptViewHolder(view)

    }

    override fun onBindViewHolder(holder: ReceiptViewHolder, position: Int) {
        val shopModel =myReceiptsList[position]
        holder.name.text=shopModel.name
        holder.discount.text=shopModel.discount+" %"
    }

    override fun getItemCount(): Int {
       return myReceiptsList.size
    }

    fun setList(list: MutableList<ShopModel>) {
        this.myReceiptsList=list
        notifyDataSetChanged()
    }

}