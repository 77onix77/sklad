package com.onix77.sklad


import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager

import com.onix77.sklad.databinding.ActivityMainBinding


val listCat = mutableListOf(
    Category("Легковые материалы"),
    Category("Грузовые материалы"),
    Category("Грузы для металических дисков"),
    Category("Грузы для литых дисков"),
    Category("Грузы для грузовых дисков")
)

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listCat[0].addElement(Element("латка UP3", 300, 100))
        listCat[0].addElement(Element("латка UP4.5", 80, 100))
        listCat[0].addElement(Element("латка UP6", 100, 50))
        listCat[0].addElement(Element("пластырь R11", 10, 20))
        listCat[0].addElement(Element("пластырь R12", 50, 20))
        listCat[0].addElement(Element("пластырь R20", 50, 20))

        binding.recV.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = RecyclerAdapter(listCat, this@MainActivity)
        }

        binding.addBut.setOnClickListener {
            val alertText = LayoutInflater.from(this).inflate(R.layout.alert_dialog_main, null, false)
            AlertDialog.Builder(this)
                .setTitle("Введите название категории")
                .setView(alertText)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    val text = alertText.findViewById<EditText>(R.id.edTNameCat).text.toString()
                    if (text.isNotEmpty()) {
                        listCat += Category(text)
                    } else {
                        Toast.makeText(this, R.string.no_tile, Toast.LENGTH_LONG).show()
                    }
                }
                .setNegativeButton(android.R.string.cancel, null)
                .show()
        }
    }
}