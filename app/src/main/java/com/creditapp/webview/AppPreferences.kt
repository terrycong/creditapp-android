package com.creditapp.webview

import android.content.Context

class AppPreferences(context: Context) {
    
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    companion object {
        private const val PREFS_NAME = "creditapp_prefs"
        private const val KEY_SERVER_URL = "server_url"
        private const val KEY_REMEMBER_ME = "remember_me"
        private const val KEY_LAST_SYNC = "last_sync"
        private const val DEFAULT_URL = "http://localhost:8080"
    }
    
    // 服务器地址
    fun getServerUrl(): String? {
        return prefs.getString(KEY_SERVER_URL, null)
    }
    
    fun setServerUrl(url: String) {
        prefs.edit().putString(KEY_SERVER_URL, url).apply()
    }
    
    fun clearServerUrl() {
        prefs.edit().remove(KEY_SERVER_URL).apply()
    }
    
    // 是否记住登录
    fun getRememberMe(): Boolean {
        return prefs.getBoolean(KEY_REMEMBER_ME, false)
    }
    
    fun setRememberMe(remember: Boolean) {
        prefs.edit().putBoolean(KEY_REMEMBER_ME, remember).apply()
    }
    
    // 最后同步时间
    fun getLastSync(): Long {
        return prefs.getLong(KEY_LAST_SYNC, 0)
    }
    
    fun setLastSync(time: Long) {
        prefs.edit().putLong(KEY_LAST_SYNC, time).apply()
    }
    
    // 清除所有设置
    fun clearAll() {
        prefs.edit().clear().apply()
    }
}