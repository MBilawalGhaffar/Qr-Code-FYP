package com.example.qrcodefyp.activity

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import com.example.qrcodefyp.R
import com.example.qrcodefyp.model.BudgetModel
import com.example.qrcodefyp.preference.BudgetPreference
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class BudgetActivity : AppCompatActivity() {
    private lateinit var tvTotalBudget:TextView
    private lateinit var tvUsedBudget:TextView
    private lateinit var tvRemainingBudget:TextView
    private lateinit var addBudget:MaterialCardView
    private lateinit var resetBudget:MaterialCardView
    private lateinit var myBudgetModel: BudgetModel

    private lateinit var alertDialogBuilder: MaterialAlertDialogBuilder


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        onBackPressed()
        return super.onOptionsItemSelected(item)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        myBudgetModel= BudgetModel()

        resetBudget=findViewById(R.id.button_reset)


        tvTotalBudget=findViewById(R.id.tv_total_sar)
        tvUsedBudget=findViewById(R.id.tv_used_sar)
        tvRemainingBudget=findViewById(R.id.tv_remaining_sar)
        getBudget()


        alertDialogBuilder=MaterialAlertDialogBuilder(this)
        alertDialogBuilder.setTitle("Budget Reset")
        alertDialogBuilder.setMessage("Are you sure you want to reset your budget?")
        alertDialogBuilder.setCancelable(true)
        alertDialogBuilder.setNegativeButton("No") { dialog, which ->
            dialog.dismiss()
        }
        alertDialogBuilder.setPositiveButton("Yes") { dialog, which ->
            val budgetModel=BudgetModel(100,0,100,"SAR")
            BudgetPreference(this).addBudget(budgetModel)
            getBudget()
            Toast.makeText(this,"Budget Reset",Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        resetBudget.setOnClickListener {
            alertDialogBuilder.show()
        }

    }

    private fun getBudget() {
        myBudgetModel=BudgetPreference(this).budget
        tvTotalBudget.text=myBudgetModel.totalBudget.toString()+" ${myBudgetModel.currency}"
        tvUsedBudget.text=myBudgetModel.usedBudget.toString()+" ${myBudgetModel.currency}"
        tvRemainingBudget.text=myBudgetModel.remainingBudget.toString()+" ${myBudgetModel.currency}"
    }
}