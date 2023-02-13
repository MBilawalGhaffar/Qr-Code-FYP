package com.example.qrcodefyp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.qrcodefyp.R
import com.example.qrcodefyp.model.BillModel
import com.example.qrcodefyp.model.ReceiptModel

class BillsAdapter(context: Context, function: () -> Unit): RecyclerView.Adapter<BillsAdapter.ReceiptViewHolder>() {
    private var myReceiptsList:MutableList<BillModel> = ArrayList()
    private val mContext=context

    class ReceiptViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        val total: TextView = itemView.findViewById(R.id.total)
        val description: TextView = itemView.findViewById(R.id.description)
        val expiry: TextView = itemView.findViewById(R.id.expiry)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.bill_item_view, parent, false)

        return ReceiptViewHolder(view)

    }

    override fun onBindViewHolder(holder: ReceiptViewHolder, position: Int) {
        val receiptModel =myReceiptsList[position]
        holder.total.text=receiptModel.total_bill
        holder.description.text=receiptModel.description
        holder.expiry.text=receiptModel.bill_date
    }

    override fun getItemCount(): Int {
       return myReceiptsList.size
    }

    fun setList(list: MutableList<BillModel>) {
        this.myReceiptsList=list
        notifyDataSetChanged()
    }

}