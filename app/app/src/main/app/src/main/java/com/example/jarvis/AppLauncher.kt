package com.example.jarvis

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.net.Uri

class AppLauncher(private val context: Context) {

    private val installedApps: Map<String, String> by lazy { buildAppIndex() }

    private fun buildAppIndex(): Map<String, String> {
        val pm = context.packageManager
        val apps: List<ApplicationInfo> = pm.getInstalledApplications(0)
        val map = mutableMapOf<String, String>()
        for (app in apps) {
            if (pm.getLaunchIntentForPackage(app.packageName) == null) continue
            val label = pm.getApplicationLabel(app).toString().lowercase()
            map[label] = app.packageName
        }
        return map
    }

    fun openAppByName(spokenName: String): Boolean {
        val name = spokenName.trim().lowercase()

        installedApps[name]?.let {
            return launchPackage(it)
        }

        val match = installedApps.entries.firstOrNull {
            it.key.contains(name) || name.contains(it.key)
        }
        if (match != null) {
            return launchPackage(match.value)
        }

        return false
    }

    private fun launchPackage(packageName: String): Boolean {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName) ?: return false
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        return true
    }

    fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun webSearch(query: String) {
        val intent = Intent(Intent.ACTION_WEB_SEARCH)
        intent.putExtra("query", query)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun dial(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun openSettings() {
        val intent = Intent(android.provider.Settings.ACTION_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun openCamera() {
        val intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}
