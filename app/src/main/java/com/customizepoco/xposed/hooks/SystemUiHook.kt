package com.customizepoco.xposed.hooks

import android.content.Context
import com.customizepoco.xposed.ConfigClient
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

object SystemUiHook {

    fun hook(lpparam: XC_LoadPackage.LoadPackageParam, context: Context) {
        hookControlCenterBlur(lpparam)
        hook120HzUnlock(lpparam)
    }

    /**
     * Unlocks Gaussian Blur & Liquid Glass styling in HyperOS Control Center & Notification Shade
     */
    private fun hookControlCenterBlur(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (!ConfigClient.forceLiquidGlass) return

        val blurClasses = listOf(
            "com.android.systemui.statusbar.phone.ScrimController",
            "miui.systemui.controlcenter.panel.ControlCenterPanelView",
            "miui.systemui.controlcenter.utils.ControlCenterBlurUtils",
            "miui.systemui.utils.BlurUtils",
            "com.android.systemui.controlcenter.utils.ControlCenterBlurUtils"
        )

        for (className in blurClasses) {
            try {
                val clazz = XposedHelpers.findClassIfExists(className, lpparam.classLoader) ?: continue

                // Force blur capabilities to true
                val booleanMethods = listOf(
                    "isBlurSupported",
                    "isDeviceSupportBlur",
                    "isHighDevice",
                    "supportBlur",
                    "isLiquidGlassEnabled"
                )

                for (m in booleanMethods) {
                    try {
                        XposedHelpers.findAndHookMethod(clazz, m, object : XC_MethodHook() {
                            override fun beforeHookedMethod(param: MethodHookParam) {
                                param.result = true
                            }
                        })
                    } catch (_: Throwable) {}
                }

                // Override blur radius with custom value
                try {
                    XposedHelpers.findAndHookMethod(clazz, "getBlurRadius", object : XC_MethodHook() {
                        override fun beforeHookedMethod(param: MethodHookParam) {
                            param.result = ConfigClient.blurRadius
                        }
                    })
                } catch (_: Throwable) {}

            } catch (t: Throwable) {
                XposedBridge.log("[CustomizeMyPoco] Blur hook exception for $className: ${t.message}")
            }
        }
    }

    /**
     * Prevent SystemUI from dropping display refresh rate from 120Hz to 60Hz when opening panels
     */
    private fun hook120HzUnlock(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (!ConfigClient.unlock120Hz) return

        try {
            val policyClass = XposedHelpers.findClassIfExists(
                "com.android.systemui.statusbar.phone.CentralSurfacesImpl",
                lpparam.classLoader
            ) ?: XposedHelpers.findClassIfExists(
                "com.android.systemui.statusbar.phone.StatusBar",
                lpparam.classLoader
            )

            if (policyClass != null) {
                val methods = listOf("requestCustomFps", "setPanelFpsLimit")
                for (m in methods) {
                    try {
                        XposedBridge.hookAllMethods(policyClass, m, object : XC_MethodHook() {
                            override fun beforeHookedMethod(param: MethodHookParam) {
                                // Prevent downgrading FPS to 60hz
                                param.result = null
                            }
                        })
                    } catch (_: Throwable) {}
                }
            }
        } catch (_: Throwable) {}
    }
}
