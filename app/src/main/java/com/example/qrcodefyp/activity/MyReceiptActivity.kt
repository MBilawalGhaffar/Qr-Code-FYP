package com.example.qrcodefyp.activity

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qrcodefyp.adapter.ReceiptsAdapter
import com.example.qrcodefyp.databinding.ActivityMyReceiptBinding
import com.example.qrcodefyp.model.ReceiptModel
import com.example.qrcodefyp.util.FirebaseUtil
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MyReceiptActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyReceiptBinding
    private lateinit var receiptsAdapter: ReceiptsAdapter
    private lateinit var receiptRecyclerView: RecyclerView
    private var list:MutableList<ReceiptModel> =ArrayList()
    private var mDatabase: FirebaseDatabase? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyReceiptBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        mDatabase = FirebaseDatabase.getInstance()
        Log.e("Receipts","******************************getAllReceipts********************************")
        getAllReceipts()
        receiptsAdapter=ReceiptsAdapter(this){

        }
        receiptRecyclerView=binding.recReceipts
        receiptRecyclerView.layoutManager=LinearLayoutManager(this)
        receiptRecyclerView.setHasFixedSize(true)
        receiptRecyclerView.adapter=receiptsAdapter




    }

    private fun getAllReceipts() {
        mDatabase!!.getReference(FirebaseUtil.DB_RECEIPT_REF).child(FirebaseUtil.USER.uuid).addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    Log.e("Receipts","snapshot -> exist")
                    Log.e("Receipts","snapshot -> "+snapshot.value)

                    for (snapshot in snapshot.children) {
                        val receiptModel: ReceiptModel = snapshot.getValue(ReceiptModel::class.java)!!
                        Log.e("Receipts","*****************snapshot**************")
                        Log.e("Receipts","id -> "+receiptModel.id)
                        Log.e("Receipts","category -> "+receiptModel.category)
                        Log.e("Receipts","description -> "+receiptModel.description)
                        Log.e("Receipts","expiry_date -> "+receiptModel.expiry_date)
                        Log.e("Receipts","payment -> "+receiptModel.payment)
                        list.add(receiptModel)
                    }
                    receiptsAdapter.setList(list)
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