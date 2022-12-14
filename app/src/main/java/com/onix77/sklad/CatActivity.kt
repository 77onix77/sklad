package com.onix77.sklad

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class CatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cat)

        val text = findViewById<TextView>(R.id.catText)
        val cat = intent.getSerializableExtra("cat") as Category
        "Это ${cat.name}".also { text.text = it }

    }
}