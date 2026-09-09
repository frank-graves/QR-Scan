// org/foss/lens/infrastructure/NetworkMonitor.kt
package org.foss.lens.infrastructure

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import org.foss.lens.observability.AppLogger

/**
 * Expone un Flow booleano "hay internet" sin pedir ninguna librería extra:
 * escucha callbacks de ConnectivityManager. Sirve para disparar el sync
 * offline-first cuando el dispositivo vuelve a tener red.
 */
class NetworkMonitor(private val context: Context) {

    private val connectivity: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun online(): Flow<Boolean> = callbackFlow {
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(true)
            }

            override fun onLost(network: Network) {
                trySend(false)
            }
        }

        val currentlyOnline = connectivity.activeNetwork?.let { active ->
            connectivity.getNetworkCapabilities(active)
                ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        } ?: false
        trySend(currentlyOnline)

        runCatching { connectivity.registerNetworkCallback(request, callback) }
            .onFailure { AppLogger.warn("NetworkMonitor", "Sin permiso para observar red") }
        awaitClose { runCatching { connectivity.unregisterNetworkCallback(callback) } }
    }.distinctUntilChanged()
}
