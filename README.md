# MeshLink — Infrastructure-Free Peer Messaging

Native Android/Kotlin prototype for nearby peer messaging.

## Included
- Compose messaging interface and local message history
- Wi-Fi Direct peer discovery and connection request API
- Bluetooth LE scanning hook for nearby peer discovery
- AES-GCM payload encryption with an app-local key
- Room persistence
- Store-and-forward routing primitives: unique message IDs, duplicate suppression, hop limit, expiry and relay-path metadata

## Build
Use JDK 17, Android SDK 35 and Gradle 8.11.1:
```powershell
gradle wrapper --gradle-version 8.11.1
# Create local.properties with your Android SDK path
.\gradlew.bat assembleDebug
```
APK: `app/build/outputs/apk/debug/app-debug.apk`

## Prototype limitations
This is not a production-ready messenger. BLE scanning currently discovers at the platform level but does not yet parse MeshLink advertisements or transport messages. Wi-Fi Direct discovery/connection requests are implemented, but group-owner negotiation, socket lifecycle, peer protocol, and end-to-end multi-hop forwarding still need integration and physical-device testing. The AES key is generated locally per app installation; secure peer key exchange, identity verification, and production key management are not implemented. Do not use for sensitive communications. Android nearby-device permissions and Wi-Fi/Bluetooth must be enabled on test devices.
