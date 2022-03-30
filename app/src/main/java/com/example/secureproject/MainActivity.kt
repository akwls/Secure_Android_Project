package com.example.secureproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    lateinit var edtKey: EditText
    lateinit var plainText: EditText

    lateinit var encryptText: TextView
    lateinit var decryptionText: TextView

    lateinit var txtEncryptText: TextView
    lateinit var txtDecryptionText: TextView

    lateinit var btnReset: Button
    lateinit var btnProcess: Button
    lateinit var btnEncrypt: Button
    lateinit var btnDecrypt: Button

    lateinit var btnRecent: Button
    lateinit var btnDesc: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "3104"

        edtKey = findViewById(R.id.edt_key)
        plainText = findViewById(R.id.plain_text)
        encryptText = findViewById(R.id.encrypt_text)
        decryptionText = findViewById(R.id.decryption_text)

        txtEncryptText = findViewById(R.id.txt_encrypt_text)
        txtDecryptionText = findViewById(R.id.txt_decryption_text)

        btnReset = findViewById(R.id.btn_reset)
        btnProcess = findViewById(R.id.btn_process)
        btnEncrypt = findViewById(R.id.btn_encrypt)
        btnDecrypt = findViewById(R.id.btn_decrypt)

        btnRecent = findViewById(R.id.btn_recent)
        btnDesc = findViewById(R.id.btn_desc)

        btnEncrypt.setOnClickListener {
            txtEncryptText.visibility = View.VISIBLE
            encryptText.visibility = View.VISIBLE

            btnProcess.isEnabled = true
            btnEncrypt.visibility = View.GONE
            btnDecrypt.visibility = View.VISIBLE

        }

        btnDecrypt.setOnClickListener {
            txtDecryptionText.visibility = View.VISIBLE
            decryptionText.visibility = View.VISIBLE

            btnDecrypt.isEnabled = false
        }

        btnReset.setOnClickListener {
            edtKey.setText("")
            plainText.setText("")

            txtEncryptText.visibility = View.GONE
            encryptText.text = ""
            txtDecryptionText.visibility = View.GONE
            decryptionText.text = ""

            btnProcess.isEnabled = false
            btnDecrypt.visibility = View.GONE
            btnEncrypt.visibility = View.GONE
        }

        btnRecent.setOnClickListener {
            startActivity(Intent(this, RecentActivity::class.java))
        }

        btnDesc.setOnClickListener {
            startActivity(Intent(this, DescActivity::class.java))
        }

    }
}