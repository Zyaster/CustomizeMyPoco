package com.customizepoco.xposed.hooks

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

object BuildPropHook {

    fun hookSystem(lpparam: XC_LoadPackage.LoadPackageParam) {
        try {
            val buildClass = XposedHelpers.findClassIfExists("miui.os.Build", lpparam.classLoader)
            if (buildClass != null) {
                // Spoof IS_POCO & IS_POCO_PHONE to false
                trySetStaticBooleanField(buildClass, "IS_POCO", false)
                trySetStaticBooleanField(buildClass, "IS_POCO_PHONE", false)

                // Force high-end device level (3 = flagship profile)
                trySetStaticIntField(buildClass, "DEVICE_LEVEL", 3)
                trySetStaticIntField(buildClass, "DEVICE_LEVEL_CPU", 3)
                trySetStaticIntField(buildClass, "DEVICE_LEVEL_GPU", 3)
                trySetStaticIntField(buildClass, "DEVICE_LEVEL_RAM", 3)

                hookMethods(buildClass)
                XposedBridge.log("[CustomizeMyPoco] Successfully spoofed miui.os.Build flags to flagship profile")
            }
        } catch (t: Throwable) {
            XposedBridge.log("[CustomizeMyPoco] Error in BuildPropHook: ${t.message}")
        }
    }

    private fun hookMethods(clazz: Class<*>) {
        val methodsToTrue = listOf("isBlurSupported", "isFlagship", "isHighEndDevice", "isLiquidGlassSupported")
        for (m in methodsToTrue) {
            try {
                XposedHelpers.findAndHookMethod(clazz, m, object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        param.result = true
                    }
                })
            } catch (_: Throwable) {}
        }
    }

    private fun trySetStaticBooleanField(clazz: Class<*>, fieldName: String, value: Boolean) {
        try {
            XposedHelpers.setStaticBooleanField(clazz, fieldName, value)
        } catch (_: Throwable) {}
    }

    private fun trySetStaticIntField(clazz: Class<*>, fieldName: String, value: Int) {
        try {
            XposedHelpers.setStaticIntField(clazz, fieldName, value)
        } catch (_: Throwable) {}
    }
}
