package com.example.qrcodefyp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.qrcodefyp.R
import com.example.qrcodefyp.model.BillModel
import com.example.qrcodefyp.model.ReceiptModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class BillsAdapter(context: Context, function: () -> Unit): RecyclerView.Adapter<BillsAdapter.ReceiptViewHolder>() {
    private var myReceiptsList:MutableList<BillModel> = ArrayList()
    private val mContext=context
    private val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    class ReceiptViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        val total: TextView = itemView.findViewById(R.id.total)
        val description: TextView = itemView.findViewById(R.id.description)
        val expiry: TextView = itemView.findViewById(R.id.expiry)
        val upcoming: ImageView = itemView.findViewById(R.id.upcoming)
        val today: ImageView = itemView.findViewById(R.id.today)
        val expired: ImageView = itemView.findViewById(R.id.expired)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.bill_item_view, parent, false)

        return ReceiptViewHolder(view)

    }

    override fun onBindViewHolder(holder: ReceiptViewHolder, position: Int) {
        val receiptModel =myReceiptsList[position]
        holder.total.text=receiptModel.total_bill+" SAR"
        holder.description.text=receiptModel.description
        holder.expiry.text=receiptModel.bill_date

        val date=receiptModel.bill_date
        val aSplit = date.split('/')
        val day=aSplit[0].toInt()
        val month=aSplit[1].toInt()
        val year=aSplit[2].toInt()

        val nowDate = sdf.format(Date())
        val bSplit = nowDate.split('/')
        val tDay=bSplit[0].toInt()
        val tMonth=bSplit[1].toInt()
        val tYear=bSplit[2].toInt()
        if(tDay == day && tMonth==month ){
            holder.today.visibility=View.VISIBLE
            holder.upcoming.visibility=View.GONE
            holder.expired.visibility=View.GONE
        }else if(tDay > day && tMonth>=month ){
            holder.expired.visibility=View.VISIBLE
            holder.upcoming.visibility=View.GONE
            holder.today.visibility=View.GONE
        } else{
            holder.upcoming.visibility=View.VISIBLE
            holder.today.visibility=View.GONE
            holder.expired.visibility=View.GONE
        }

    }

    override fun getItemCount(): Int {
       return myReceiptsList.size
    }

    fun setList(list: MutableList<BillModel>) {
        this.myReceiptsList=list
        notifyDataSetChanged()
    }

}