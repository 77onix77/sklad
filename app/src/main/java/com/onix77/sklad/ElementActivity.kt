package com.onix77.sklad

import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.onix77.sklad.databinding.ActivityElementBinding
import java.io.File
import java.io.FileOutputStream
import java.io.Serializable
import java.util.*


//@Suppress("SpellCheckingInspection")
@Suppress("SpellCheckingInspection")
class ElementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityElementBinding
    private lateinit var imView: ImageView
    private val  myViewModel: MyViewModel by viewModels {
        MyViewModelFactory((application as MyApplication).repository)
    }

    // Разрешение на использование камеры
    private val permissionCam = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) cameraOn.launch()
        else if (!ActivityCompat.shouldShowRequestPermissionRationale(this, CAMERA)) {
            Toast.makeText(this, R.string.toast_El_Act_permission, Toast.LENGTH_LONG).show()
        } else Toast.makeText(this, R.string.toast_El_Act_no_permission, Toast.LENGTH_LONG).show()
    }

    // пллучение фото с камеры для элемента
    private val cameraOn = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            imView.setImageBitmap(bitmap)
            saveImage()
        }

    }

    //разрешение на использование пользовательских файлов
    private val permissionGal = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) imageOn.launch(PickVisualMediaRequest())
        else if (!ActivityCompat.shouldShowRequestPermissionRationale(this, READ_EXTERNAL_STORAGE)) {
            Toast.makeText(this, R.string.toast_El_Act_permission, Toast.LENGTH_LONG).show()
        } else Toast.makeText(this, R.string.toast_El_Act_no_permission, Toast.LENGTH_LONG).show()
    }

    //получение картинки с устройства для элемента
    private val imageOn = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        imView.setImageURI(uri)
        saveImage()
    }

    val date = MyDate()

    private lateinit var el: ElementDB

    private var elChange = false // был ли элемент изменен

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityElementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        imView = binding.ElImageView

        el = (intent.serializable("el") as? ElementDB)!!

        binding.apply {
            ElNameElTV.text = el.nameEl
            ElNameCatTV.text = el.nameCat
            ElRestTV.text = el.number.toString()
            if (el.number <= el.criticalRest) ElRestTV.setBackgroundResource(R.color.pink)
            if (!el.path_image.isNullOrEmpty() && File(this@ElementActivity.filesDir, "IMAGES_ELEMENTS/${el.path_image!!}").exists()) {
                imView.setImageURI(Uri.parse("file:///${this@ElementActivity.filesDir}/IMAGES_ELEMENTS/${el.path_image}"))
            }
        }

        // выбор операции расход/приход
        ArrayAdapter.createFromResource(
            this,
            R.array.item_spinner,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.ElSpinner.adapter = adapter
        }
        binding.ElSpinner.setSelection(0)

        //кнопки быстрого выбора количества
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

        // установка изображения элемента(алертдиалог с выбором камеры или галереи)
        imView.setOnClickListener {
            val item = arrayOf(getString(R.string.from_gal), getString(R.string.from_cam))
            val alBil = AlertDialog.Builder(this)
            alBil.apply {
                setTitle(getString(R.string.edite_image))
                setItems(item) {_, i ->
                    if (i == 1) {
                        if (ContextCompat.checkSelfPermission(this@ElementActivity, CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            cameraOn.launch()
                        } else permissionCam.launch(CAMERA)
                    }
                    if (i == 0) {
                        if (ContextCompat.checkSelfPermission(this@ElementActivity, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            imageOn.launch(PickVisualMediaRequest())
                        } else permissionGal.launch(READ_EXTERNAL_STORAGE)
                    }
                }
                setNegativeButton(android.R.string.cancel, null)
                show()
            }
        }

        //сохранение изменения остатка и запись в историю
        binding.ElOkBt.setOnClickListener {
            if (!binding.ElNumberET.text.isNullOrEmpty()) {

                val parity: String
                if (binding.ElSpinner.selectedItemPosition == 0) {
                    el.number -= binding.ElNumberET.text.toString().toInt()
                    parity = "-"
                } else {
                    el.number += binding.ElNumberET.text.toString().toInt()
                    parity = "+"
                }


                myViewModel.apply {
                    updateEl(el)
                    insertInHistory(EntryHistory(
                        null,
                        date.getDate(),
                        date.getTime(),
                        el.nameCat,
                        el.nameEl,
                        "$parity${binding.ElNumberET.text}",
                        el.number
                    ))
                }
                finish()

            } else if (elChange) {
                myViewModel.updateEl(el)
                finish()

            } else {
                Toast.makeText(this, R.string.toast_El_Act_message, Toast.LENGTH_LONG).show()
                finish()
            }
        }



        binding.ElCancelBt.setOnClickListener {
            finish()
        }
    }



    // сохранение изображения элемента
    private fun saveImage() {
        val bitmap = imView.drawable.toBitmap()
        val name = (date.getDateTimeForName() + ".jpg")
        val dir = File(this.filesDir, "IMAGES_ELEMENTS")
        if (!dir.exists()) dir.mkdir()

        try {
            val file = File(dir, name)
            val fileStr = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG,75, fileStr)
            fileStr.close()
            if (!el.path_image.isNullOrEmpty() && File(dir, el.path_image!!).exists()) File(dir, el.path_image!!).delete()
            el.path_image = name
            elChange = true
        } catch (e: Exception) {
            Toast.makeText(this, R.string.toast_El_Act_save_exception, Toast.LENGTH_LONG).show()
        }
    }

    private inline fun <reified T : Serializable> Intent.serializable(key: String): T? = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializableExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getSerializableExtra(key) as? T
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.element_menu_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.menuDel -> delEl()
            R.id.menuEdite -> editeEl()
        }
        return true
    }

    //удаление элемента
    private fun delEl() {
        AlertDialog.Builder(this)
            .setTitle(R.string.el_aldig_del_title)
            .setMessage(R.string.el_aldig_del_text)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                val dir = File(this.filesDir, "IMAGES_ELEMENTS")
                if (!el.path_image.isNullOrEmpty() && File(dir, el.path_image!!).exists()) File(dir, el.path_image!!).delete()
                myViewModel.insertInHistory(EntryHistory(
                    null,
                    date.getDate(),
                    date.getTime(),
                    el.nameCat,
                    el.nameEl,
                    getString(R.string.del),
                    el.number
                ))
                myViewModel.delete(el)
                finish()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    //Редактирование элемента
    private fun editeEl() {
        val alertText = LayoutInflater.from(this).inflate(R.layout.alert_dialog_cat, null, false)
        val name = alertText.findViewById<EditText>(R.id.edTNameEl)
        val num = alertText.findViewById<EditText>(R.id.edTNumEl)
        val critNum = alertText.findViewById<EditText>(R.id.edTCritNumEl)
        name.setText(el.nameEl)
        num.setText(el.number.toString())
        critNum.setText(el.criticalRest.toString())
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.edite_el))
            .setView(alertText)
            .setPositiveButton(android.R.string.ok) { _, _ ->

                if (name.text.isNotEmpty() && num.text.isNotEmpty() && critNum.text.isNotEmpty() &&
                    (name.text.toString() != el.nameEl || num.text.toString() != el.number.toString() ||
                            critNum.text.toString() != el.criticalRest.toString())
                ) {

                    myViewModel.updateEl(ElementDB(
                        el.id,
                        el.nameCat,
                        name.text.toString(),
                        num.text.toString().toInt(),
                        critNum.text.toString().toInt(),
                        el.path_image
                    ))

                    binding.apply {
                        ElNameElTV.text = name.text.toString()
                        ElRestTV.text = num.text.toString()
                    }

                    val date = MyDate()

                    myViewModel.insertInHistory(EntryHistory(
                        null,
                        date.getDate(),
                        date.getTime(),
                        el.nameCat,
                        el.nameEl,
                        getString(R.string.before_edite),
                        el.number
                    ))

                    myViewModel.insertInHistory(EntryHistory(
                        null,
                        date.getDate(),
                        date.getTime(),
                        el.nameCat,
                        name.text.toString(),
                        getString(R.string.after_edite),
                        num.text.toString().toInt()
                    ))
                    finish()

                } else {
                    Toast.makeText(this, R.string.toast_edit_element_message, Toast.LENGTH_LONG).show()
                }
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

}