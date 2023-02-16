package com.example.qrcodefyp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.qrcodefyp.R
import com.example.qrcodefyp.model.ReceiptModel
import com.example.qrcodefyp.util.FirebaseUtil
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ReceiptsAdapter(context: Context, function: () -> Unit): RecyclerView.Adapter<ReceiptsAdapter.ReceiptViewHolder>() {
    private var myReceiptsList:MutableList<ReceiptModel> = ArrayList()
    private val mContext=context
    private val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    class ReceiptViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        val total: TextView = itemView.findViewById(R.id.total)
        val description: TextView = itemView.findViewById(R.id.description)
        val expiry: TextView = itemView.findViewById(R.id.expiry)
        val category: TextView = itemView.findViewById(R.id.category)
        val expired:ImageView=itemView.findViewById(R.id.expired)
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
        val date=receiptModel.expiry_date
        val aSplit = date.split('/')
        val day=aSplit[0].toInt()
        val month=aSplit[1].toInt()
        val year=aSplit[2].toInt()

        val nowDate = sdf.format(Date())
        val bSplit = nowDate.split('/')
        val tDay=bSplit[0].toInt()
        val tMonth=bSplit[1].toInt()
        val tYear=bSplit[2].toInt()
        if(tDay > day && tMonth>=month ){
            holder.expired.visibility=View.VISIBLE
        }else{
            holder.expired.visibility=View.GONE
        }

    }

    override fun getItemCount(): Int {
       return myReceiptsList.size
    }

    fun setList(list: MutableList<ReceiptModel>) {
        this.myReceiptsList=list
        notifyDataSetChanged()
    }

}