package java.devcolibri.itvdn.com.cryptoccurencytracker.adapter

import android.content.Context
import android.media.Image

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.cryptoccurency_item.view.*
import java.devcolibri.itvdn.com.cryptoccurencytracker.R


import java.devcolibri.itvdn.com.day4cryptoccurency.pojo.Cryptocurrency

class CryptoAdapter : RecyclerView.Adapter<CryptoAdapter.CryptoHolder>() {

    class CryptoHolder(val view : View) : RecyclerView.ViewHolder(view)

    private var items: List<Cryptocurrency>? = ArrayList<Cryptocurrency>()
    private val TAG = "CryptoAdapter"
    private val mDatabase = FirebaseDatabase.getInstance().reference

    fun update(items: List<Cryptocurrency>?) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CryptoHolder {
        Log.d(TAG, "onCreateViewHolder: ")
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cryptoccurency_item, parent, false)
        return CryptoHolder(view)
    }

    override fun getItemCount(): Int {
        return items!!.size
    }

    override fun onBindViewHolder(holder: CryptoHolder, position: Int) {
        
        val currency = items!![position]
        Log.d(TAG, "onBindViewHolder: ${currency.name}")
        with(holder.view) {
            crp.text = currency.symbol
            crypto_name.text = currency.name
            course.text = currency.quote!!.USD!!.price.toString()
            val percent1h = currency.quote.USD!!.percent_change_1h
            hours1_stats.text = "1h:${percent1h}%"
            val percent24h = currency.quote.USD.percent_change_24h
            hours24_stats.text = "24h:${percent24h}%"
            val percent7d = currency.quote.USD.percent_change_7d
            d7_stats.text = "7d:${percent7d}%"

            hours1_stats.setTextColor(color(context, percent1h) )
            d7_stats.setTextColor(color(context, percent7d) )
            hours24_stats.setTextColor(color(context, percent24h) )

            loadImage(context, image, currency.symbol!!)

        }

    }

    private fun loadImage(context: Context, image : ImageView, imgSymbol : String) {
        mDatabase.child("currency").child(imgSymbol).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "onCancelled: ${error}")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val imageUrl = snapshot.getValue(String::class.java)
                Picasso.with(context).load(imageUrl).into(image)
            }

        })
    }

    private fun color(context : Context, value : Double) : Int  =
        if (value > 0) {
            context.resources.getColor(R.color.green)
        } else {
            context.resources.getColor(R.color.red)
        }




}