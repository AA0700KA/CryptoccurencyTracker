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

//        val sslcontext = SSLContext.getInstance("TLSv1")
//        sslcontext.init(null, null, null)
//        val NoSSLv3Factory = NoSSLv3SocketFactory(sslcontext.getSocketFactory())
//
//        HttpsURLConnection.setDefaultSSLSocketFactory(NoSSLv3Factory)


        mAdapter = CryptoAdapter()

        val firebaseDatabase = FirebaseDatabase.getInstance().reference

        firebaseDatabase.child("currency").child("ABBC").addValueEventListener(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "onCancelled: ${error}")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.value
                Log.d(TAG, "onDataChange: ${value}")
            }

        })


        recycler_view.adapter = mAdapter
        recycler_view.layoutManager = LinearLayoutManager(this)

        updateAndroidSecurityProvider()



//        val spec = ConnectionSpec.Builder(ConnectionSpec.COMPATIBLE_TLS)
//            .tlsVersions(TlsVersion.TLS_1_2, TlsVersion.TLS_1_1, TlsVersion.TLS_1_0)
//            .cipherSuites(
//                CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
//                CipherSuite.TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256,
//                CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA,
//                CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA
//            )
//            .build()
//
//        val builder = OkHttpClient.Builder()
//            .connectionSpecs(Collections.singletonList(spec))

//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//            Log.e(TAG, "onCreate: Version Lower than 5.0" )
//            HttpsURLConnection.setDefaultSSLSocketFactory(TLSSocketFactory())
//        }

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


//                    for (cryptocurrency in cryptocurrencies) {
//                        Log.d(TAG, "${cryptocurrency.id}, ${cryptocurrency.name}, ${cryptocurrency.symbol}, ${cryptocurrency.quote!!.USD!!.price}")
//                    }
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
