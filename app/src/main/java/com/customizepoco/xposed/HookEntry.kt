package com.customizepoco.xposed

import android.app.Application
import android.content.Context
import com.customizepoco.xposed.hooks.BuildPropHook
import com.customizepoco.xposed.hooks.PocoLauncherHook
import com.customizepoco.xposed.hooks.SystemUiHook
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class HookEntry : IXposedHookLoadPackage {

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        val pkg = lpparam.packageName

        when (pkg) {
            "android" -> {
                BuildPropHook.hookSystem(lpparam)
            }
            "com.android.systemui", "miui.systemui.plugin" -> {
                hookWithContext(lpparam) { context ->
                    ConfigClient.load(context)
                    SystemUiHook.hook(lpparam, context)
                }
            }
            "com.mi.android.globallauncher", "com.miui.home" -> {
                hookWithContext(lpparam) { context ->
                    ConfigClient.load(context)
                    PocoLauncherHook.hook(lpparam, context)
                }
            }
        }
    }

    private fun hookWithContext(lpparam: XC_LoadPackage.LoadPackageParam, onContextReady: (Context) -> Unit) {
        try {
            XposedHelpers.findAndHookMethod(
                Application::class.java,
                "attach",
                Context::class.java,
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        val context = param.args[0] as? Context ?: return
                        try {
                            onContextReady(context)
                        } catch (t: Throwable) {
                            XposedBridge.log("[CustomizeMyPoco] Error in post-attach hook for ${lpparam.packageName}: ${t.message}")
                        }
                    }
                }
            )
        } catch (t: Throwable) {
            XposedBridge.log("[CustomizeMyPoco] Could not hook Application.attach for ${lpparam.packageName}: ${t.message}")
        }
    }
}
