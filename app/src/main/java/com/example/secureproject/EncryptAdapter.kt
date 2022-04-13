package com.example.secureproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.secureproject.database.Encrypt

class EncryptAdapter(private val datalist: List<Encrypt>): RecyclerView.Adapter<EncryptAdapter.EncryptItemViewHolder>() {
    class EncryptItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        lateinit var encrypt: Encrypt
        val plainText = view.findViewById<TextView>(R.id.plain_text)
        val encryptText = view.findViewById<TextView>(R.id.encrypt_text)

        fun bind(encrypt: Encrypt) {
            this.encrypt = encrypt
            plainText.text = encrypt.plainText
            encryptText.text = encrypt.encryptText
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EncryptItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return EncryptItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: EncryptItemViewHolder, position: Int) {
        holder.bind(datalist[position])
    }

    override fun getItemCount(): Int {
        return datalist.size
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_encrypt
    }

}