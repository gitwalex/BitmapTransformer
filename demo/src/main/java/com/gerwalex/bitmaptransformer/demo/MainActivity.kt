package com.gerwalex.bitmaptransformer.demo

import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.MenuProvider
import com.gerwalex.bitmaptransformer.BitmapTransformer
import com.gerwalex.bitmaptransformer.demo.databinding.ActivityMainBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val prefs: SharedPreferences by lazy {
        getSharedPreferences(BuildConfig.APPLICATION_ID, MODE_PRIVATE)
    }
    private lateinit var adapter: PicassoAdapter
    private lateinit var binding: ActivityMainBinding
    private val dataSet = ArrayList<PicassoAdapter.Type>()
    private val menuProvider = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.imagelist_bottom_navigation, menu)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            when (menuItem.itemId) {
                R.id.gallery -> {
                    activityGalleryLauncher.launch("image/*")
                }
                R.id.camera -> {
                    getImageFileUri().let {
                        activityCameraLauncher.launch(it)
                    }
                }
            }
            return true
        }
    }
    private val activityGalleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val file = saveBitmap(it)
                val oldfile = adapter.setFile(file)
                MainScope().launch {
                    prefs
                        .edit()
                        .putString("LASTFILE", file.absolutePath)
                        .apply()
                    oldfile.delete()
                }
            }
        }
    private val activityCameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { _: Boolean? ->
            getImageFileUri().let {
                val file = saveBitmap(it)
                val oldfile = adapter.setFile(file)
                MainScope().launch {
                    prefs
                        .edit()
                        .putString("LASTFILE", file.absolutePath)
                        .apply()
                    oldfile.delete()
                }
            }
        }

    private fun getImageFileUri(): Uri {
        val cameraImageUri = FileProvider.getUriForFile(
            this, BuildConfig.APPLICATION_ID + ".fileprovider",
            File(filesDir, "cameraImageFile-tmp"))
        return cameraImageUri
    }

    private fun saveBitmap(uri: Uri): File {
        val resizedFile = BitmapTransformer(this@MainActivity, uri)
            .outDir(filesDir)
            .quality(100)
            .targetLength(512)
            .compressFormat(Bitmap.CompressFormat.PNG)
            .scaleBitmapToFile()
        Log.d("gerwalex", "BitmapFileSize: ${resizedFile.length()}")
        return resizedFile
    }

    @get:Throws(IOException::class)
    private val demoFile: File
        get() {
            val lastfile = prefs.getString("LASTFILE", null)
            if (lastfile != null) {
                return File(lastfile)
            }
            return BitmapTransformer(this, R.drawable.check)
                .outDir(filesDir)
                .outFilename(filename)
                .scaleBitmapToFile()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        addMenuProvider(menuProvider, this)
        PicassoAdapter.Type
            .values()
            .forEach {
                dataSet.add(it)
            }
        adapter = PicassoAdapter(this, dataSet)
        adapter.setFile(demoFile)
        binding.list.adapter = adapter
    }

    companion object {

        private const val filename = "demo_picture"
    }
}