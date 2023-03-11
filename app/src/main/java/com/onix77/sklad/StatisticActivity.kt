package com.onix77.sklad

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.net.toUri
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.onix77.sklad.databinding.ActivityHistoryBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.File


class StatisticActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private val  myViewModel: MyViewModel by viewModels {
        MyViewModelFactory((application as MyApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val listCat = mutableListOf("ВСЕ")
        val listEl = mutableListOf("ВСЕ")

        binding.titleTVStat.text = "Статистика за период"


        lifecycle.coroutineScope.launch {
            myViewModel.getCat().collect() {
                listCat += it
            }
        }


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
                    lifecycle.coroutineScope.launch {
                        myViewModel.getNameEl(listCat[p2]).collect() {
                            listEl += it
                        }
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        val listStat = mutableListOf<ItemStatistic>()


        binding.recVHis.apply {
            layoutManager = LinearLayoutManager(this@StatisticActivity)
            adapter = RecAdStat(listStat)
            addItemDecoration(DividerItemDecoration(
                this@StatisticActivity, DividerItemDecoration.VERTICAL).apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    setDrawable(getDrawable(R.drawable.divider)!!)
                }
            }
            )
        }

        val date = MyDate()

        binding.dateFromETHis.setText(date.getBeginMounth())
        binding.dateToETHis.setText(date.getDate())

        binding.okBtHis.setOnClickListener {
            val listHis = mutableListOf<EntryHistory>()
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
                    lifecycle.coroutineScope.launch {
                        myViewModel.getAllDateHis(
                            binding.dateFromETHis.text.toString(),
                            binding.dateToETHis.text.toString()
                        ).collect() {
                            listHis += it
                            listStat.clear()
                            listStat += statCalcAll(listHis)
                            if (listStat.isNotEmpty()) binding.sharBtHis.visibility = View.VISIBLE
                            else binding.sharBtHis.visibility = View.GONE
                            binding.recVHis.adapter!!.notifyDataSetChanged()
                        }
                    }

                } else if (binding.ElSpHis.selectedItemPosition == 0) {
                    listHis.clear()
                    lifecycle.coroutineScope.launch {
                        myViewModel.getCatDateHis(
                            binding.dateFromETHis.text.toString(),
                            binding.dateToETHis.text.toString(),
                            binding.catSpHis.selectedItem.toString()
                        ).collect() {
                            listHis += it
                            listStat.clear()
                            listStat += statCalcCat(listHis, binding.catSpHis.selectedItem.toString())
                            if (listStat.isNotEmpty()) binding.sharBtHis.visibility = View.VISIBLE
                            else binding.sharBtHis.visibility = View.GONE
                            binding.recVHis.adapter!!.notifyDataSetChanged()
                        }
                    }

                } else {
                    listHis.clear()
                    lifecycle.coroutineScope.launch {
                        myViewModel.getElDateHis(
                            binding.dateFromETHis.text.toString(),
                            binding.dateToETHis.text.toString(),
                            binding.catSpHis.selectedItem.toString(),
                            binding.ElSpHis.selectedItem.toString()
                        ).collect() {
                            listHis += it
                            listStat.clear()
                            listStat += statCalcEl(listHis,
                                binding.catSpHis.selectedItem.toString(),
                                binding.ElSpHis.selectedItem.toString()
                            )
                            if (listStat.isNotEmpty()) binding.sharBtHis.visibility = View.VISIBLE
                            else binding.sharBtHis.visibility = View.GONE
                            binding.recVHis.adapter!!.notifyDataSetChanged()
                        }
                    }
                }
            }
        }

        binding.sharBtHis.setOnClickListener {
            lifecycle.coroutineScope.launch {
                val file = createFileShare(listStat, binding.dateFromETHis.text.toString(), binding.dateToETHis.text.toString())
                val uri = file.toUri()
                val intent = Intent(Intent.ACTION_SEND, uri)
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                startActivity(intent)
            }

        }

    }

    private suspend fun statCalcAll(hisList: MutableList<EntryHistory>): List<ItemStatistic> {
        val listStat = mutableListOf<ItemStatistic>()
        val db = MainDB.getDB(this)

        val listCat = db.getDao().getCatL()
        for (cat in listCat) listStat += statCalcCat(hisList, cat)

        return listStat
    }

    private suspend fun statCalcCat(hisList: MutableList<EntryHistory>, cat: String): List<ItemStatistic> {
        val listStat = mutableListOf<ItemStatistic>()
        val db = MainDB.getDB(this)

        val listEl = db.getDao().getNameElL(cat)
        for (el in listEl) listStat += statCalcEl(hisList, cat, el)

        return listStat
    }

    private fun statCalcEl(hisList: MutableList<EntryHistory>, cat: String, el: String): ItemStatistic {
        var plus = 0
        var minus = 0
        for (elHis in hisList) {
            if (elHis.nameCat == cat && elHis.nameEl == el) {
                if (elHis.changeRest[0] == '+') plus += elHis.changeRest.toInt()
                if (elHis.changeRest[0] == '-') minus += elHis.changeRest.toInt()
                //hisList.remove(elHis)
            }
        }
        return ItemStatistic(cat, el, plus, minus)
    }

    private fun createFileShare(list: List<ItemStatistic>, dateFrom: String, dateTo: String): File {
        val dir = File(this.filesDir, "SHARE_FILE")
        if (!dir.exists()) dir.mkdir()
        val file = File(dir, "statistic.txt")
        file.writeText("\t\t**************Статистика с $dateFrom по $dateTo**************\n")
        file.appendText("""
            --------------------------------------------------------------------------------
            | Название                                     | Приход (+)    | Расход (-)    |
            ================================================================================
        """.trimIndent())
        val groupMap = list.groupBy { it.cat }
        for ((k, v) in groupMap) {
            var str = "|     $k"
            str += " ".repeat(73 - k.length)
            str += "|\n"
            file.appendText(str)
            file.appendText("--------------------------------------------------------------------------------\n")
            for (el in v) {
                str = "| ${el.el}"
                str += " ".repeat(46 - el.el.length)
                str += "|"
                str += " ".repeat(13 - el.sumPlus.toString().length)
                str += "+${el.sumPlus.toString().length}"
                str += " |"
                str += " ".repeat(14 - el.sumMinus.toString().length)
                str += "+${el.sumMinus.toString().length}"
                str += " |\n"
                file.appendText(str)
                file.appendText("--------------------------------------------------------------------------------\n")
            }
        }
        return file
    }


}