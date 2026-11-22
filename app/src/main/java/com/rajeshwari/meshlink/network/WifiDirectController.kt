package com.rajeshwari.meshlink.network
import android.content.Context
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pManager
import android.os.Looper
class WifiDirectController(context: Context) {
 private val manager = context.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
 private val channel = manager.initialize(context, Looper.getMainLooper(), null)
 fun discoverPeers(onResult: (Boolean, String?) -> Unit = { _, _ -> }) {
  try { manager.discoverPeers(channel, object : WifiP2pManager.ActionListener {
   override fun onSuccess() = onResult(true, null)
   override fun onFailure(reason: Int) = onResult(false, "Wi-Fi Direct discovery failed: $reason")
  }) } catch (_: SecurityException) { onResult(false, "Nearby Wi-Fi permission is required") }
 }
 fun connect(address: String, onResult: (Boolean, String?) -> Unit = { _, _ -> }) {
  val config = WifiP2pConfig().apply { deviceAddress = address }
  try { manager.connect(channel, config, object : WifiP2pManager.ActionListener {
   override fun onSuccess() = onResult(true, null)
   override fun onFailure(reason: Int) = onResult(false, "Connection failed: $reason")
  }) } catch (_: SecurityException) { onResult(false, "Nearby Wi-Fi permission is required") }
 }
}
