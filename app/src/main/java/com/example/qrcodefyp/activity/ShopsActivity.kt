package com.example.qrcodefyp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qrcodefyp.R
import com.example.qrcodefyp.adapter.ReceiptsAdapter
import com.example.qrcodefyp.adapter.ShopsAdapter
import com.example.qrcodefyp.databinding.ActivityShopsBinding
import com.example.qrcodefyp.model.ReceiptModel
import com.example.qrcodefyp.model.ShopModel
import com.example.qrcodefyp.util.FirebaseUtil
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ShopsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShopsBinding


    private lateinit var shopsAdapter: ShopsAdapter
    private lateinit var shopRecyclerView: RecyclerView
    private var list:MutableList<ShopModel> =ArrayList()
    private var mDatabase: FirebaseDatabase? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityShopsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        mDatabase = FirebaseDatabase.getInstance()
        Log.e("Receipts","******************************getAllReceipts********************************")
        getAllShops()
        shopsAdapter=ShopsAdapter(this){

        }
        shopRecyclerView=binding.recShops
        shopRecyclerView.layoutManager= LinearLayoutManager(this)
        shopRecyclerView.setHasFixedSize(true)
        shopRecyclerView.adapter=shopsAdapter
    }
    private fun getAllShops() {
        mDatabase!!.getReference(FirebaseUtil.DB_SHOP_REF).addValueEventListener(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){

                    for (snapshot in snapshot.children) {
                        val shopModel: ShopModel = snapshot.getValue(ShopModel::class.java)!!
                        list.add(shopModel)
                    }
                    shopsAdapter.setList(list)
                }else{
                    Log.e("Receipts","snapshot -> don't exist")
                    Toast.makeText(this@ShopsActivity,"User have no record yet!", Toast.LENGTH_SHORT).show()
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Receipts","error -> ${error.message}")
                Toast.makeText(this@ShopsActivity,"error, ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        onBackPressed()
        return super.onOptionsItemSelected(item)

    }

}