package com.blhealthcare.md5checker

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class AppAdapter(private val context: Context) : BaseAdapter() {
    private var apps: ArrayList<AppInfo> = ArrayList()

    fun add(info: AppInfo) {
        apps.add(info)
//        notifyDataSetChanged()
    }

    fun clear() {
        apps.clear()
        notifyDataSetChanged()
    }

    override fun notifyDataSetChanged() {
        if (apps.isNotEmpty()) {
            apps = apps.sortedWith(compareBy { it.appName }).toMutableList() as ArrayList<AppInfo>
        }
        super.notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return apps.size
    }

    override fun getItem(position: Int): Any {
        return apps[position]
    }

    override fun getItemId(position: Int): Long {
        return apps[position].hashCode().toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder
        if (convertView == null) {
            view = View.inflate(context, R.layout.app_item, null)
            holder = ViewHolder()
            holder.appName = view.findViewById(R.id.appName)
            holder.packageName = view.findViewById(R.id.packageName)
            holder.md5 = view.findViewById(R.id.md5)
            view.tag = holder
        } else {
            view = convertView
            holder = convertView.tag as ViewHolder
        }
        val appName: TextView = holder.appName
        val packageName: TextView = holder.packageName
        val md5: TextView = holder.md5
        val appInfo = getItem(position) as AppInfo

        appName.text = appInfo.getAppName()
        packageName.text = appInfo.packageName
        md5.text = appInfo.md5

        return view
    }

    private fun AppInfo.getAppName(): String {
        return if (appName.length > 25) {
            "**"
        } else
            appName
    }

    private class ViewHolder {
        lateinit var appName: TextView
        lateinit var packageName: TextView
        lateinit var md5: TextView
    }
}