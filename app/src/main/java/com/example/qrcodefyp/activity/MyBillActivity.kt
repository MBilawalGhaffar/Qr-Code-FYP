package com.example.qrcodefyp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.Window
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qrcodefyp.R
import com.example.qrcodefyp.adapter.BillsAdapter

import com.example.qrcodefyp.databinding.ActivityMyBillBinding
import com.example.qrcodefyp.dialog.AddBillDialog
import com.example.qrcodefyp.dialog.AddBudgetDialog
import com.example.qrcodefyp.model.BillModel
import com.example.qrcodefyp.model.ReceiptModel
import com.example.qrcodefyp.util.FirebaseUtil
import com.google.android.material.card.MaterialCardView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MyBillActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyBillBinding
    private lateinit var billsAdapter: BillsAdapter
    private lateinit var billRecyclerView: RecyclerView
    private var list:MutableList<BillModel> =ArrayList()
    private var mDatabase: FirebaseDatabase? = null
    private lateinit var addBill: MaterialCardView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMyBillBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        mDatabase = FirebaseDatabase.getInstance()
        getAllBills()
        billsAdapter=BillsAdapter(this){

        }
        billRecyclerView=binding.recBills
        billRecyclerView.layoutManager= LinearLayoutManager(this)
        billRecyclerView.setHasFixedSize(true)
        billRecyclerView.adapter=billsAdapter

        addBill=findViewById(R.id.button_add)
        addBill.setOnClickListener {
            val billDialog= AddBillDialog(this,R.style.ReceiptDialog)
            billDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            billDialog.show()
        }

    }

    private fun getAllBills() {
        mDatabase!!.getReference(FirebaseUtil.DB_BILL_REF).child(FirebaseUtil.USER.uuid).addValueEventListener(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    Log.e("Receipts","snapshot -> exist")
                    Log.e("Receipts","snapshot -> "+snapshot.value)
                    list.clear()
                    for (snapshot in snapshot.children) {
                        val receiptModel: BillModel = snapshot.getValue(BillModel::class.java)!!
                        Log.e("Receipts","*****************snapshot**************")
                        Log.e("Receipts","id -> "+receiptModel.id)
                        Log.e("Receipts","description -> "+receiptModel.description)

                        list.add(receiptModel)
                    }
                    billsAdapter.setList(list)
                }else{
                    Log.e("Receipts","snapshot -> don't exist")
                    Toast.makeText(this@MyBillActivity,"User have no record yet!", Toast.LENGTH_SHORT).show()
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Receipts","error -> ${error.message}")
                Toast.makeText(this@MyBillActivity,"error, ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        onBackPressed()
        return super.onOptionsItemSelected(item)

    }

}