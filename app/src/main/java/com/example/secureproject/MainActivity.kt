package com.example.secureproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    lateinit var EdtKey: EditText
    lateinit var PlainText: EditText
    lateinit var CypherText: EditText
    lateinit var DecryptionText: EditText

    lateinit var TxtCypherText: TextView
    lateinit var TxtDecryptionText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "3104"



    }
}