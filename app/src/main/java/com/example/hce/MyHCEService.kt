package com.example.hce

import android.app.Service
import android.content.Intent
import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.util.Log

class MyHCEService : HostApduService() {

    companion object {
        const val CHANNEL_ID = "hce_service_channel_id"
        val TAG = "Host Card Emulator"
        val STATUS_SUCCESS = "9000"
        val STATUS_FAILED = "6F00"
        val CLA_NOT_SUPPORTED = "6E00"
        val INS_NOT_SUPPORTED = "6D00"
        val AID = "A0000002471001"
        val SELECT_INS = "A4"
        val DEFAULT_CLA = "00"
        val MIN_APDU_LENGTH = 12
    }

    private var valueToSend: String = "empty value"

//    override fun onCreate() {
//        super.onCreate()
//
//        // Create a notification to display when the service is running in the foreground
//        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
//            .setContentTitle("HCE Service")
//            .setContentText("Service is running")
//            .setSmallIcon(R.drawable.ic_launcher_foreground)
//            .build()
//
//        // Start the service in the foreground
//        startForeground(1, notification)
//    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (intent.hasExtra("ndefMessage")) {
            valueToSend = intent.getStringExtra("ndefMessage") ?: ""
        }

        Log.e(TAG, "onStartCommand() | NDEF$valueToSend")

        return Service.START_STICKY
    }

    override fun onDeactivated(reason: Int) {
        Log.d(TAG, "Deactivated: $reason")
    }

    override fun processCommandApdu(commandApdu: ByteArray?, extras: Bundle?): ByteArray {
        if (commandApdu == null) {
            return Utils.hexStringToByteArray(STATUS_FAILED)
        }

        val hexCommandApdu = Utils.toHex(commandApdu)

        if (hexCommandApdu.length < MIN_APDU_LENGTH) {
            return Utils.hexStringToByteArray(STATUS_FAILED)
        }

        if (hexCommandApdu.substring(0, 2) != DEFAULT_CLA) {
            return Utils.hexStringToByteArray(CLA_NOT_SUPPORTED)
        }

        if (hexCommandApdu.substring(2, 4) != SELECT_INS) {
            return Utils.hexStringToByteArray(INS_NOT_SUPPORTED)
        }

        if (hexCommandApdu.substring(10, 24) == AID) {
            return Utils.hexStringToByteArray(
                valueToSend.toByteArray().joinToString("") { "%02x".format(it) })
        } else {
            return Utils.hexStringToByteArray(STATUS_FAILED)
        }
    }
}
