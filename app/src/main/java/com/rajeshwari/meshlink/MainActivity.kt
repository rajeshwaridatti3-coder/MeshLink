package com.rajeshwari.meshlink

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.rajeshwari.meshlink.data.MeshDatabase
import com.rajeshwari.meshlink.data.MessageEntity
import com.rajeshwari.meshlink.network.BluetoothPeerScanner
import com.rajeshwari.meshlink.network.WifiDirectController
import com.rajeshwari.meshlink.routing.MessageRouter
import com.rajeshwari.meshlink.security.PayloadCipher
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
 private lateinit var db: MeshDatabase
 private lateinit var router: MessageRouter
 private lateinit var wifi: WifiDirectController
 private lateinit var ble: BluetoothPeerScanner
 private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { }
 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  db = MeshDatabase.get(this)
  router = MessageRouter(db.messageDao(), PayloadCipher.getOrCreate(this))
  wifi = WifiDirectController(this)
  ble = BluetoothPeerScanner(this)
  requestNearbyPermissions()
  setContent {
   val messages by db.messageDao().observeAll().collectAsState(initial = emptyList())
   MaterialTheme {
    MeshLinkScreen(
     messages = messages,
     onDiscover = { wifi.discoverPeers(); ble.startScan() },
     onStop = { ble.stopScan() },
     onSend = { body -> lifecycleScope.launch { db.messageDao().insert(router.createLocalMessage(body, "Nearby peer")) } }
    )
   }
  }
 }
 private fun requestNearbyPermissions() {
  val p = mutableListOf<String>()
  if (Build.VERSION.SDK_INT >= 33) { p += Manifest.permission.NEARBY_WIFI_DEVICES; p += Manifest.permission.BLUETOOTH_SCAN; p += Manifest.permission.BLUETOOTH_CONNECT }
  else if (Build.VERSION.SDK_INT >= 29) p += Manifest.permission.ACCESS_FINE_LOCATION
  if (p.isNotEmpty()) permissionLauncher.launch(p.toTypedArray())
 }
 override fun onDestroy() { ble.stopScan(); super.onDestroy() }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MeshLinkScreen(messages: List<MessageEntity>, onDiscover: () -> Unit, onStop: () -> Unit, onSend: (String) -> Unit) {
 var draft by remember { mutableStateOf("") }
 var status by remember { mutableStateOf("Not scanning") }
 Scaffold(topBar = { TopAppBar(title = { Text("MeshLink") }) }) { padding ->
  Column(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
   Text("Infrastructure-free peer messaging", style = MaterialTheme.typography.titleMedium)
   Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
    Button(onClick = { onDiscover(); status = "Discovery started" }) { Text("Discover peers") }
    OutlinedButton(onClick = { onStop(); status = "Scanning stopped" }) { Text("Stop") }
   }
   Text("Status: $status", style = MaterialTheme.typography.bodySmall)
   HorizontalDivider()
   Text("Local message history", style = MaterialTheme.typography.titleMedium)
   LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
    items(messages, key = { it.id }) { m ->
     ElevatedCard { Column(Modifier.padding(12.dp)) {
      Text(m.senderLabel, style = MaterialTheme.typography.labelMedium)
      Text(m.preview)
      Text("Hops: ${m.hops} • Route: ${m.route}", style = MaterialTheme.typography.bodySmall)
     } }
    }
   }
   OutlinedTextField(value = draft, onValueChange = { draft = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Write a message") })
   Button(onClick = { if (draft.isNotBlank()) { onSend(draft.trim()); draft = "" } }, modifier = Modifier.fillMaxWidth()) { Text("Save message locally") }
   Text("Peer transport is a prototype; local messages are stored on this device.", style = MaterialTheme.typography.bodySmall)
  }
 }
}
