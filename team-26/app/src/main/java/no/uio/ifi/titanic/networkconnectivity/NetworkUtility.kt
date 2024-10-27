package no.uio.ifi.titanic.networkconnectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL


object NetworkUtility {
    private const val TAG = "NetworkUtility"
    private val _networkAvailable = MutableStateFlow(true)
    val networkAvailable: StateFlow<Boolean> = _networkAvailable.asStateFlow()
    private var debounceJob: Job? = null
    private const val DEBOUNCE_VALUE = 10_000L // 1 seconds delay
    private const val CHECK_URL = "https://www.google.com" // Reliable endpoint

    //pings google.com to see if communication over the internet is successful
    fun isInternetAvailable(): Boolean {
        return try {
            (URL(CHECK_URL).openConnection() as HttpURLConnection).run {
                connectTimeout = 5000
                connect()
                responseCode == 200
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking internet connection: $e")
            false
        }
    }

    //Specifies behaviour of the different network states
    fun registerNetworkCallback(context: Context) {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                Log.d(TAG, "Network is available")
                debounceJob?.cancel()  // Cancel any ongoing debounce delay
                _networkAvailable.value = true
            }

            override fun onLost(network: Network) {
                Log.d(TAG, "Network is lost")
                debounceJob?.cancel()
                debounceJob = CoroutineScope(Dispatchers.IO).launch {
                    delay(DEBOUNCE_VALUE)
                    if (!isInternetAvailable()) {
                        _networkAvailable.value = false
                    }
                }
            }

            override fun onUnavailable() {
                Log.d(TAG, "Network is unavailable")
                debounceJob?.cancel()
                debounceJob = CoroutineScope(Dispatchers.IO).launch {
                    delay(DEBOUNCE_VALUE)
                    if (!isInternetAvailable()) {
                        _networkAvailable.value = false
                    }
                }
            }
        }

        // Register the network callback
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
        Log.d(TAG, "Network callback registered")
    }
}