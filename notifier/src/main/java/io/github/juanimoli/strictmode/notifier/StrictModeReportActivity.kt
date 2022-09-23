package io.github.juanimoli.strictmode.notifier

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.CompoundButton
import android.widget.ListView
import android.widget.ToggleButton
import io.github.juanimoli.strictmode.notifier.commons.StrictModeViolation

class StrictModeReportActivity : Activity() {
    private var adapter: ReportAdapter? = null
    private var violationStore: ViolationStore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.strictmode_notifier_activity_report)

        ReportActivityUtils.setTitle(
            this,
            getString(R.string.strictmode_notifier_title, packageName)
        )

        adapter = ReportAdapter(this)
        violationStore = ViolationStore(this)

        val listView = findViewById<ListView>(R.id.__list_view)
        listView.adapter = adapter
        listView.onItemClickListener =
            OnItemClickListener { _: AdapterView<*>?, _: View?, position: Int, _: Long ->
                val report = adapter!!.getItem(position)

                StrictModeReportDetailActivity.start(
                    this@StrictModeReportActivity,
                    report
                )
            }

        val toggleButton = findViewById<ToggleButton>(R.id.__enable_button)
        toggleButton.isChecked = StringModeConfig.from(this).isEnabled
        toggleButton.setOnCheckedChangeListener { _: CompoundButton?, _: Boolean ->
            StringModeConfig.from(
                this@StrictModeReportActivity
            ).toggle()
        }

        findViewById<View>(R.id.__delete_button).setOnClickListener {
            violationStore!!.clear()
            adapter!!.clear()
            adapter!!.notifyDataSetChanged()
        }

        if (savedInstanceState == null) {
            val report = intent.getSerializableExtra(EXTRA_REPORT) as StrictModeViolation?
            if (report != null) {
                StrictModeReportDetailActivity.start(this, report)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        val reports: List<StrictModeViolation>? = violationStore?.all

        adapter!!.clear()
        adapter!!.addAll(reports)
        adapter!!.notifyDataSetChanged()
    }

    companion object {
        private const val EXTRA_REPORT = "EXTRA_REPORT"

        @Suppress("MemberVisibilityCanBePrivate")
        fun createIntent(context: Context?, report: StrictModeViolation?): Intent {
            val intent = Intent(context, StrictModeReportActivity::class.java)
            intent.putExtra(EXTRA_REPORT, report)
            return intent
        }

        fun createPendingIntent(context: Context?, report: StrictModeViolation?): PendingIntent {
            val intent = createIntent(context, report)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.getActivity(
                    context,
                    1,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                )
            } else {
                PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            }
        }
    }
}
