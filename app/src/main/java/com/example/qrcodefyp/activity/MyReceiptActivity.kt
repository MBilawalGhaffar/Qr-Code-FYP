package com.example.qrcodefyp.activity

import android.app.Dialog
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.Window
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.qrcodefyp.R
import com.example.qrcodefyp.adapter.ReceiptsAdapter
import com.example.qrcodefyp.databinding.ActivityMyReceiptBinding
import com.example.qrcodefyp.model.ReceiptModel
import com.example.qrcodefyp.util.FirebaseUtil
import com.example.qrcodefyp.util.ScanUtil
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

class MyReceiptActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyReceiptBinding
    private lateinit var receiptsAdapter: ReceiptsAdapter
    private lateinit var receiptRecyclerView: RecyclerView
    private var list:MutableList<ReceiptModel> =ArrayList()
    private var mDatabase: FirebaseDatabase? = null
    private val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyReceiptBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        mDatabase = FirebaseDatabase.getInstance()
        Log.e("Receipts","******************************getAllReceipts********************************")
        getAllReceipts()
        receiptsAdapter=ReceiptsAdapter(this){ url ->
            val imageDialog= Dialog(this, R.style.ReceiptDialog)
            imageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            imageDialog.setContentView(R.layout.open_image_layout)
            val imageView: ImageView =imageDialog.findViewById(R.id.receiptImage)
            imageDialog.show()

            Glide.with(this)
                .load(url)
                .into(imageView)
        }
        receiptRecyclerView=binding.recReceipts
        receiptRecyclerView.layoutManager=LinearLayoutManager(this)
        receiptRecyclerView.setHasFixedSize(true)
        receiptRecyclerView.adapter=receiptsAdapter

        val date="01/03/2023"
        val nowDate = sdf.format(Date())
        Log.d("SPLIT","date $date")
        Log.d("SPLIT","nowDate $nowDate")

        val aSplit = nowDate.split('/')
        val day=aSplit[0].toInt()
        val month=aSplit[1].toInt()
        val year=aSplit[2].toInt()
        Log.d("SPLIT","aSplit $aSplit")

        Log.d("SPLIT","bSplit ${aSplit[0]}")
        Log.d("SPLIT","bSplit ${aSplit[1]}")
        Log.d("SPLIT","bSplit ${aSplit[2]}")
        Log.d("SPLIT","day $day")
        Log.d("SPLIT","month $month")
        Log.d("SPLIT","year $year")

    }

    private fun getAllReceipts() {
        mDatabase!!.getReference(FirebaseUtil.DB_RECEIPT_REF).child(FirebaseUtil.USER.uuid).addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
//                    Log.e("Receipts","snapshot -> exist")
//                    Log.e("Receipts","snapshot -> "+snapshot.value)
                    list.clear()
                    for (snapshot in snapshot.children) {
                        val receiptModel: ReceiptModel = snapshot.getValue(ReceiptModel::class.java)!!
                        Log.e("Receipts","*****************snapshot**************")
//                        Log.e("Receipts","id -> "+receiptModel.id)
//                        Log.e("Receipts","category -> "+receiptModel.category)
//                        Log.e("Receipts","description -> "+receiptModel.description)
//                        Log.e("Receipts","expiry_date -> "+receiptModel.expiry_date)
//                        Log.e("Receipts","payment -> "+receiptModel.payment)
                        list.add(receiptModel)
                    }
                    receiptsAdapter.setList(list)
//                    for (item in list){
//
//                        val date=item.expiry_date
//                        val aSplit = date.split('/')
//                        val day=aSplit[0].toInt()
//                        val month=aSplit[1].toInt()
//                        val year=aSplit[2].toInt()
//
//                        val nowDate = sdf.format(Date())
//                        val bSplit = nowDate.split('/')
//                        val tDay=bSplit[0].toInt()
//                        val tMonth=bSplit[1].toInt()
//                        val tYear=bSplit[2].toInt()
//                        if(tDay > day && tMonth>=month ){
//                            //delete
//                            mDatabase!!.getReference(FirebaseUtil.DB_RECEIPT_REF).child(FirebaseUtil.USER.uuid).child(item.id).removeValue()
//                        }
//                    }


                }else{
                    Log.e("Receipts","snapshot -> don't exist")
                    Toast.makeText(this@MyReceiptActivity,"User have no record yet!",Toast.LENGTH_SHORT).show()
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Receipts","error -> ${error.message}")
                Toast.makeText(this@MyReceiptActivity,"error, ${error.message}",Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        onBackPressed()
        return super.onOptionsItemSelected(item)

    }
}