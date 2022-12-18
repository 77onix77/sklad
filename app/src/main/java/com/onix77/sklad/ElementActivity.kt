package com.onix77.sklad

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.onix77.sklad.databinding.ActivityElementBinding

class ElementActivity : AppCompatActivity() {
    private lateinit var binding: ActivityElementBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityElementBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}