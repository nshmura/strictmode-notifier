package io.github.juanimoli.strictmode.notifier

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import io.github.juanimoli.strictmode.notifier.commons.StrictModeViolation

internal class ReportAdapter(private val reportActivity: StrictModeReportActivity) : BaseAdapter() {
    private val reports: MutableList<StrictModeViolation> = ArrayList()

    override fun getCount(): Int {
        return reports.size
    }

    override fun getItem(position: Int): StrictModeViolation {
        return reports[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        var convertView = view

        if (convertView == null) {
            convertView = LayoutInflater.from(reportActivity)
                .inflate(R.layout.strictmode_notifier_row, parent, false)
            convertView.tag = ViewHolder(convertView)
        }

        val report = getItem(position)
        val holder = convertView!!.tag as ViewHolder

        holder.numberText.text =
            parent.context.getString(R.string.strictmode_notifier_count, count - position)
        holder.dateText.text = report.getDateText(reportActivity)
        if (report.violationType != null) {
            holder.violationTypeText.text =
                ViolationTypeInfo.convert(report.violationType).violationName()
        } else {
            holder.violationTypeText.text = report.message
        }

        return convertView
    }

    fun addAll(reports: List<StrictModeViolation>?) {
        this.reports.addAll(reports!!)
    }

    fun clear() {
        reports.clear()
    }

    private inner class ViewHolder(convertView: View) {
        val numberText: TextView = convertView.findViewById<View>(R.id.__number) as TextView
        val violationTypeText: TextView =
            convertView.findViewById<View>(R.id.__violation_type) as TextView
        val dateText: TextView = convertView.findViewById<View>(R.id.__date) as TextView
    }
}
