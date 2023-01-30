package com.onix77.sklad


import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager

import com.onix77.sklad.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val listCat = mutableListOf<String>()
        val db = MainDB.getDB(this)
        val getDB = Thread { listCat += db.getDao().getCat().toMutableList() }
        getDB.start()
        getDB.join()

        binding.recV.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = RecyclerAdapter(listCat, this@MainActivity)
        }

        binding.historyBt.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        binding.addBut.setOnClickListener {
            val alertText = LayoutInflater.from(this).inflate(R.layout.alert_dialog_main, null, false)
            AlertDialog.Builder(this)
                .setTitle("Введите название категории")
                .setView(alertText)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    val text = alertText.findViewById<EditText>(R.id.edTNameCat).text.toString()
                    if (text.isNotEmpty()) {
                        listCat += text
                        binding.recV.adapter!!.notifyItemInserted(listCat.lastIndex)
                        Toast.makeText(this, R.string.toast_main_message, Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, R.string.no_tile, Toast.LENGTH_LONG).show()
                    }
                }
                .setNegativeButton(android.R.string.cancel, null)
                .show()
        }
    }
}