package java.devcolibri.itvdn.com.cryptoccurencytracker.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import java.devcolibri.itvdn.com.cryptoccurencytracker.R
import java.devcolibri.itvdn.com.cryptoccurencytracker.adapter.CryptoAdapter
import java.devcolibri.itvdn.com.day4cryptoccurency.pojo.Cryptocurrency
import java.devcolibri.itvdn.com.day4cryptoccurency.pojo.Data
import java.io.IOException
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.security.ProviderInstaller
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MainActivity : AppCompatActivity() {

    private lateinit var client : OkHttpClient
    private lateinit var request : Request
    private lateinit var mAdapter : CryptoAdapter
    private var mCryptocurrecncies : MutableList<Cryptocurrency>? = null

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAdapter = CryptoAdapter()

        recycler_view.adapter = mAdapter
        recycler_view.layoutManager = LinearLayoutManager(this)

        updateAndroidSecurityProvider()


        client = OkHttpClient()

        request = Request.Builder()
            .url("https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest?CMC_PRO_API_KEY=6f9002dd-d9dd-46fd-b3ee-b795f33bae97")
            .build()

        loadData()

        swipe_layout.setOnRefreshListener {
            mCryptocurrecncies!!.clear()
            loadData()
            swipe_layout.isRefreshing = false
        }

    }

    private fun loadData() {
        client.newCall(request)
            .enqueue(object : Callback {

                override fun onFailure(call: Call, e: IOException) {
                    Log.e(TAG, "onFailure: ${e}" )
                }

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body()!!.string()
                    val gson = Gson()
                    val data = gson.fromJson<Data>(body, Data::class.java)
                    mCryptocurrecncies = data.data

                    runOnUiThread({
                        mAdapter.update(mCryptocurrecncies)
                    })

                }
            })
    }

    private fun updateAndroidSecurityProvider() {
        try {
            ProviderInstaller.installIfNeeded(this)
        } catch (e: GooglePlayServicesRepairableException) {
            // Thrown when Google Play Services is not installed, up-to-date, or enabled
            // Show dialog to allow users to install, update, or otherwise enable Google Play services.
            println("Google Play Services not available.")
        } catch (e: GooglePlayServicesNotAvailableException) {
            println("Google Play Services not available.")
        }

    }

}
