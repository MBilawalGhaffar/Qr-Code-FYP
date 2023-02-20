package com.example.qrcodefyp.activity

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.qrcodefyp.MyApplication
import com.example.qrcodefyp.R
import com.example.qrcodefyp.databinding.ActivityCurrencyConverterBinding

class CurrencyConverterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCurrencyConverterBinding
    private var selectedItem1: String? = "USD"
    private var selectedItem2: String? = "PKR"
    var dialog: Dialog? = null
private val myLink="https://api.getgeoapi.com/v2/currency/convert\n" +
        "?api_key=70a7cae2c26c8df0defbef9bf91e00f205f0aa4a\n" +
        "&from=EUR\n" +
        "&to=GBP\n" +
        "&amount=10\n" +
        "&format=json"
    private var baseUrl="https://api.getgeoapi.com/v2/currency/convert"
    private var apiKey="?api_key=70a7cae2c26c8df0defbef9bf91e00f205f0aa4a"
    private var fromKey="&from="
    private var from="EUR"
    private var toKey="&to="
    private var to="GBP"
    private var amountKey="&amount="
    private var amount= "10"
    private var format="&format=json"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityCurrencyConverterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle(R.string.Currency_Converter)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        setUpClickLister()
        setUpCountryPicker()
        dialog = Dialog(this)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setContentView(R.layout.dialog_wait1)
        dialog!!.setCanceledOnTouchOutside(false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        onBackPressed()
        return super.onOptionsItemSelected(item)

    }
    private fun setUpClickLister() {
        binding.etSecondCurrency.setText(getString(R.string.Converted_amount))
        //on click btn
        binding.btnConvert.setOnClickListener {

            val numberToConvert = binding.etFirstCurrency.text.toString()
            if(numberToConvert.isEmpty() || numberToConvert == "0"){
                Toast.makeText(this,getString(R.string.Enter_Amount),Toast.LENGTH_SHORT).show()
            }else{
                doConversion()
            }
        }

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
        dialog!!.show()
        //Get the data inputed
        val mFrom = selectedItem1.toString()
        val mTo = selectedItem2.toString()
        val mAmount = binding.etFirstCurrency.text.toString().toDouble()

        //do the conversion
        val myLink=baseUrl+apiKey+fromKey+mFrom+toKey+mTo+amountKey+mAmount+format
        val queue: RequestQueue = Volley.newRequestQueue(applicationContext)


        val request = JsonObjectRequest(Request.Method.GET, myLink, null, { response ->
            Log.e("TAG_API", "RESPONSE IS $response")
            try {
                if(response.get("status").equals("success")){
                    var myResponse=response.getJSONObject("rates").getJSONObject(mTo).get("rate_for_amount")
//                    val formattedString = String.format("%,.2f", myResponse.toString())
                    binding.etSecondCurrency.text = myResponse.toString()
                    dialog!!.dismiss()
                }else{
                    Toast.makeText(this@CurrencyConverterActivity, getString(R.string.Fail_response), Toast.LENGTH_SHORT)
                        .show()
                    dialog!!.dismiss()
                }


            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@CurrencyConverterActivity, getString(R.string.Fail_response), Toast.LENGTH_SHORT).show()
                dialog!!.dismiss()
            }

        },{ error ->
            Log.e("TAG_API", "RESPONSE IS $error")
            Toast.makeText(this@CurrencyConverterActivity, getString(R.string.Fail_response), Toast.LENGTH_SHORT).show()
            dialog!!.dismiss()
        })
        queue.add(request)
    }
}