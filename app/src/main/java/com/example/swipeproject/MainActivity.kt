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

        // initialising and populating recycler view
        val recyclerView = findViewById<RecyclerView>(R.id.rv_product_list)
        recyclerView.layoutManager = LinearLayoutManager(this)
        fetch()
        mAdapter =  ProductListAdapter()
        recyclerView.adapter = mAdapter

        // switching to AddProductActivity using Intent
        val navToAddProduct = findViewById<Button>(R.id.NavToAddProduct)
        navToAddProduct.setOnClickListener {
            startActivity(Intent(this, AddProductActivity::class.java))
        }


        // searching for product
        val searchProductBtn = findViewById<ImageView>(R.id.bt_search_product)
        searchProductBtn.setOnClickListener {
            val productNameEt = findViewById<EditText>(R.id.et_product_name)
            val productName = productNameEt.text.toString()

            // validating product name isn't empty
            if(productName.isEmpty()) Toast.makeText(this, "Please enter a product name", Toast.LENGTH_SHORT).show()
            else {

                // searchProduct() takes productArray and productName as arguments
                val productIndex = searchProduct(productArray, productName)

                if (productIndex != -1) {
                    // productName is available in productArray
                    mAdapter.updateProducts(arrayListOf(productArray[productIndex]))
                } else {
                    // product name is not available, make a Toast message for the user
                    Toast.makeText(this, "ops, product not found", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // to clear the search Product input field
        val clearTextBtn = findViewById<ImageView>(R.id.bt_clear_text)
        clearTextBtn.setOnClickListener {
            val productNameEt = findViewById<EditText>(R.id.et_product_name)
            productNameEt.setText("")

            // after clearing the search product field, reloading all the products in recycler view
            fetch()
        }

        // to enhance the UX, whenever the touch is made outside the EditText, the soft keyboard
        // should be dismissed
        dismissSoftKeyboardOnBkgTap(findViewById(R.id.cl_bkg))
    }

    override fun onResume() {
        super.onResume()

        // reloading all the products in recycler view, when the activity is again reloaded.
        fetch()
    }


    private fun dismissSoftKeyboardOnBkgTap(view: View) {

        // if the view doesn't include EditText
        if (view !is EditText) {
            view.setOnTouchListener { view, event ->
                Utils.hideSoftKeyboard(this@MainActivity)
                false
            }
        }

        // recursively exploring all the child views if the view provided is a viewGroup
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                dismissSoftKeyboardOnBkgTap(innerView)
            }

        }
    }

    private fun searchProduct(productArray: java.util.ArrayList<Product>, productName : String): Int {
        // loop through the entire productArray and check if any product with productName is present
        for(i in 0 until productArray.size) {
            if(productArray[i].productName == productName) return i
        }

        // if not then return -1
        return -1;
    }

    private fun fetch() {

        // function to make volley GET request :

        // url to get data from
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

                // calling updateProducts to populate the recycler view
                mAdapter.updateProducts(productArray)
            }, {
                Toast.makeText(this, "something went wrong with the network call", Toast.LENGTH_SHORT).show()
            }
        )

        // using singleton function getInstance, and then using it putting request to Request Queue
        MySingleton.getInstance(this).addToRequestQueue(jsonArrayRequest)
    }
}