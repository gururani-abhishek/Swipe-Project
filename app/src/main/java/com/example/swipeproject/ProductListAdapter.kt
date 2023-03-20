package com.example.swipeproject

import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.w3c.dom.Text

class ProductListAdapter : RecyclerView.Adapter<ProductListAdapter.ViewHolder>() {
    private val items : ArrayList<Product> = ArrayList()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var productImage : ImageView = view.findViewById(R.id.iv_product)
        var productName : TextView = view.findViewById(R.id.tv_product_name)
        var productType : TextView = view.findViewById(R.id.tv_product_type)
        var productPrice : TextView = view.findViewById(R.id.tv_product_price)
        var productTax : TextView = view.findViewById(R.id.tv_product_tax)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.product,parent, false))

    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = items[position]

        val imageUrl = currentItem.imageUrl
        val defaultImageUrl = "https://user-images.githubusercontent.com/59947871/226199368-4afaf573-5779-4ec0-831c-9c8f22553312.png"

//        when image url is empty :
        if(imageUrl == "") {
            Glide.with(holder.itemView.context).load(defaultImageUrl).into(holder.productImage)
        } else {
            Glide.with(holder.itemView.context).load(imageUrl).into(holder.productImage)
        }

        holder.productName.text = currentItem.productName
        holder.productType.text = currentItem.productType
        holder.productPrice.text = currentItem.productPrice.toString()
        holder.productTax.text = currentItem.productTax.toString()

    }

    fun updateProducts(productArray: java.util.ArrayList<Product>) {
        items.clear()
        items.addAll(productArray)

        notifyDataSetChanged()
    }
}