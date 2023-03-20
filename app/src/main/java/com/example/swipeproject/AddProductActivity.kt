package com.example.swipeproject

import android.content.res.Resources
import android.media.tv.TvContract.Programs
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject


class AddProductActivity : AppCompatActivity() {
    private lateinit var productType : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)



        val sendDataToApiBtn = findViewById<Button>(R.id.bt_post)
        sendDataToApiBtn.setOnClickListener {
            postDataUsingVolley()
        }

        val productTypeSpinner = findViewById<Spinner>(R.id.s_product_type)
        val productTypes = resources.getStringArray(R.array.product_types)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, productTypes)
        productTypeSpinner.adapter = adapter

        productTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                productType = productTypes[position]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }


        dismissSoftKeyboardOnBkgTap(findViewById(R.id.cl_bkg_addProj))
    }

    private fun dismissSoftKeyboardOnBkgTap(view : View) {
        if (view !is EditText) {
            view.setOnTouchListener { view, event ->
                Utils.hideSoftKeyboard(this@AddProductActivity)
                false
            }
        }

        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                dismissSoftKeyboardOnBkgTap(innerView)
            }

        }
    }


    private fun postDataUsingVolley() {

        val productNameEt = findViewById<EditText>(R.id.et_product_name)
        val productPriceEt = findViewById<EditText>(R.id.et_product_price)
        val productTaxEt = findViewById<EditText>(R.id.et_product_tax)

        val productName = productNameEt.text.toString()
        val productType = productType
        val productPrice = productPriceEt.text.toString()
        val productTax = productTaxEt.text.toString()

        val priceInt = productPrice.toIntOrNull() ?: 0

        if(productName.isEmpty() || priceInt == 0 || productTax.isEmpty()) {
            Toast.makeText(this, "please enter valid values", Toast.LENGTH_SHORT).show()
        } else {
            productNameEt.setText("")
            productPriceEt.setText("")
            productTaxEt.setText("")
            postDataUsingVolley1(productName, productType, priceInt, productTax)
        }
    }

    private fun postDataUsingVolley1(name: String, type: String, price: Int, tax: String) {
        // url to post our data
        val url = "https://app.getswipe.in/api/public/add"

        val loadingPB = findViewById<ProgressBar>(R.id.pb)
        loadingPB.visibility = View.VISIBLE

        val request: StringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                loadingPB.visibility = View.GONE

                Toast.makeText(this@AddProductActivity, "Data added to API", Toast.LENGTH_SHORT).show()
                try {

                    val respObj = JSONObject(response)
                    val message = respObj.getString("message")
                    val productId = respObj.getString("product_id")

                    val responseTV = findViewById<TextView>(R.id.tv_api_response)
                    responseTV.text = "response message : $message\nproduct id : $productId"

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener { error ->
                Toast.makeText(this@AddProductActivity,"Fail to get response = $error",Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): Map<String, String> {
                val params: HashMap<String, String> = HashMap()

                params["product_name"] = name
                params["product_type"] = type
                params["price"] = price.toString()
                params["tax"] = tax

                return params
            }
        }
        MySingleton.getInstance(this).addToRequestQueue(request)
    }
}