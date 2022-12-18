package com.onix77.sklad

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.onix77.sklad.databinding.ActivityCatBinding

class CatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val cat = intent.getSerializableExtra("cat") as Category
        binding.nameCategory.text = cat.name
        binding.recVCat.apply {
            layoutManager = LinearLayoutManager(this@CatActivity)
            adapter = RecAdCat(cat.listEl, this@CatActivity)
        }

        binding.addButEl.setOnClickListener {
            val alertText = LayoutInflater.from(this).inflate(R.layout.alert_dialog_cat, null, false)
            AlertDialog.Builder(this)
                .setTitle("Создание элемента")
                .setView(alertText)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    val name = alertText.findViewById<EditText>(R.id.edTNameEl).text.toString()
                    val num = alertText.findViewById<EditText>(R.id.edTNumEl).text.toString()
                    val critNum = alertText.findViewById<EditText>(R.id.edTCritNumEl).text.toString()
                    if (name.isNotEmpty() && num.isNotEmpty() && critNum.isNotEmpty()) {
                        cat.listEl += Element(name, num.toInt(), critNum.toInt())
                    } else {
                        Toast.makeText(this, R.string.toast_cat_message, Toast.LENGTH_LONG).show()
                    }
                }
                .setNegativeButton(android.R.string.cancel, null)
                .show()

        }

    }
}