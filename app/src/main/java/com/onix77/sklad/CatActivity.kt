package com.onix77.sklad

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
    }
}