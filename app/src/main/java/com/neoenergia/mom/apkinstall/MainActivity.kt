  package com.neoenergia.mom.apkinstall

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Environment.getExternalStoragePublicDirectory
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.datalogic.device.app.PackageInstaller
import com.datalogic.device.app.PackageInstallerListener
import com.datalogic.device.app.PackageInstallerResult
import java.io.File


  class MainActivity : AppCompatActivity() {
      override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(!packageManager.canRequestPackageInstalls()) {
            startActivity(
                Intent(
                    Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                    Uri.parse("package:com.neoenergia.mom.apkinstall")
                )
            )
        }

        if(ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this@MainActivity,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            } else {
                ActivityCompat.requestPermissions(this@MainActivity,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            }
        }
    }

    private fun getNeoOSBPackage(): File? {
        @Suppress("DEPRECATION")
        val files = getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).listFiles {
                _: File, name: String -> name.lowercase().startsWith("neo.osb.app") && name.lowercase().endsWith(".apk")
        }
        if(files!!.isNotEmpty()){
            return files[0]
        }
        return null
    }

    override fun onResume() {
        super.onResume()

        val apkFile = getNeoOSBPackage()
        if(apkFile != null)
        {
            val pm = applicationContext.packageManager
            val apkInfo = pm.getPackageArchiveInfo(apkFile.path, PackageManager.GET_META_DATA)

            findViewById<TextView>(R.id.fileNameTextView).text = apkFile.name
            findViewById<TextView>(R.id.packageNameTextView).text = apkInfo?.applicationInfo?.packageName

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                findViewById<TextView>(R.id.packageVersionTextView).text = getString(R.string.packageVersionFormat,apkInfo?.longVersionCode, apkInfo?.versionName)
            }else{
                @Suppress("DEPRECATION")
                findViewById<TextView>(R.id.packageVersionTextView).text = getString(R.string.packageVersionFormat, apkInfo?.versionCode,apkInfo?.versionName)
            }
            findViewById<Button>(R.id.installButton).isEnabled = true
        } else{
            findViewById<Button>(R.id.installButton).isEnabled = false
        }
    }

    fun installApk(@Suppress("UNUSED_PARAMETER")view: View) {
        val apkFile = getNeoOSBPackage()

        try {
            val packageInstaller = PackageInstaller(applicationContext)
            val listener = PackageInstallerListener {results: MutableList<PackageInstallerResult>? ->
                if (results != null) {
                    for (result in results){
                        Log.d("Listener", result.result.toString())
                    }
                }
            }
            packageInstaller.install(apkFile?.path!!, false, listener)
        }catch (e1: Exception) {
            Log.e(javaClass.name, "Error while creating installer", e1)
            return
        }
    }

    fun callBatteryInfo(@Suppress("UNUSED_PARAMETER")view: View) {
        val intent = Intent(this, BatteryActivity::class.java)

        startActivity(intent)
    }
  }