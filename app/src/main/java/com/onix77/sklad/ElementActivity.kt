package com.onix77.sklad

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.onix77.sklad.databinding.ActivityElementBinding
import java.text.SimpleDateFormat

import java.util.*
import kotlinx.datetime.Clock

class ElementActivity : AppCompatActivity() {
    private lateinit var binding: ActivityElementBinding
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityElementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val el = intent.getSerializableExtra("el") as ElementDB

        binding.apply {
            ElNameElTV.text = el.nameEl
            ElNameCatTV.text = el.nameCat
            ElRestTV.text = el.number.toString()
            if (el.number <= el.criticalRest) ElRestTV.setBackgroundResource(R.color.pink)
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.item_spinner,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.ElSpinner.adapter = adapter
        }
        binding.ElSpinner.setSelection(0)

        binding.apply {
            El1BT.setOnClickListener { ElNumberET.setText("1")  }
            El2BT.setOnClickListener { ElNumberET.setText("2")  }
            El3BT.setOnClickListener { ElNumberET.setText("3")  }
            El5BT.setOnClickListener { ElNumberET.setText("5")  }
            El10BT.setOnClickListener { ElNumberET.setText("10")  }
            El15BT.setOnClickListener { ElNumberET.setText("15")  }
            El20BT.setOnClickListener { ElNumberET.setText("20")  }
            El30BT.setOnClickListener { ElNumberET.setText("30")  }
            El50BT.setOnClickListener { ElNumberET.setText("50")  }
            El100BT.setOnClickListener { ElNumberET.setText("100")  }
            El200BT.setOnClickListener { ElNumberET.setText("200")  }
            El500BT.setOnClickListener { ElNumberET.setText("500")  }
        }

        binding.ElOkBt.setOnClickListener {
            if (binding.ElNumberET.text.isNullOrEmpty()) {
                Toast.makeText(this, R.string.toast_El_Act_message, Toast.LENGTH_LONG).show()
                finish()
            } else {
                val parity: String
                if (binding.ElSpinner.selectedItemPosition == 0) {
                    el.number -= binding.ElNumberET.text.toString().toInt()
                    parity = "-"
                } else {
                    el.number += binding.ElNumberET.text.toString().toInt()
                    parity = "+"
                }
                val db = MainDB.getDB(this)
                val th = Thread {
                    val date = MyDate()
                    db.getDao().updateEl(el)
                    db.getDao().insertInHistory(EntryHistory(
                        null,
                        date.getDate(),
                        date.getTime(),
                        el.nameCat,
                        el.nameEl,
                        "$parity${binding.ElNumberET.text}",
                        el.number
                    ))
                }
                th.start()
                th.join()
                finish()
            }
        }

        binding.ElCancelBt.setOnClickListener {
            finish()
        }





    }
}