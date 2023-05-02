package com.example.hce

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.hce.Utils.BrodCastAction
import com.example.hce.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var nfcAdapter: NfcAdapter
    private lateinit var binding: ActivityMainBinding

    private val myBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val data = intent?.getStringExtra("myData")
            binding.statusTv.apply {
                text="$text \n $data"
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.editText.requestFocus()
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        binding.button.setOnClickListener {
            binding.statusTv.text=""
            val intent = Intent(this@MainActivity, MyHCEService::class.java)
            intent.putExtra("ndefMessage", binding.editText.text.toString())
            startService(intent)
        }

        registerReceiver(myBroadcastReceiver, IntentFilter(BrodCastAction))
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(myBroadcastReceiver)
    }
}