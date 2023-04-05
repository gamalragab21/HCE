package com.example.hce

import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.hce.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var nfcAdapter: NfcAdapter
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        binding.button.setOnClickListener {
            val intent = Intent(this@MainActivity, MyHCEService::class.java)
            intent.putExtra("ndefMessage", binding.editText.text.toString())
            startService(intent)
        }
    }
}