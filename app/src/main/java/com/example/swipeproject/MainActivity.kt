package com.example.swipeproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest

class MainActivity : AppCompatActivity() {
    private lateinit var mAdapter : ProductListAdapter
    private lateinit var productArray : ArrayList<Product>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val recyclerView = findViewById<RecyclerView>(R.id.rv_product_list)
        recyclerView.layoutManager = LinearLayoutManager(this)
        fetch()
        mAdapter =  ProductListAdapter()
        recyclerView.adapter = mAdapter


        val navToAddProduct = findViewById<Button>(R.id.NavToAddProduct)
        navToAddProduct.setOnClickListener {
            startActivity(Intent(this, AddProductActivity::class.java))
        }


        val searchProductBtn = findViewById<ImageView>(R.id.bt_search_product)
        searchProductBtn.setOnClickListener {
            val productNameEt = findViewById<EditText>(R.id.et_product_name)
            val productName = productNameEt.text.toString()

            if(productName.isEmpty()) Toast.makeText(this, "Please enter a product name", Toast.LENGTH_SHORT).show()
            else {

                val productIndex = searchProduct(productArray, productName)

                if (productIndex != -1) {
                    mAdapter.updateProducts(arrayListOf(productArray[productIndex]))
                } else {
                    Toast.makeText(this, "ops, product not found", Toast.LENGTH_SHORT).show()
                }
            }
        }


        val clearTextBtn = findViewById<ImageView>(R.id.bt_clear_text)
        clearTextBtn.setOnClickListener {
            val productNameEt = findViewById<EditText>(R.id.et_product_name)
            productNameEt.setText("")
            fetch()
        }

        dismissSoftKeyboardOnBkgTap(findViewById(R.id.cl_bkg))
    }

    override fun onResume() {
        super.onResume()
        fetch()
    }


    private fun dismissSoftKeyboardOnBkgTap(view: View) {
        if (view !is EditText) {
            view.setOnTouchListener { view, event ->
                Utils.hideSoftKeyboard(this@MainActivity)
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

    private fun searchProduct(productArray: java.util.ArrayList<Product>, productName : String): Int {
        for(i in 0 until productArray.size) {
            if(productArray[i].productName == productName) return i
        }
        return -1;
    }

    private fun fetch() {
        val url = "https://app.getswipe.in/api/public/get"
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null, { response ->
                productArray = ArrayList()

                for(i in 0 until response.length()) {
                    val productJsonObject= response.getJSONObject(i)
                    val product = Product (
                        productJsonObject.getString("image"),
                        productJsonObject.getString("product_name"),
                        productJsonObject.getString("product_type"),
                        productJsonObject.getString("price").toFloat(),
                        productJsonObject.getString("tax").toFloat()
                    )

                    productArray.add(product)
                }

                mAdapter.updateProducts(productArray)
            }, {
                Toast.makeText(this, "something went wrong with the network call", Toast.LENGTH_SHORT).show()
            }
        )

        MySingleton.getInstance(this).addToRequestQueue(jsonArrayRequest)
    }
}