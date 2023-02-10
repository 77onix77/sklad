package com.onix77.sklad

import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.onix77.sklad.databinding.ActivityElementBinding
import java.util.*

class ElementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityElementBinding
    private lateinit var imView: ImageView
    private val  myViewModel: MyViewModel by viewModels {
        MyViewModelFactory((application as MyApplication).repository)
    }

    private val permissionCam = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) cameraOn.launch()
        else if (!ActivityCompat.shouldShowRequestPermissionRationale(this, CAMERA)) {
            Toast.makeText(this, R.string.toast_El_Act_permission, Toast.LENGTH_LONG).show()
        } else Toast.makeText(this, R.string.toast_El_Act_no_permission, Toast.LENGTH_LONG).show()
    }

    private val cameraOn = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        imView.setImageBitmap(bitmap)
    }

    private val permissionGal = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) imageOn.launch(PickVisualMediaRequest())
        else if (!ActivityCompat.shouldShowRequestPermissionRationale(this, READ_EXTERNAL_STORAGE)) {
            Toast.makeText(this, R.string.toast_El_Act_permission, Toast.LENGTH_LONG).show()
        } else Toast.makeText(this, R.string.toast_El_Act_no_permission, Toast.LENGTH_LONG).show()
    }

    private val imageOn = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        imView.setImageURI(uri)
    }

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

        imView = binding.ElImageView

        binding.ImButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                imageOn.launch(PickVisualMediaRequest())
            } else permissionGal.launch(READ_EXTERNAL_STORAGE)
        }

        binding.camButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, CAMERA) == PackageManager.PERMISSION_GRANTED) {
                cameraOn.launch()
            } else permissionCam.launch(CAMERA)
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

                val date = MyDate()
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
            }
        }
        binding.ElCancelBt.setOnClickListener {
            finish()
        }
    }
}