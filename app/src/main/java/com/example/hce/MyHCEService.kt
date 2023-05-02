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
        val AID2 = "F0010203040506"
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

        Log.e(TAG, "onStartCommand() | NDEF: $valueToSend")
        sendDataToBrodCast("onStartCommand() | NDEF: $valueToSend")

        return Service.START_STICKY
    }

    private fun sendDataToBrodCast(data: String) {
        val intent = Intent(Utils.BrodCastAction)
        intent.putExtra("myData", data)
        sendBroadcast(intent)
    }

    override fun onDeactivated(reason: Int) {
        Log.d(TAG, "Deactivated: $reason")
    }

    override fun processCommandApdu(command: ByteArray?, extras: Bundle?): ByteArray {
        Log.e(TAG, "processCommandApdu: start")
        sendDataToBrodCast("Reader Start Process Command with Apdu :$command")
        if (command == null) {
            sendDataToBrodCast("The Command Apdu is nullable , status is :$STATUS_FAILED")
            return Utils.hexStringToByteArray(STATUS_FAILED)
        }

        val hexCommandApdu = Utils.toHex2(command)
        sendDataToBrodCast(" Apdu of reader as hex :$hexCommandApdu")

        Log.e(TAG, "processCommandApdu: hexCommandApdu ${hexCommandApdu}")

        if (hexCommandApdu.length < MIN_APDU_LENGTH) {
            Log.e(TAG, "processCommandApdu: STATUS_FAILED")
            sendDataToBrodCast("The Command Apdu is short with length ${hexCommandApdu.length},  status is :$STATUS_FAILED")

            return Utils.hexStringToByteArray(STATUS_FAILED)
        }

        if (hexCommandApdu.substring(0, 2) != DEFAULT_CLA) {
            Log.e(TAG, "processCommandApdu: CLA_NOT_SUPPORTED")
            return Utils.hexStringToByteArray(CLA_NOT_SUPPORTED)
        }

        if (hexCommandApdu.substring(2, 4) != SELECT_INS) {
            Log.e(TAG, "processCommandApdu: INS_NOT_SUPPORTED")
            return Utils.hexStringToByteArray(INS_NOT_SUPPORTED)
        }

        return if (hexCommandApdu.substring(10, 24) == AID) {
            sendDataToBrodCast(
                "Process Done With correct apdu and AID the value as hex string is: ${
                    Utils.toHex(
                        valueToSend.toByteArray()
                    )
                } "
            )

            Log.e(TAG, "processCommandApdu: ${Utils.toHex(valueToSend.toByteArray())}")
            Utils.hexStringToByteArray(Utils.toHex(valueToSend.toByteArray()))
        } else {
            sendDataToBrodCast(
                "Your application id not correct we received this ${
                    hexCommandApdu.substring(
                        10,
                        24
                    )
                }, and it must be like is : $AID , status is $STATUS_FAILED"
            )
            Utils.hexStringToByteArray(STATUS_FAILED)
        }
    }
}
