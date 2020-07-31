package ml.arseniy899.bankaccounttest.viewModel

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build


class NetworkStateReceiver : BroadcastReceiver()
{
	
	protected var listeners: MutableSet<NetworkStateReceiverListener>
	protected var connected: Boolean? = null
	
	init
	{
		listeners = HashSet()
		connected = null
	}
	
	override fun onReceive(context: Context, intent: Intent?)
	{
		if (intent == null || intent.extras == null) return
		
		val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
		connected = isInternetAvailable(context)
		
		
		notifyStateToAll()
	}
	private fun isInternetAvailable(context: Context): Boolean {
		var result = false
		val connectivityManager =
			context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			val networkCapabilities = connectivityManager.activeNetwork ?: return false
			val actNw =
				connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
			result = when {
				actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
				actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
				actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
				else -> false
			}
		} else {
			connectivityManager.run {
				connectivityManager.activeNetworkInfo?.run {
					result = when (type) {
						ConnectivityManager.TYPE_WIFI -> true
						ConnectivityManager.TYPE_MOBILE -> true
						ConnectivityManager.TYPE_ETHERNET -> true
						else -> false
					}
					
				}
			}
		}
		
		return result
	}
	private fun notifyStateToAll()
	{
		for (listener in listeners) notifyState(listener)
	}
	
	private fun notifyState(listener: NetworkStateReceiverListener?)
	{
		if (connected == null || listener == null) return
		
		if (connected == true) listener.networkAvailable()
		else listener.networkUnavailable()
	}
	
	fun addListener(l: NetworkStateReceiverListener)
	{
		listeners.add(l)
		notifyState(l)
	}
	
	fun removeListener(l: NetworkStateReceiverListener)
	{
		listeners.remove(l)
	}
	
	interface NetworkStateReceiverListener
	{
		fun networkAvailable()
		fun networkUnavailable()
	}
}