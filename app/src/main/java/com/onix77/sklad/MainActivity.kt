package com.onix77.sklad


import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.FileProvider
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.onix77.sklad.databinding.ActivityMainBinding
import com.yandex.mobile.ads.banner.AdSize
import com.yandex.mobile.ads.common.AdRequest
import kotlinx.coroutines.launch


@Suppress("SpellCheckingInspection")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val  myViewModel: MyViewModel by viewModels {
        MyViewModelFactory((application as MyApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bannerYandex.setAdUnitId("demo-banner-yandex")
        binding.bannerYandex.setAdSize(AdSize.stickySize(300))
        val adRequest = AdRequest.Builder().build()
        binding.bannerYandex.loadAd(adRequest)

        val listCat = mutableListOf<String>()


        binding.recV.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = RecyclerAdapter(listCat, this@MainActivity)
        }

        lifecycle.coroutineScope.launch {
            myViewModel.getCat().collect {
                val difUtil = MainDiffUtils(listCat, it)
                val difResult = DiffUtil.calculateDiff(difUtil)
                listCat.clear()
                listCat += it
                binding.recV.adapter?.let { it1 -> difResult.dispatchUpdatesTo(it1) }
            }
        }

        binding.historyBt.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        binding.statBut.setOnClickListener {
            val intent = Intent(this, StatisticActivity::class.java)
            startActivity(intent)
        }

        binding.addBut.setOnClickListener {
            val alertText = LayoutInflater.from(this).inflate(R.layout.alert_dialog_main, null, false)
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.input_title_cat))
                .setView(alertText)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    val text = alertText.findViewById<EditText>(R.id.edTNameCat).text.toString()
                    if (text.isNotEmpty()) {
                        listCat += text
                        binding.recV.adapter!!.notifyItemInserted(listCat.lastIndex)
                        Toast.makeText(this, R.string.toast_main_message, Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, R.string.no_tile, Toast.LENGTH_LONG).show()
                    }
                }
                .setNegativeButton(android.R.string.cancel, null)
                .show()
        }

        binding.sharBtMain.setOnClickListener {
            lifecycle.coroutineScope.launch {
                val listAllEl = MainDB.getDB(this@MainActivity).getDao().getAll()
                val fileShare = FileShare(listAllEl, this@MainActivity.filesDir)
                val file = fileShare.createFileRest()
                val uri = FileProvider.getUriForFile(this@MainActivity, "com.onix77.sklad.fileprovider", file)
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                intent.putExtra(Intent.EXTRA_STREAM, uri)
                startActivity(intent)
            }
        }
    }
}