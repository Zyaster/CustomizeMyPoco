package com.customizepoco.xposed.hooks

import android.content.Context
import android.view.View
import android.widget.TextView
import com.customizepoco.xposed.ConfigClient
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

object PocoLauncherHook {

    fun hook(lpparam: XC_LoadPackage.LoadPackageParam, context: Context) {
        hookIconScaling(lpparam)
        hookIconLabels(lpparam)
        hookSuperIconsAndFolders(lpparam)
        hookRecentTasksBlur(lpparam)
    }

    /**
     * Over-scale icon sizes beyond standard limits (50% - 200%+)
     */
    private fun hookIconScaling(lpparam: XC_LoadPackage.LoadPackageParam) {
        val scale = ConfigClient.iconScale
        if (scale == 1.0f) return

        // 1. Hook DeviceConfig.getIconScaleRatio() in POCO Launcher / MIUI Home
        try {
            val deviceConfigClass = XposedHelpers.findClassIfExists("com.miui.home.launcher.DeviceConfig", lpparam.classLoader)
                ?: XposedHelpers.findClassIfExists("com.mi.android.globallauncher.DeviceConfig", lpparam.classLoader)

            if (deviceConfigClass != null) {
                val methods = listOf("getIconScaleRatio", "calcIconScaleRatio", "getLauncherIconScale")
                for (m in methods) {
                    try {
                        XposedHelpers.findAndHookMethod(deviceConfigClass, m, object : XC_MethodHook() {
                            override fun afterHookedMethod(param: MethodHookParam) {
                                val original = (param.result as? Number)?.toFloat() ?: 1.0f
                                param.result = original * scale
                            }
                        })
                    } catch (_: Throwable) {}
                }
            }
        } catch (t: Throwable) {
            XposedBridge.log("[CustomizeMyPoco] Error hooking DeviceConfig icon scale: ${t.message}")
        }

        // 2. Hook DeviceProfile pixel sizes for icons & hotseat
        try {
            val deviceProfileClass = XposedHelpers.findClassIfExists("com.miui.home.launcher.DeviceProfile", lpparam.classLoader)
                ?: XposedHelpers.findClassIfExists("com.android.launcher3.DeviceProfile", lpparam.classLoader)
                ?: XposedHelpers.findClassIfExists("com.mi.android.globallauncher.DeviceProfile", lpparam.classLoader)

            if (deviceProfileClass != null) {
                XposedBridge.hookAllConstructors(deviceProfileClass, object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        try {
                            scaleField(param.thisObject, "iconSizePx", scale)
                            scaleField(param.thisObject, "allAppsIconSizePx", scale)
                            scaleField(param.thisObject, "hotseatIconSizePx", scale)
                            scaleField(param.thisObject, "folderIconSizePx", scale)
                        } catch (_: Throwable) {}
                    }
                })
            }
        } catch (t: Throwable) {
            XposedBridge.log("[CustomizeMyPoco] Error hooking DeviceProfile constructor: ${t.message}")
        }
    }

    /**
     * Option to hide icon titles/labels under apps on home screen
     */
    private fun hookIconLabels(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (!ConfigClient.hideLabels) return

        val textClasses = listOf(
            "com.miui.home.launcher.ItemIcon",
            "com.miui.home.launcher.common.ItemIcon",
            "com.mi.android.globallauncher.ItemIcon",
            "com.android.launcher3.BubbleTextView"
        )

        for (className in textClasses) {
            try {
                val clazz = XposedHelpers.findClassIfExists(className, lpparam.classLoader) ?: continue

                // Hook setText / setTitle
                val methods = listOf("setText", "setTitle")
                for (m in methods) {
                    try {
                        XposedBridge.hookAllMethods(clazz, m, object : XC_MethodHook() {
                            override fun beforeHookedMethod(param: MethodHookParam) {
                                param.args[0] = ""
                            }
                        })
                    } catch (_: Throwable) {}
                }

                // Or hide title view visibility if separate
                try {
                    XposedHelpers.findAndHookMethod(clazz, "getTitleView", object : XC_MethodHook() {
                        override fun afterHookedMethod(param: MethodHookParam) {
                            (param.result as? View)?.visibility = View.GONE
                        }
                    })
                } catch (_: Throwable) {}
            } catch (_: Throwable) {}
        }
    }

    /**
     * Unlock XXL 3x3 Large Folders & Super Icons
     */
    private fun hookSuperIconsAndFolders(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (!ConfigClient.unlockSuperIcons) return

        val folderInfoClasses = listOf(
            "com.miui.home.launcher.FolderInfo",
            "com.mi.android.globallauncher.FolderInfo"
        )

        for (className in folderInfoClasses) {
            try {
                val clazz = XposedHelpers.findClassIfExists(className, lpparam.classLoader) ?: continue
                val unlockMethods = listOf("canCreateLargeFolder", "isLargeFolderSupported", "isSuperIconSupported")
                for (m in unlockMethods) {
                    try {
                        XposedHelpers.findAndHookMethod(clazz, m, object : XC_MethodHook() {
                            override fun beforeHookedMethod(param: MethodHookParam) {
                                param.result = true
                            }
                        })
                    } catch (_: Throwable) {}
                }
            } catch (_: Throwable) {}
        }
    }

    /**
     * Force blur behind recent apps cards
     */
    private fun hookRecentTasksBlur(lpparam: XC_LoadPackage.LoadPackageParam) {
        try {
            val recentsClass = XposedHelpers.findClassIfExists("com.miui.home.recents.views.RecentsContainer", lpparam.classLoader)
                ?: XposedHelpers.findClassIfExists("com.mi.android.globallauncher.recents.views.RecentsContainer", lpparam.classLoader)

            if (recentsClass != null) {
                val blurMethods = listOf("isBlurSupported", "canUseBlur", "isDeviceSupportBlur")
                for (m in blurMethods) {
                    try {
                        XposedHelpers.findAndHookMethod(recentsClass, m, object : XC_MethodHook() {
                            override fun beforeHookedMethod(param: MethodHookParam) {
                                param.result = true
                            }
                        })
                    } catch (_: Throwable) {}
                }
            }
        } catch (_: Throwable) {}
    }

    private fun scaleField(obj: Any, fieldName: String, factor: Float) {
        try {
            val original = XposedHelpers.getIntField(obj, fieldName)
            if (original > 0) {
                XposedHelpers.setIntField(obj, fieldName, (original * factor).toInt())
            }
        } catch (_: Throwable) {}
    }
}
