package com.onix77.sklad


import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.onix77.sklad.databinding.ActivityHistoryBinding

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val listCat = mutableListOf("ВСЕ")
        val listEl = mutableListOf("ВСЕ")
        val db = MainDB.getDB(this)
        var getDB = Thread { listCat += db.getDao().getCat().toMutableList() }
        getDB.start()
        getDB.join()

        ArrayAdapter(this, android.R.layout.simple_spinner_item, listCat).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.catSpHis.adapter = adapter
        }
        binding.catSpHis.setSelection(0)

        ArrayAdapter(this, android.R.layout.simple_spinner_item, listEl).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.ElSpHis.adapter = adapter
        }
        binding.ElSpHis.setSelection(0)

        binding.catSpHis.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                listEl.apply {
                    clear()
                    add("ВСЕ")
                }

                binding.ElSpHis.setSelection(0)

                if (p2 != 0) {
                    getDB = Thread { listEl += db.getDao().getNameEl(listCat[p2]) }
                    getDB.start()
                    getDB.join()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        val listHis = mutableListOf<EntryHistory>()

        binding.recVHis.apply {
            layoutManager = LinearLayoutManager(this@HistoryActivity)
            adapter = RecAdHis(listHis)
            addItemDecoration(DividerItemDecoration(
                this@HistoryActivity, DividerItemDecoration.VERTICAL).apply {
                    setDrawable(getDrawable(R.drawable.divider)!!)
                }
            )
        }

        val date = MyDate()

        binding.dateFromETHis.setText(date.getBeginMounth())
        binding.dateToETHis.setText(date.getDate())

        binding.okBtHis.setOnClickListener {
            var dateOk = true
            if (!Regex("20\\d\\d-[0,1]\\d-[0-3]\\d").matches(binding.dateFromETHis.text)) {
                dateOk = false
                binding.dateFromETHis.error = R.string.error_input.toString()
                Toast.makeText(this, R.string.toast_His_Act, Toast.LENGTH_LONG).show()
            }
            if (!Regex("20\\d\\d-[0,1]\\d-[0-3]\\d").matches(binding.dateToETHis.text)) {
                dateOk = false
                binding.dateToETHis.error = R.string.error_input.toString()
                Toast.makeText(this, R.string.toast_His_Act, Toast.LENGTH_LONG).show()
            }
            if (dateOk) {
                if (binding.catSpHis.selectedItemPosition == 0) {
                    listHis.clear()
                    getDB = Thread {
                        listHis += db.getDao().getAllDateHis(
                            binding.dateFromETHis.text.toString(),
                            binding.dateToETHis.text.toString()
                        )
                    }
                    getDB.start()
                    getDB.join()
                    binding.recVHis.adapter!!.notifyDataSetChanged()
                } else if (binding.ElSpHis.selectedItemPosition == 0) {
                    listHis.clear()
                    getDB = Thread {
                        listHis += db.getDao().getCatDateHis(
                            binding.dateFromETHis.text.toString(),
                            binding.dateToETHis.text.toString(),
                            binding.catSpHis.selectedItem.toString()
                        )
                    }
                    getDB.start()
                    getDB.join()
                    binding.recVHis.adapter!!.notifyDataSetChanged()
                } else {
                    listHis.clear()
                    getDB = Thread {
                        listHis += db.getDao().getElDateHis(
                            binding.dateFromETHis.text.toString(),
                            binding.dateToETHis.text.toString(),
                            binding.catSpHis.selectedItem.toString(),
                            binding.ElSpHis.selectedItem.toString()
                        )
                    }
                    getDB.start()
                    getDB.join()
                    binding.recVHis.adapter!!.notifyDataSetChanged()
                }
            }
        }
    }
}