package com.customizepoco.xposed

import android.content.Context
import android.net.Uri
import android.os.Bundle
import de.robv.android.xposed.XposedBridge

object ConfigClient {
    private const val URI_STR = "content://com.customizepoco.app.provider"
    private val CONTENT_URI = Uri.parse(URI_STR)

    var iconScale: Float = 1.0f
    var hideLabels: Boolean = false
    var unlockSuperIcons: Boolean = true
    var forceLiquidGlass: Boolean = true
    var forceDeviceLevel: Boolean = true
    var unlock120Hz: Boolean = true
    var blurRadius: Int = 45

    fun load(context: Context) {
        try {
            val bundle: Bundle? = context.contentResolver.call(
                CONTENT_URI,
                "getConfig",
                null,
                null
            )
            bundle?.let {
                iconScale = it.getFloat("icon_scale_factor", 1.0f)
                hideLabels = it.getBoolean("hide_icon_labels", false)
                unlockSuperIcons = it.getBoolean("unlock_super_icons", true)
                forceLiquidGlass = it.getBoolean("force_liquid_glass", true)
                forceDeviceLevel = it.getBoolean("force_device_level", true)
                unlock120Hz = it.getBoolean("unlock_120hz", true)
                blurRadius = it.getInt("blur_radius", 45)
            }
        } catch (t: Throwable) {
            XposedBridge.log("[CustomizeMyPoco] Failed to fetch config from provider: ${t.message}")
        }
    }
}
