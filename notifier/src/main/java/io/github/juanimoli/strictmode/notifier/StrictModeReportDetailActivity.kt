package io.github.juanimoli.strictmode.notifier

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import io.github.juanimoli.strictmode.notifier.commons.StrictModeViolation

class StrictModeReportDetailActivity : Activity() {
    private var violationStore: ViolationStore? = null
    private var report: StrictModeViolation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.strictmode_notifier_activity_report_detail)

        violationStore = ViolationStore(this@StrictModeReportDetailActivity)
        report = intent.getSerializableExtra(EXTRA_REPORT) as StrictModeViolation?

        val stackTrace = findViewById<TextView>(R.id.__stacktrace_text)
        val stacktraceText = report?.stacktraceText
        val span: Spannable = SpannableString(stacktraceText)
        setColor(span, stacktraceText, packageName, Color.WHITE)
        stackTrace.text = span

        if (report?.violationType != null) {
            ReportActivityUtils.setTitle(
                this,
                ViolationTypeInfo.convert(report!!.violationType).violationName()
            )
            ReportActivityUtils.setDisplayHomeAsUpEnabled(this, true)
        }

        findViewById<View>(R.id.__delete_button).setOnClickListener {
            violationStore!!.remove(report)
            finish()
        }
    }

    override fun onMenuItemSelected(featureId: Int, item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }

        return super.onMenuItemSelected(featureId, item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.strictmode_notifier_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.__menu_copy -> {
                copy()
                true
            }
            R.id.__menu_share -> {
                share()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun copy() {
        val clip = ClipData.newPlainText("info", shareText())
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(clip)

        Toast.makeText(this, R.string.strictmode_notifier_copyped, Toast.LENGTH_LONG).show()
    }

    private fun share() {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareText())
        sendIntent.type = "text/plain"
        try {
            startActivity(
                Intent.createChooser(
                    sendIntent,
                    resources.getText(R.string.strictmode_notifier_menu_share)
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun shareText(): String? {
        var shareText: String?
        if (report!!.violationType != null) {
            shareText = ViolationTypeInfo.convert(report!!.violationType).violationName()
            shareText += """
                
                
                ${report?.stacktraceText}
                """.trimIndent()
        } else {
            shareText = report?.stacktraceText
        }
        return shareText
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun setColor(span: Spannable, stacktraceText: String?, highlight: String, color: Int) {
        val length = highlight.length
        var start = 0

        while (true) {
            val index = stacktraceText!!.indexOf(highlight, start)
            if (index < 0) {
                break
            }

            span.setSpan(
                StyleSpan(Typeface.BOLD), index, index + length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )

            span.setSpan(
                ForegroundColorSpan(color), index, index + length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )

            start = index + length
        }
    }

    companion object {
        private const val EXTRA_REPORT = "EXTRA_REPORT"

        fun start(context: Context, report: StrictModeViolation?) {
            context.startActivity(createIntent(context, report))
        }

        @Suppress("MemberVisibilityCanBePrivate")
        fun createIntent(context: Context?, report: StrictModeViolation?): Intent {
            val intent = Intent(context, StrictModeReportDetailActivity::class.java)
            intent.putExtra(EXTRA_REPORT, report)
            return intent
        }
    }
}
