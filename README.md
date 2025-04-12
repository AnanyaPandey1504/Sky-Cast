## SKY-CAST-  A Drone RTSP Video Stream Viewer App (Android)

This is an Android application developed as part of a technical task to demonstrate RTSP video streaming capabilities using the **LibVLC** library.
The app is designed to connect to a live RTSP stream (such as from a drone camera), display it within the app, and allow recording of the stream locally.
It also supports viewing the video in **Picture-in-Picture (PiP)** mode.

## ✅ Features Implemented

### 1. **RTSP Stream Playback**
- Users can enter an RTSP URL at the top of the screen.
- The stream is played using the **LibVLC** library.
- Displays the live video from the drone (or any RTSP server).

### 2. **Record Stream**
- A `Record` button is provided to start/stop recording the stream.
- Recordings are saved locally in the app’s private directory.
- The app ensures that recording only starts when the stream is playing.

### 3. **Picture-in-Picture (PiP) Mode**
- A `PiP` button launches the video in a floating window, allowing multitasking.
- Works on Android 8.0 (API 26) and above.
- Stream continues playing while in PiP mode.

---

## 📷 How to Test

> **Note:** You will need **two devices** OR use a **device + emulator setup** for testing.

### 📡 Streaming Setup
1. Install **RTSP Server** app from Google Play Store on **Device A**.
   - [RTSP Server App Link](https://play.google.com/store/apps/details?id=com.surveillancesystem.rtspserver)
2. Open the app and press **R** to start streaming.
3. Copy the generated RTSP URL (e.g. `rtsp://192.168.1.2:5540/ch0`).
4. Ensure both devices (or device + emulator) are on the **same Wi-Fi network**.

### 📲 Viewer App (This Project)
1. Install this app on **Device B** or emulator.
2. Paste the RTSP URL into the input field and press **Play**.
3. Press **Record** to save the stream locally.
4. Press **PiP** to open the floating window.

---

## 🛠️ Tech Stack

- **Language**: Kotlin
- **IDE**: Android Studio
- **Streaming Library**: [LibVLC](https://wiki.videolan.org/LibVLC/)
- **Android Features**:
  - MediaPlayer
  - Picture-in-Picture API
  - File Handling and Permissions
  - Toasts & Logging for Error Handling

