package com.creditapp.webview

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class SettingsActivity : AppCompatActivity() {

    private lateinit var prefs: AppPreferences
    private lateinit var etServerUrl: EditText
    private lateinit var switchRememberMe: Switch
    private lateinit var btnSave: Button
    private lateinit var btnReset: Button
    private lateinit var btnClearCache: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        prefs = AppPreferences(this)
        
        setupToolbar()
        initViews()
        loadSettings()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "设置"
    }

    private fun initViews() {
        etServerUrl = findViewById(R.id.etServerUrl)
        switchRememberMe = findViewById(R.id.switchRememberMe)
        btnSave = findViewById(R.id.btnSave)
        btnReset = findViewById(R.id.btnReset)
        btnClearCache = findViewById(R.id.btnClearCache)

        btnSave.setOnClickListener {
            saveSettings()
        }

        btnReset.setOnClickListener {
            showResetDialog()
        }

        btnClearCache.setOnClickListener {
            clearCache()
        }
    }

    private fun loadSettings() {
        val serverUrl = prefs.getServerUrl()
        if (!serverUrl.isNullOrBlank()) {
            etServerUrl.setText(serverUrl)
        }
        switchRememberMe.isChecked = prefs.getRememberMe()
    }

    private fun saveSettings() {
        val url = etServerUrl.text.toString().trim()
        
        if (url.isBlank()) {
            showError("请输入服务器地址")
            return
        }
        
        // 简单的 URL 格式验证
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            showError("地址必须以 http:// 或 https:// 开头")
            return
        }
        
        prefs.setServerUrl(url)
        prefs.setRememberMe(switchRememberMe.isChecked)
        
        Toast.makeText(this, "设置已保存", Toast.LENGTH_SHORT).show()
        setResult(RESULT_OK)
        finish()
    }

    private fun showResetDialog() {
        AlertDialog.Builder(this)
            .setTitle("重置设置")
            .setMessage("确定要重置所有设置吗？这将清除保存的服务器地址。")
            .setPositiveButton("重置") { _, _ ->
                prefs.clearAll()
                etServerUrl.setText("")
                switchRememberMe.isChecked = false
                Toast.makeText(this, "设置已重置", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun clearCache() {
        try {
            // 清除 WebView 缓存
            val webView = android.webkit.WebView(this)
            webView.clearCache(true)
            
            // 清除 Cookie
            android.webkit.CookieManager.getInstance().removeAllCookies(null)
            
            // 清除应用缓存
            cacheDir.deleteRecursively()
            
            Toast.makeText(this, "缓存已清除", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            showError("清除缓存失败: ${e.message}")
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}