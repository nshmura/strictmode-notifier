package io.github.juanimoli.strictmode.notifier

import android.app.Activity

/**
 * @author nishimura_shinichi
 */
internal object ReportActivityUtils {
    fun setTitle(activity: Activity, title: String?) {
        val actionBar = activity.actionBar
        actionBar!!.title = title
    }

    fun setDisplayHomeAsUpEnabled(activity: Activity, enabled: Boolean) {
        val actionBar = activity.actionBar
        actionBar!!.setDisplayHomeAsUpEnabled(enabled)
    }
}
