package com.rajeshwari.meshlink.network
import android.Manifest
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
class BluetoothPeerScanner(private val context: Context) {
 private val adapter = (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
 private var callback: ScanCallback? = null
 fun startScan() {
  if (adapter == null || !adapter.isEnabled) return
  if (Build.VERSION.SDK_INT >= 31 && ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) return
  try {
   callback = object : ScanCallback() { override fun onScanResult(type: Int, result: ScanResult) { /* MeshLink advertisement parsing can be added here. */ } }
   adapter.bluetoothLeScanner?.startScan(callback)
  } catch (_: SecurityException) { }
 }
 fun stopScan() {
  try { callback?.let { adapter?.bluetoothLeScanner?.stopScan(it) } } catch (_: SecurityException) { }
  callback = null
 }
}
