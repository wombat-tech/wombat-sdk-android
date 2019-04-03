package com.chainwisegroup.wombatsdk

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.annotation.DrawableRes

object Wombat {
    fun isAvailable(context: Context): Boolean {
        val pm = context.packageManager
        return try {
            pm.getApplicationInfo("com.chainwisegroup.wombat.debug", PackageManager.GET_META_DATA)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            Log.wtf("WOMBAT", "Wombat not found", e)
            false
        }
    }

    fun getLoginIntent(): Intent {
        val componentName =
            ComponentName("com.chainwisegroup.wombat.debug", "com.chainwisegroup.wombat.exposed.LoginActivity")
        return Intent().apply {
            component = componentName
        }
    }

    fun getLoginIntent(@DrawableRes icon: Int): Intent {
        return getLoginIntent().apply {
            putExtra("icon_res", icon)
        }
    }

    fun getLoginIntent(icon: String): Intent {
        return getLoginIntent().apply {
            putExtra("icon_url", icon)
        }
    }

    fun requestTransfer(nativeTransferRequest: NativeTransferRequest): Intent {
        val componentName = ComponentName(
            "com.chainwisegroup.wombat.debug",
            "com.chainwisegroup.wombat.exposed.TransferRequestActivity"
        )
        return Intent().apply {
            component = componentName
            val b = Bundle()
            nativeTransferRequest.writeToBundle(b)
            putExtras(b)
        }
    }


    fun getInfoFromResult(intent: Intent?): AccountInfo? {

        val name = intent?.getStringExtra("name")
        val publicKey = intent?.getStringExtra("publicKey")
        if (name != null && publicKey != null) {
            return AccountInfo(name, publicKey)
        } else {
            return null
        }
    }


}