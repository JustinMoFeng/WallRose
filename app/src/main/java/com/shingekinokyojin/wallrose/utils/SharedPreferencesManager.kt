package com.shingekinokyojin.wallrose.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys


object SharedPreferencesManager {
    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        sharedPreferences = EncryptedSharedPreferences.create(
            "MySecurePreferences", // 文件名
            MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC), // 密钥别名
            context, // 上下文
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, // 密钥加密方案
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM // 值加密方案
        )
    }

    fun saveToken(token: String) {
        sharedPreferences.edit().putString("wr_token", token).apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString("wr_token", null)
    }

    fun deleteToken() {
        sharedPreferences.edit().remove("wr_token").apply()
    }
}
