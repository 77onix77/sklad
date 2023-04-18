package com.onix77.sklad


import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.FileProvider
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.onix77.sklad.databinding.ActivityHistoryBinding
import kotlinx.coroutines.launch



class StatisticActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private val  myViewModel: MyViewModel by viewModels {
        MyViewModelFactory((application as MyApplication).repository)
    }
    private var menuItemShare: MenuItem? = null
    private val listStat = mutableListOf<ItemStatistic>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val listCat = mutableListOf("ВСЕ")
        val listEl = mutableListOf("ВСЕ")

        binding.titleTVStat.text = "Статистика за период"


        lifecycle.coroutineScope.launch {
            myViewModel.getCat().collect {
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
                        myViewModel.getNameEl(listCat[p2]).collect {
                            listEl += it
                        }
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }




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
                        ).collect {
                            listHis += it
                            val list = statCalcAll(listHis)
                            val difUtil = StatDiffUtils(listStat, list)
                            val difResult = DiffUtil.calculateDiff(difUtil)
                            listStat.clear()
                            listStat += list
                            menuItemShare?.isVisible = listStat.isNotEmpty()
                            binding.recVHis.adapter?.let { it2 -> difResult.dispatchUpdatesTo(it2) }
                        }
                    }

                } else if (binding.ElSpHis.selectedItemPosition == 0) {
                    listHis.clear()
                    lifecycle.coroutineScope.launch {
                        myViewModel.getCatDateHis(
                            binding.dateFromETHis.text.toString(),
                            binding.dateToETHis.text.toString(),
                            binding.catSpHis.selectedItem.toString()
                        ).collect {
                            listHis += it
                            val list = statCalcCat(listHis, binding.catSpHis.selectedItem.toString())
                            val difUtil = StatDiffUtils(listStat, list)
                            val difResult = DiffUtil.calculateDiff(difUtil)
                            listStat.clear()
                            listStat += list
                            menuItemShare?.isVisible = listStat.isNotEmpty()
                            binding.recVHis.adapter?.let { it2 -> difResult.dispatchUpdatesTo(it2) }
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
                        ).collect {
                            listHis += it
                            val list = mutableListOf(statCalcEl(listHis,
                                binding.catSpHis.selectedItem.toString(),
                                binding.ElSpHis.selectedItem.toString()
                            ))
                            val difUtil = StatDiffUtils(listStat, list)
                            val difResult = DiffUtil.calculateDiff(difUtil)
                            listStat.clear()
                            listStat += list
                            menuItemShare?.isVisible = listStat.isNotEmpty()
                            binding.recVHis.adapter?.let { it2 -> difResult.dispatchUpdatesTo(it2) }
                        }
                    }
                }
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
            }
        }
        return ItemStatistic(cat, el, plus, minus)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.stat_menu_toolbar, menu)
        menuItemShare = menu?.findItem(R.id.menuShare)
        menuItemShare?.isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        if (item.itemId == menuItemShare?.itemId) {
            lifecycle.coroutineScope.launch {
                val fileShare = FileShare(listStat, this@StatisticActivity.filesDir)
                val file = fileShare.createFileStat(binding.dateFromETHis.text.toString(), binding.dateToETHis.text.toString())
                val uri = FileProvider.getUriForFile(this@StatisticActivity, "com.onix77.sklad.fileprovider", file)
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                intent.putExtra(Intent.EXTRA_STREAM, uri)
                startActivity(intent)
            }
        }
        return true
    }




}