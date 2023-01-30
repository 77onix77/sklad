package com.onix77.sklad

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.onix77.sklad.databinding.ActivityCatBinding

class CatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCatBinding
    private var list = mutableListOf<ElementDB>()
    private lateinit var cat: String
    private lateinit var myAdapter: RecAdCat


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cat = intent.getStringExtra("cat")!!

        binding.nameCategory.text = cat
        binding.recVCat.apply {
            layoutManager = LinearLayoutManager(this@CatActivity)
            adapter = RecAdCat(list, this@CatActivity)
        }

        val db = MainDB.getDB(this)
        db.getDao().getEl(cat).asLiveData().observe(this) {
            list.clear()
            list += it
            binding.recVCat.adapter!!.notifyDataSetChanged()
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

                        val th = Thread{
                            val date = MyDate()

                            db.getDao().insertEl(ElementDB(
                                null,
                                cat, name,
                                num.toInt(),
                                critNum.toInt()
                            ))
                            db.getDao().insertInHistory(EntryHistory(
                                null,
                                date.getDate(),
                                date.getTime(),
                                cat,
                                name,
                                "+$num",
                                num.toInt()
                            ))
                        }
                        th.start()
                        list += ElementDB(null, cat, name, num.toInt(), critNum.toInt())
                        binding.recVCat.adapter!!.notifyItemInserted(list.lastIndex)

                    } else {
                        Toast.makeText(this, R.string.toast_cat_message, Toast.LENGTH_LONG).show()
                    }
                }
                .setNegativeButton(android.R.string.cancel, null)
                .show()
        }
    }

}