package com.onix77.sklad

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.onix77.sklad.databinding.ActivityCatBinding
import androidx.activity.viewModels
import androidx.recyclerview.widget.DiffUtil

class CatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCatBinding

    private val  myViewModel: MyViewModel by viewModels {
        MyViewModelFactory((application as MyApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val cat = intent.getStringExtra("cat")!!
        val list = mutableListOf<ElementDB>()

        binding.nameCategory.text = cat
        binding.recVCat.apply {
            layoutManager = LinearLayoutManager(this@CatActivity)
            adapter = RecAdCat(list, this@CatActivity)
        }

        myViewModel.getEl(cat).observe(this) {
            val difUtil = CatDiffUtils(list, it)
            val difResult = DiffUtil.calculateDiff(difUtil)
            list.clear()
            list += it
            binding.recVCat.adapter?.let { it2 -> difResult.dispatchUpdatesTo(it2) }
        }

        binding.addButEl.setOnClickListener {
            val alertText = LayoutInflater.from(this).inflate(R.layout.alert_dialog_cat, null, false)
            AlertDialog.Builder(this)
                .setTitle(R.string.create_element)
                .setView(alertText)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    val name = alertText.findViewById<EditText>(R.id.edTNameEl).text.toString()
                    val num = alertText.findViewById<EditText>(R.id.edTNumEl).text.toString()
                    val crNum = alertText.findViewById<EditText>(R.id.edTCritNumEl).text.toString()
                    if (name.isNotEmpty() && num.isNotEmpty() && crNum.isNotEmpty()) {

                        myViewModel.insertEl(ElementDB(
                            null,
                            cat, name,
                            num.toInt(),
                            crNum.toInt()
                        ))

                        val date = MyDate()
                        myViewModel.insertInHistory(EntryHistory(
                            null,
                            date.getDate(),
                            date.getTime(),
                            cat,
                            name,
                            "+$num",
                            num.toInt()
                        ))

                    } else {
                        Toast.makeText(this, R.string.toast_cat_message, Toast.LENGTH_LONG).show()
                    }
                }
                .setNegativeButton(android.R.string.cancel, null)
                .show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return true
    }
}