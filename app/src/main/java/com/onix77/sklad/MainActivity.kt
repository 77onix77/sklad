package com.onix77.sklad

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.onix77.sklad.databinding.ActivityMainBinding

lateinit var  binding:ActivityMainBinding
val listCat = mutableListOf(
    Category("Легковые материалы"),
    Category("Грузовые материалы"),
    Category("Грузы для металических дисков"),
    Category("Грузы для литых дисков"),
    Category("Грузы для грузовых дисков"),
    Category("Легковые материалы"),
    Category("Грузовые материалы"),
    Category("Грузы для металических дисков"),
    Category("Грузы для литых дисков"),
    Category("Грузы для грузовых дисков"),
    Category("Легковые материалы"),
    Category("Грузовые материалы"),
    Category("Грузы для металических дисков"),
    Category("Грузы для литых дисков"),
    Category("Грузы для грузовых дисков")
)

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recV.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = RecyclerAdapter(listCat, this@MainActivity)
        }

        binding.addBut.setOnClickListener {

        }



    }

}