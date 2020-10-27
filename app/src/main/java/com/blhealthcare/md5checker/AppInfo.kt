package com.blhealthcare.md5checker

import com.google.gson.annotations.SerializedName

data class AppInfo(
    @SerializedName("AppName")
    val appName: String,

    @SerializedName("PackageName")
    val packageName: String,

    @SerializedName("MD5")
    val md5: String
)