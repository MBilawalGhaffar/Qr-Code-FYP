package com.example.qrcodefyp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.qrcodefyp.R
import com.example.qrcodefyp.model.ReceiptModel

class ReceiptsAdapter(context: Context, function: () -> Unit): RecyclerView.Adapter<ReceiptsAdapter.ReceiptViewHolder>() {
    private var myReceiptsList:MutableList<ReceiptModel> = ArrayList()
    private val mContext=context

    class ReceiptViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        val total: TextView = itemView.findViewById(R.id.total)
        val description: TextView = itemView.findViewById(R.id.description)
        val expiry: TextView = itemView.findViewById(R.id.expiry)
        val category: TextView = itemView.findViewById(R.id.category)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.receipt_item_view, parent, false)

        return ReceiptViewHolder(view)

    }

    override fun onBindViewHolder(holder: ReceiptViewHolder, position: Int) {
        val receiptModel =myReceiptsList[position]
        holder.total.text=receiptModel.total_bill
        holder.category.text=receiptModel.category
        holder.description.text=receiptModel.description
        holder.expiry.text=receiptModel.expiry_date
    }

    override fun getItemCount(): Int {
       return myReceiptsList.size
    }

    fun setList(list: MutableList<ReceiptModel>) {
        this.myReceiptsList=list
        notifyDataSetChanged()
    }

}