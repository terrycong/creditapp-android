package com.creditapp.webview

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.progressindicator.LinearProgressIndicator

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var progressBar: LinearProgressIndicator
    private lateinit var noNetworkView: View
    
    private lateinit var prefs: AppPreferences

    companion object {
        const val DEFAULT_URL = "http://localhost:8080"
        const val REQUEST_CODE_SETTINGS = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = AppPreferences(this)
        
        initViews()
        setupWebView()
        checkNetworkAndLoad()
    }

    private fun initViews() {
        webView = findViewById(R.id.webView)
        swipeRefresh = findViewById(R.id.swipeRefresh)
        progressBar = findViewById(R.id.progressBar)
        noNetworkView = findViewById(R.id.noNetworkView)

        swipeRefresh.setOnRefreshListener {
            webView.reload()
        }

        // 点击重试
        noNetworkView.setOnClickListener {
            checkNetworkAndLoad()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        // WebView 基础设置
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            setSupportZoom(true)
            builtInZoomControls = true
            displayZoomControls = false
            useWideViewPort = true
            loadWithOverviewMode = true
            setSupportMultipleWindows(false)
            cacheMode = android.webkit.WebSettings.LOAD_DEFAULT
            
            // 允许混合内容（HTTP + HTTPS）
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }
        }

        // Cookie 管理
        CookieManager.getInstance().apply {
            setAcceptCookie(true)
            setAcceptThirdPartyCookies(webView, true)
        }

        // WebViewClient
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val url = request?.url.toString()
                // 拦截外部链接，在应用内打开
                return false
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressBar.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressBar.visibility = View.GONE
                swipeRefresh.isRefreshing = false
            }

            override fun onReceivedError(
                view: WebView?,
                errorCode: Int,
                description: String?,
                failingUrl: String?
            ) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                showError("加载失败: $description")
            }
        }

        // WebChromeClient - 用于进度条和 JS 对话框
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                progressBar.progress = newProgress
                if (newProgress == 100) {
                    progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun checkNetworkAndLoad() {
        if (isNetworkAvailable()) {
            noNetworkView.visibility = View.GONE
            webView.visibility = View.VISIBLE
            loadUrl()
        } else {
            noNetworkView.visibility = View.VISIBLE
            webView.visibility = View.GONE
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo
            @Suppress("DEPRECATION")
            return networkInfo?.isConnected == true
        }
    }

    private fun loadUrl() {
        val url = prefs.getServerUrl()
        if (url.isNullOrBlank()) {
            // 首次使用，弹出配置对话框
            showConfigDialog()
        } else {
            webView.loadUrl(url)
        }
    }

    private fun showConfigDialog() {
        val currentUrl = prefs.getServerUrl() ?: DEFAULT_URL
        
        val builder = AlertDialog.Builder(this)
        builder.setTitle("配置服务器地址")
        
        val input = android.widget.EditText(this).apply {
            setText(currentUrl)
            hint = "例如: http://192.168.1.100:8080"
            setSingleLine()
            setPadding(50, 30, 50, 30)
        }
        
        builder.setView(input)
        
        builder.setPositiveButton("保存并连接") { _, _ ->
            val newUrl = input.text.toString().trim()
            if (newUrl.isNotBlank()) {
                prefs.setServerUrl(newUrl)
                webView.loadUrl(newUrl)
            } else {
                showError("请输入有效的服务器地址")
            }
        }
        
        builder.setNegativeButton("使用默认") { _, _ ->
            prefs.setServerUrl(DEFAULT_URL)
            webView.loadUrl(DEFAULT_URL)
        }
        
        builder.setCancelable(false)
        builder.show()
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        swipeRefresh.isRefreshing = false
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivityForResult(
                    Intent(this, SettingsActivity::class.java),
                    REQUEST_CODE_SETTINGS
                )
                true
            }
            R.id.action_refresh -> {
                webView.reload()
                true
            }
            R.id.action_home -> {
                loadUrl()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SETTINGS && resultCode == RESULT_OK) {
            // 设置已更新，重新加载
            webView.loadUrl(prefs.getServerUrl() ?: DEFAULT_URL)
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        webView.onResume()
    }

    override fun onPause() {
        super.onPause()
        webView.onPause()
    }

    override fun onDestroy() {
        webView.destroy()
        super.onDestroy()
    }
}