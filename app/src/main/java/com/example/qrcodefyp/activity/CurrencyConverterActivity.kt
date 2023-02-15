package com.example.qrcodefyp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import com.example.qrcodefyp.R
import com.example.qrcodefyp.databinding.ActivityCurrencyConverterBinding
import com.google.android.material.snackbar.Snackbar

class CurrencyConverterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCurrencyConverterBinding
    private var selectedItem1: String? = "AFN"
    private var selectedItem2: String? = "AFN"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityCurrencyConverterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        setUpClickLister()
        getConvertedValue()
        setUpCountryPicker()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        onBackPressed()
        return super.onOptionsItemSelected(item)

    }
    private fun setUpClickLister() {

        //on click btn
        binding.btnConvert.setOnClickListener {

            //check if the input is empty
            val numberToConvert = binding.etFirstCurrency.text.toString()

            if(numberToConvert.isEmpty() || numberToConvert == "0"){


            }else{
                doConversion()
            }
        }

    }

    private fun getConvertedValue(){

//        val formattedString = String.format("%,.2f", "ratesViewModel.convertedRate.value")

        //set the value in the second edit text field
        binding.etSecondCurrency.setText("formattedString")

        //stop progress bar
        binding.prgLoading.visibility = View.GONE
        //show button
        binding.btnConvert.visibility = View.VISIBLE

    }
    private fun setUpCountryPicker() {

        //first country picker selector
        binding.firstCountryPicker.cpViewHelper.selectedCountry.observe(this) { firstSelectedCountry ->

            //set first text view
            binding.txtFirstCurrency.text = firstSelectedCountry?.currencyCode

            selectedItem1 = firstSelectedCountry?.currencyCode

        }

        //second country picker selector
        binding.secondCountryPicker.cpViewHelper.selectedCountry.observe(this){ secondSelectedCountry ->

            //set second text view to selected country
            binding.txtSecondCurrency.text = secondSelectedCountry?.currencyCode

            selectedItem2 = secondSelectedCountry?.currencyCode

        }

    }


    private fun doConversion(){



        //make progress bar visible
        binding.prgLoading.visibility = View.VISIBLE

        //make button invisible
        binding.btnConvert.visibility = View.GONE

        //Get the data inputed
        val from = selectedItem1.toString()
        val to = selectedItem2.toString()
        val amount = binding.etFirstCurrency.text.toString().toDouble()

        //do the conversion


    }
}