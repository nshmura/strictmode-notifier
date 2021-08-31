package io.github.juanimoli.strictmode.notifier.ui.adapter

import android.annotation.TargetApi
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.StrictMode
import android.security.NetworkSecurityPolicy
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.Toast
import io.github.juanimoli.strictmode.notifier.ViolationTypeInfo
import io.github.juanimoli.strictmode.notifier.commons.ViolationType
import io.github.juanimoli.strictmode.notifier.helper.LeakedSQLiteOpenHelper
import io.github.juanimoli.strictmode.notifier.testapp.R
import io.github.juanimoli.strictmode.notifier.ui.ClassInstanceLimitActivity
import io.github.juanimoli.strictmode.notifier.ui.LeakedClosableObjectsActivity
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

internal class Adapter : BaseAdapter() {
    private val values: Array<ViolationType>

    init {
        val list = ArrayList(listOf(*ViolationType.values()))
        list.remove(ViolationType.ACTIVITY_LEAKS)
        list.remove(ViolationType.LEAKED_REGISTRATION_OBJECTS)
        list.remove(ViolationType.LEAKED_SQL_LITE_OBJECTS)
        list.remove(ViolationType.UNKNOWN)
        values = list.toTypedArray()
    }

    override fun getCount(): Int {
        return values.size
    }

    override fun getItem(position: Int): ViolationType {
        return values[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        var convertView = view
        if (convertView == null) {
            convertView =
                LayoutInflater.from(parent.context).inflate(R.layout.row_actions, parent, false)
        }
        val info = ViolationTypeInfo.convert(getItem(position))
        val text = String.format(
            "%s error (minSdk %s)",
            ViolationTypeInfo.convert(info.violationType).violationName(),
            info.minSdkVersion
        )

        val button = convertView!!.findViewById<Button>(R.id.button)
        button.text = text
        button.isEnabled = Build.VERSION.SDK_INT >= info.minSdkVersion
        button.setOnClickListener {
            fireStrictModeError(
                convertView.context,
                info
            )
        }
        button.isAllCaps = false

        return convertView
    }

    private fun fireStrictModeError(context: Context, info: ViolationTypeInfo) {
        if (Build.VERSION.SDK_INT < info.minSdkVersion) {
            Toast.makeText(context, "Min SDK version " + info.minSdkVersion, Toast.LENGTH_LONG)
                .show()
            return
        }

        when (info) {
            ViolationTypeInfo.CUSTOM_SLOW_CALL -> fireSlowCall()
            ViolationTypeInfo.NETWORK -> fireNetwork()
            ViolationTypeInfo.RESOURCE_MISMATCHES -> fireResourceMismatches(context)
            ViolationTypeInfo.CLASS_INSTANCE_LIMIT -> fireClassInstanceLimit(context)
            ViolationTypeInfo.CLEARTEXT_NETWORK -> fireCleartextNetwork()
            ViolationTypeInfo.FILE_URI_EXPOSURE -> fireFileUriExposure(context)
            ViolationTypeInfo.LEAKED_CLOSABLE_OBJECTS -> fireLeakedClosableObjects(context)
            ViolationTypeInfo.ACTIVITY_LEAKS -> fireActivityLeaks()
            ViolationTypeInfo.LEAKED_REGISTRATION_OBJECTS -> fireLeakedRegistrationObjects(context)
            ViolationTypeInfo.LEAKED_SQL_LITE_OBJECTS -> fireLeakedSqlLiteObjects(context)
            ViolationTypeInfo.UNKNOWN -> doNothing()
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private fun fireSlowCall() {
        StrictMode.noteSlowCall("fireSlowCall")
    }

    private fun fireNetwork() {
        try {
            val url = URL("https://google.com/")
            val con = url.openConnection() as HttpURLConnection
            con.requestMethod = "GET"
            con.connect()
            con.disconnect()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun fireResourceMismatches(context: Context) {
        val names = context.resources.obtainTypedArray(R.array.sample_names)
        names.getInt(0, 0)
        names.recycle()
    }

    private fun fireClassInstanceLimit(context: Context) {
        ClassInstanceLimitActivity.start(context, 2)
    }

    // https://koz.io/android-m-and-the-war-on-cleartext-traffic/
    @TargetApi(Build.VERSION_CODES.M)
    private fun fireCleartextNetwork() {
        val runnable = Runnable {
            try {
                val method = NetworkSecurityPolicy.getInstance()
                    .javaClass
                    .getMethod("setCleartextTrafficPermitted", Boolean::class.javaPrimitiveType)
                method.invoke(NetworkSecurityPolicy.getInstance(), false)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            try {
                val url = URL("http://google.com/")
                val con = url.openConnection() as HttpURLConnection
                con.requestMethod = "GET"
                con.connect()
                con.disconnect()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            try {
                val method = NetworkSecurityPolicy.getInstance()
                    .javaClass
                    .getMethod("setCleartextTrafficPermitted", Boolean::class.javaPrimitiveType)
                method.invoke(NetworkSecurityPolicy.getInstance(), true)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        Thread(runnable).start()
    }

    private fun fireFileUriExposure(context: Context) {
        val file = context.getExternalFilesDir(".")
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("file://$file"))
        context.startActivity(intent)
    }

    private fun fireLeakedClosableObjects(context: Context) {
        LeakedClosableObjectsActivity.start(context)
    }

    //FIXME not working
    private fun fireActivityLeaks() {}

    //FIXME not working
    private fun fireLeakedRegistrationObjects(context: Context) {
        val i = Intent("LeakAction")
        context.sendBroadcast(i)
    }

    //FIXME not working
    private fun fireLeakedSqlLiteObjects(context: Context) {
        val helper = LeakedSQLiteOpenHelper(context.applicationContext)
        val db = helper.writableDatabase

        val values = ContentValues()
        values.put("data", "data")
        db.insert("sample", null, values)

        val cursor = db.query("sample", arrayOf("_id", "data"), null, null, null, null, "_id DESC")
        cursor.moveToNext()
        cursor.close()
        //db.close();
    }

    private fun doNothing() {
        // Do nothing
    }
}
