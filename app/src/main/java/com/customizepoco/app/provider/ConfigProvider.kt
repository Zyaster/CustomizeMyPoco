package com.customizepoco.app.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
import android.os.Bundle

class ConfigProvider : ContentProvider() {

    companion object {
        const val AUTHORITY = "com.customizepoco.app.provider"
        const val METHOD_GET_CONFIG = "getConfig"
        const val METHOD_SET_CONFIG = "setConfig"

        const val PREFS_NAME = "poco_light_config"
        const val KEY_ICON_SCALE = "icon_scale_factor"
        const val KEY_HIDE_LABELS = "hide_icon_labels"
        const val KEY_SUPER_ICONS = "unlock_super_icons"
        const val KEY_LIQUID_GLASS = "force_liquid_glass"
        const val KEY_DEVICE_LEVEL = "force_device_level"
        const val KEY_UNLOCK_120HZ = "unlock_120hz"
        const val KEY_BLUR_RADIUS = "blur_radius"

        fun getPrefs(context: Context): SharedPreferences {
            return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        }
    }

    override fun onCreate(): Boolean = true

    override fun call(method: String, arg: String?, extras: Bundle?): Bundle? {
        val ctx = context ?: return null
        val prefs = getPrefs(ctx)

        return when (method) {
            METHOD_GET_CONFIG -> {
                Bundle().apply {
                    putFloat(KEY_ICON_SCALE, prefs.getFloat(KEY_ICON_SCALE, 1.0f))
                    putBoolean(KEY_HIDE_LABELS, prefs.getBoolean(KEY_HIDE_LABELS, false))
                    putBoolean(KEY_SUPER_ICONS, prefs.getBoolean(KEY_SUPER_ICONS, true))
                    putBoolean(KEY_LIQUID_GLASS, prefs.getBoolean(KEY_LIQUID_GLASS, true))
                    putBoolean(KEY_DEVICE_LEVEL, prefs.getBoolean(KEY_DEVICE_LEVEL, true))
                    putBoolean(KEY_UNLOCK_120HZ, prefs.getBoolean(KEY_UNLOCK_120HZ, true))
                    putInt(KEY_BLUR_RADIUS, prefs.getInt(KEY_BLUR_RADIUS, 45))
                }
            }
            METHOD_SET_CONFIG -> {
                extras?.let { bundle ->
                    val editor = prefs.edit()
                    if (bundle.containsKey(KEY_ICON_SCALE)) editor.putFloat(KEY_ICON_SCALE, bundle.getFloat(KEY_ICON_SCALE))
                    if (bundle.containsKey(KEY_HIDE_LABELS)) editor.putBoolean(KEY_HIDE_LABELS, bundle.getBoolean(KEY_HIDE_LABELS))
                    if (bundle.containsKey(KEY_SUPER_ICONS)) editor.putBoolean(KEY_SUPER_ICONS, bundle.getBoolean(KEY_SUPER_ICONS))
                    if (bundle.containsKey(KEY_LIQUID_GLASS)) editor.putBoolean(KEY_LIQUID_GLASS, bundle.getBoolean(KEY_LIQUID_GLASS))
                    if (bundle.containsKey(KEY_DEVICE_LEVEL)) editor.putBoolean(KEY_DEVICE_LEVEL, bundle.getBoolean(KEY_DEVICE_LEVEL))
                    if (bundle.containsKey(KEY_UNLOCK_120HZ)) editor.putBoolean(KEY_UNLOCK_120HZ, bundle.getBoolean(KEY_UNLOCK_120HZ))
                    if (bundle.containsKey(KEY_BLUR_RADIUS)) editor.putInt(KEY_BLUR_RADIUS, bundle.getInt(KEY_BLUR_RADIUS))
                    editor.apply()
                }
                Bundle().apply { putBoolean("success", true) }
            }
            else -> null
        }
    }

    override fun query(uri: Uri, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor? = null
    override fun getType(uri: Uri): String? = null
    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int = 0
}
