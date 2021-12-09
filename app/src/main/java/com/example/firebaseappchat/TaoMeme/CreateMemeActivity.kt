package com.example.firebaseappchat.TaoMeme

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.R.attr
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.firebaseappchat.R
import yuku.ambilwarna.AmbilWarnaDialog
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import android.view.MotionEvent

import android.graphics.PointF

import android.R.attr.mode
import android.annotation.SuppressLint
import android.view.View.OnTouchListener
import android.widget.TextView







class CreateMemeActivity : AppCompatActivity() {
    var lastEvent: FloatArray? = null
    var d = 0f
    var newRot = 0f
    private var isZoomAndRotate = false
    private var isOutSide = false
    private val NONE = 0
    private val DRAG = 1
    private val ZOOM = 2
    private var mode = NONE
    private val start = PointF()
    private val mid = PointF()
    var oldDist = 1f
    private var xCoOrdinate = 0f
    private  var yCoOrdinate:Float = 0f


    private val MY_PERMISSION_REQUEST = 1
    private lateinit var btnLoad: Button
    private lateinit var btnSave: Button
    private lateinit var btnShare: Button
    private lateinit var btnColorPicker: Button

    private lateinit var ImgMeme: ImageView
    private lateinit var seekBar: SeekBar

    private lateinit var TxtImgTop: TextView
    private lateinit var TxtImgBottom: TextView

    private var currentImage = ""

    private lateinit var txtTop_Text: EditText
    private lateinit var txtBottom_Text: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_meme)
        Load()
        IF_ElSE()

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun Load() {
        seekBar = findViewById(R.id.seekBar)
        btnLoad = findViewById(R.id.btnLoadMeme)
        btnSave = findViewById(R.id.btnSaveMeme)
        btnShare = findViewById(R.id.btnShareMeme)
        btnColorPicker = findViewById(R.id.btnColorPicker)
        //
        ImgMeme = findViewById(R.id.ImgMeme)
        //
        TxtImgTop = findViewById(R.id.TxtImgTop)
        TxtImgBottom = findViewById(R.id.TxtImgBottom)
        //
        txtTop_Text = findViewById(R.id.txtTop_Text)
        txtBottom_Text = findViewById(R.id.txtBottom_Text)
        //
        btnSave.isEnabled = false
        btnShare.isEnabled = false
        //
        btnLoad.setOnClickListener() {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
        btnSave.setOnClickListener {
            val context = findViewById<View>(R.id.ContextIMG)
            val bitmap = getScreenShot(context)
            currentImage = "meme" + System.currentTimeMillis() + ".png"
            store(bitmap, currentImage)
        }

        btnShare.setOnClickListener {

        }

        txtTop_Text.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                return
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                return
            }

            override fun afterTextChanged(s: Editable?) {
                TxtImgTop.text = txtTop_Text.text.toString()
            }
        })

        txtBottom_Text.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                return
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                return
            }

            override fun afterTextChanged(s: Editable?) {
                TxtImgBottom.text = txtBottom_Text.text.toString()
            }
        })

//        TxtImgTop.setOnClickListener {
//
//        }

        TxtImgTop.setOnTouchListener { v, event ->

            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    TxtImgTop.textSize = progress.toFloat()
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    return
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    return
                }
            })
            btnColorPicker.setOnClickListener {
                val defaultColor = ContextCompat.getColor(this,R.color.Primary)
                val dialog =AmbilWarnaDialog(this, defaultColor, object :
                    AmbilWarnaDialog.OnAmbilWarnaListener {
                    override fun onCancel(dialog: AmbilWarnaDialog?) {
                        return
                    }

                    override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                        TxtImgTop.setTextColor(color)
                    }
                })
                dialog.show();
            }

            val view = v as TextView
            view.bringToFront()
            viewTransformation(view, event)
            true
        }

//        TxtImgBottom.setOnClickListener {
//
//        }

        TxtImgBottom.setOnTouchListener { v, event ->

            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    TxtImgBottom.textSize = progress.toFloat()
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    return
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    return
                }
            })
            btnColorPicker.setOnClickListener {
                val defaultColor = ContextCompat.getColor(this,R.color.Primary)
                val dialog =AmbilWarnaDialog(this, defaultColor, object :
                    AmbilWarnaDialog.OnAmbilWarnaListener {
                    override fun onCancel(dialog: AmbilWarnaDialog?) {
                        return
                    }

                    override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                        TxtImgBottom.setTextColor(color)
                    }
                })
                dialog.show();
            }

            val view = v as TextView
            view.bringToFront()
            viewTransformation(view, event)
            true
        }
    }

    private fun getScreenShot(view: View): Bitmap {
        view.isDrawingCacheEnabled = true
        val bitmap = Bitmap.createBitmap(view.getDrawingCache())
        view.isDrawingCacheEnabled = false
        return bitmap
    }

    private fun store(bitmap: Bitmap, filename: String) {
        val dirPath =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath
        val dir = File(dirPath, "MeMe")
        if (!dir.exists()) {
            dir.mkdir()
        }
        val file = File(dir, filename)
        try {
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.flush()
            fos.close()
            Toast.makeText(this, "Lưu Thành Công", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    var selectPhotoUrl: Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectPhotoUrl = data.data
            //select images
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectPhotoUrl)
            ImgMeme.setImageBitmap(bitmap)
            btnShare.isEnabled = true
            btnSave.isEnabled = true
        }
    }

    private fun IF_ElSE() {
        if (ContextCompat.checkSelfPermission(
                this,
                WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(WRITE_EXTERNAL_STORAGE),
                    MY_PERMISSION_REQUEST
                )
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(WRITE_EXTERNAL_STORAGE),
                    MY_PERMISSION_REQUEST
                )
            }
        } else {
            //
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSION_REQUEST -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    //
                } else {
                    Toast.makeText(this, "No Permission granted", Toast.LENGTH_SHORT).show()
                    finish()
                }
                return super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    private fun viewTransformation(view: View, event: MotionEvent) {
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                xCoOrdinate = view.x - event.rawX
                yCoOrdinate = view.y - event.rawY
                start.set(event.x, event.y)
                isOutSide = false
                mode = DRAG
                lastEvent = null
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                oldDist = spacing(event)
                if (oldDist > 10f) {
                    midPoint(mid, event)
                    mode = ZOOM
                }
                lastEvent = FloatArray(4)
                lastEvent!![0] = event.getX(0)
                lastEvent!![1] = event.getX(1)
                lastEvent!![2] = event.getY(0)
                lastEvent!![3] = event.getY(1)
                d = rotation(event)
            }
            MotionEvent.ACTION_UP -> {
                isZoomAndRotate = false
                if (mode == DRAG) {
                    val x = event.x
                    val y = event.y
                }
                isOutSide = true
                mode = NONE
                lastEvent = null
                mode = NONE
                lastEvent = null
            }
            MotionEvent.ACTION_OUTSIDE -> {
                isOutSide = true
                mode = NONE
                lastEvent = null
                mode = NONE
                lastEvent = null
            }
            MotionEvent.ACTION_POINTER_UP -> {
                mode = NONE
                lastEvent = null
            }
            MotionEvent.ACTION_MOVE -> if (!isOutSide) {
                if (mode == DRAG) {
                    isZoomAndRotate = false
                    view.animate().x(event.rawX + xCoOrdinate).y(event.rawY + yCoOrdinate)
                        .setDuration(0).start()
                }
                if (mode == ZOOM && event.pointerCount == 2) {
                    val newDist1 = spacing(event)
                    if (newDist1 > 10f) {
                        val scale: Float = newDist1 / oldDist * view.scaleX
                        view.scaleX = scale
                        view.scaleY = scale
                    }
                    if (lastEvent != null) {
                        newRot = rotation(event)
                        view.rotation = (view.rotation + (newRot - d)) as Float
                    }
                }
            }
        }
    }

    private fun rotation(event: MotionEvent): Float {
        val delta_x = (event.getX(0) - event.getX(1)).toDouble()
        val delta_y = (event.getY(0) - event.getY(1)).toDouble()
        val radians = Math.atan2(delta_y, delta_x)
        return Math.toDegrees(radians).toFloat()
    }

    private fun spacing(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return Math.sqrt((x * x + y * y).toDouble()).toFloat()
    }

    private fun midPoint(point: PointF, event: MotionEvent) {
        val x = event.getX(0) + event.getX(1)
        val y = event.getY(0) + event.getY(1)
        point[x / 2] = y / 2
    }
}