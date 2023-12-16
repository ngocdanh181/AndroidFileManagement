package com.example.filemanager

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val storageBtn = findViewById<MaterialButton>(R.id.storage_btn)
        storageBtn.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                handleStoragePermissionAndroid11AndAbove()
                val intent = Intent(this@MainActivity, FileListActivity::class.java)
                val path = Environment.getExternalStorageDirectory().path
                intent.putExtra("path", path)
                startActivity(intent)
            } else {
                if (checkPermission()) {

                    val intent = Intent(this@MainActivity, FileListActivity::class.java)
                    val path = Environment.getExternalStorageDirectory().path
                    intent.putExtra("path", path)
                    startActivity(intent)
                } else {

                    requestPermission()
                }
            }
        }
    }

    private fun handleStoragePermissionAndroid11AndAbove() {
        val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
        startActivity(intent)
    }

    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            this@MainActivity,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this@MainActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            Toast.makeText(
                this@MainActivity,
                "Storage permission is required, please allow from settings",
                Toast.LENGTH_SHORT
            ).show()
        } else ActivityCompat.requestPermissions(
            this@MainActivity,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            111
        )
    }
}
