package com.example.qrcodefyp.adapter

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.qrcodefyp.R
import com.example.qrcodefyp.dialog.OpenImageDialog
import com.example.qrcodefyp.model.ReceiptModel
import com.example.qrcodefyp.util.FirebaseUtil
import com.example.qrcodefyp.util.ScanUtil
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.io.path.fileVisitor

class ReceiptsAdapter(context: Context,val function: (url:String) -> Unit): RecyclerView.Adapter<ReceiptsAdapter.ReceiptViewHolder>() {
    private var myReceiptsList:MutableList<ReceiptModel> = ArrayList()
    private val mContext=context
    private val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    class ReceiptViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        val total: TextView = itemView.findViewById(R.id.total)
        val description: TextView = itemView.findViewById(R.id.description)
        val expiry: TextView = itemView.findViewById(R.id.expiry)
        val category: TextView = itemView.findViewById(R.id.category)
        val expired:ImageView=itemView.findViewById(R.id.expired)
        val upcoming: ImageView = itemView.findViewById(R.id.upcoming)
        val today: ImageView = itemView.findViewById(R.id.today)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.receipt_item_view, parent, false)

        return ReceiptViewHolder(view)

    }

    override fun onBindViewHolder(holder: ReceiptViewHolder, position: Int) {
        val receiptModel =myReceiptsList[position]
        holder.total.text=receiptModel.total_bill+" "+receiptModel.currency
        holder.category.text=receiptModel.category
        holder.description.text=receiptModel.description
        holder.expiry.text=receiptModel.expiry_date

        holder.itemView.setOnClickListener {

            function.invoke(receiptModel.image_url)
        }
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
        if(tDay == day && tMonth==month ){
            holder.today.visibility=View.VISIBLE
            holder.upcoming.visibility=View.GONE
            holder.expired.visibility=View.GONE
        } else if(tDay > day && tMonth>=month ){
            holder.expired.visibility=View.VISIBLE
            holder.upcoming.visibility=View.GONE
            holder.today.visibility=View.GONE

        }else{
            holder.upcoming.visibility=View.VISIBLE
            holder.today.visibility=View.GONE
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