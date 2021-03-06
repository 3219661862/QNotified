package me.singleneuron.hook

import android.app.Activity
import android.content.Intent
import android.util.Log
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import me.singleneuron.base.BaseDelayableHookAdapter
import me.singleneuron.util.KotlinUtils
import nil.nadph.qnotified.SyncUtils
import nil.nadph.qnotified.util.Utils

object DebugDump : BaseDelayableHookAdapter("debugDump",SyncUtils.PROC_ANY) {

    override fun doInit(): Boolean {

        //dump startActivity
        val hook = object : XposedMethodHookAdapter() {
            override fun beforeMethod(param: MethodHookParam?) {
                val intent : Intent = param!!.args[0] as Intent
                Utils.logd("debugDump: startActivity")
                KotlinUtils.dumpIntent(intent)
            }
        }
        XposedBridge.hookAllMethods(Activity::class.java,"startActivity", hook)
        XposedBridge.hookAllMethods(Activity::class.java,"startActivityForResult", hook)

        //dump setResult
        XposedBridge.hookAllMethods(Activity::class.java,"setResult", object : XposedMethodHookAdapter() {
            override fun beforeMethod(param: MethodHookParam?) {
                if (param!!.args.size!=2) return
                val intent = param.args[1] as Intent
                Utils.logd("debugDump: setResult "+param.thisObject::class.java.name)
                KotlinUtils.dumpIntent(intent)
            }
        })
        return true
    }
}