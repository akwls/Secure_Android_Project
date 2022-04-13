package com.example.secureproject

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.secureproject.database.Encrypt
import com.example.secureproject.database.EncryptDatabase
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.*

class RecentActivity : AppCompatActivity() {
    lateinit var db: EncryptDatabase
    lateinit var encryptList: MutableList<Encrypt>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recent)
        title = "3104"

        db = EncryptDatabase.getInstance(this)

        var adapter:EncryptAdapter? = null
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                encryptList = db.encryptDAO().getAll().toMutableList()
                withContext(Dispatchers.Main) {
                    val layoutManager = LinearLayoutManager(applicationContext)

                    adapter = EncryptAdapter(encryptList)

                    val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
                    recyclerView.setHasFixedSize(false)
                    recyclerView.layoutManager = layoutManager
                    recyclerView.adapter = adapter
                    findViewById<FloatingActionButton>(R.id.btn_delete).setOnClickListener {
                        CoroutineScope(Dispatchers.Main).launch {
                            async(Dispatchers.IO) {
                                db.encryptDAO().deleteAll()
                            }.await()
                            encryptList.clear()
                            recyclerView.adapter?.notifyDataSetChanged()
                        }


                    }
                }
            }
        }

    }
}