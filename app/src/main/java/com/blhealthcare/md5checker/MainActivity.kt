package com.blhealthcare.md5checker

import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ListView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import org.json.JSONArray
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.security.MessageDigest
import java.util.*

class MainActivity : AppCompatActivity() {
    companion object {
        private val TAG: String? = MainActivity::class.simpleName
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        listView = findViewById(R.id.appList)
    }

    private val md5AppArray: JSONArray = JSONArray()
    private lateinit var appAdapter: AppAdapter
    private lateinit var listView: ListView

    override fun onResume() {
        super.onResume()
        initProgress()
        initSearch()
        initList()
    }

    override fun onPause() {
        appAdapter.clear()
        super.onPause()
    }

    private lateinit var searchBox: EditText
    private lateinit var progressBar: ProgressBar

    private fun initProgress() {
        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE
    }

    private fun initSearch() {
        searchBox = findViewById(R.id.searchBox)
        searchBox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty() && !s.isNullOrBlank()) {
                    loadApps(s.toString())
                } else {
                    appAdapter.clear()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun initList() {
        appAdapter = AppAdapter(context = this)
        listView.adapter = appAdapter
    }

    private fun loadApps(key: String) {
        Log.d(TAG, "loadApps: KEY - $key")
        runOnUiThread {
            progressBar.visibility = View.VISIBLE
            listView.visibility = View.GONE
        }
        val pm: PackageManager = packageManager
        var installedApplications = pm.getInstalledApplications(0)
        installedApplications = installedApplications.filter { info ->
            pm.getApplicationLabel(info).contains(key)
        }
        if (installedApplications.size > 5) {
            searchBox.error = "Please try to be more specific!"
            progressBar.visibility = View.GONE
            return
        } else {
            searchBox.error = null
            appAdapter.clear()
        }
        installedApplications.forEach {
            try {
                val md5 = File(it.publicSourceDir).getMD5HashHex()
                if (md5.isNotEmpty()) {
                    val appInfo =
                        AppInfo(pm.getApplicationLabel(it).toString(), it.packageName, md5)
                    runOnUiThread {
                        appAdapter.add(appInfo)
                    }
                    md5AppArray.put(Gson().toJson(appInfo).toString())
                }
            } catch (e: Exception) {
                Log.e(TAG, "onResume: Failed to get md5 for " + it.name, e)
                return@forEach //continue
            }
        }

        runOnUiThread {
            appAdapter.notifyDataSetChanged()
            progressBar.visibility = View.GONE
            listView.visibility = View.VISIBLE
        }

    }

    private fun File.getMD5HashHex(): String {
        val md = MessageDigest.getInstance("MD5")
        var res = ""
        val stream: InputStream
        stream = FileInputStream(this)

        val buffer = ByteArray(8192)
        var read: Int
        while (stream.read(buffer).also { read = it } > 0) {
            md.update(buffer, 0, read)
        }
        stream.close()
        md.digest().forEach { b ->
            res += String.format("%1$02X", b)
        }
        return res.toLowerCase(Locale.ROOT)
    }
}