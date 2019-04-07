/*
 * Copyright 2017 Rozdoum
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.rozdoum.socialcomponents.utils

import android.content.Context
import android.content.res.Resources
import android.text.format.DateUtils

import com.rozdoum.socialcomponents.R

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Formatter
import java.util.Locale
import java.util.TimeZone

/**
 * Created by Kristina on 10/17/16.
 */

object FormatterUtil {

    var firebaseDBDate = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
    var firebaseDBDay = "yyyy-MM-dd"
    val NOW_TIME_RANGE = DateUtils.MINUTE_IN_MILLIS * 5 // 5 minutes

    var dateTime = "yyyy-MM-dd HH:mm:ss"

    val firebaseDateFormat: SimpleDateFormat
        get() {
            val cbDateFormat = SimpleDateFormat(firebaseDBDate)
            cbDateFormat.timeZone = TimeZone.getTimeZone("UTC")
            return cbDateFormat
        }

    fun formatFirebaseDay(date: Date): String {
        val cbDateFormat = SimpleDateFormat(firebaseDBDay)
        cbDateFormat.timeZone = TimeZone.getTimeZone("UTC")
        return cbDateFormat.format(date)
    }

    fun formatDateTime(date: Date): String {
        val cbDateFormat = SimpleDateFormat(dateTime)
        cbDateFormat.timeZone = TimeZone.getTimeZone("UTC")
        return cbDateFormat.format(date)
    }

    fun getRelativeTimeSpanString(context: Context, time: Long): CharSequence {
        val now = System.currentTimeMillis()
        val range = Math.abs(now - time)

        return if (range < NOW_TIME_RANGE) {
            context.getString(R.string.now_time_range)
        } else DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS)

    }

    fun getRelativeTimeSpanStringShort(context: Context, time: Long): CharSequence {
        val now = System.currentTimeMillis()
        val range = Math.abs(now - time)
        return formatDuration(context, range, time)
    }

    private fun formatDuration(context: Context, range: Long, time: Long): CharSequence {
        val res = context.resources
        if (range >= DateUtils.WEEK_IN_MILLIS + DateUtils.DAY_IN_MILLIS) {
            return shortFormatEventDay(context, time)
        } else if (range >= DateUtils.WEEK_IN_MILLIS) {
            val days = ((range + DateUtils.WEEK_IN_MILLIS / 2) / DateUtils.WEEK_IN_MILLIS).toInt()
            return String.format(res.getString(R.string.duration_week_shortest), days)
        } else if (range >= DateUtils.DAY_IN_MILLIS) {
            val days = ((range + DateUtils.DAY_IN_MILLIS / 2) / DateUtils.DAY_IN_MILLIS).toInt()
            return String.format(res.getString(R.string.duration_days_shortest), days)
        } else if (range >= DateUtils.HOUR_IN_MILLIS) {
            val hours = ((range + DateUtils.HOUR_IN_MILLIS / 2) / DateUtils.HOUR_IN_MILLIS).toInt()
            return String.format(res.getString(R.string.duration_hours_shortest), hours)
        } else if (range >= NOW_TIME_RANGE) {
            val minutes = ((range + DateUtils.MINUTE_IN_MILLIS / 2) / DateUtils.MINUTE_IN_MILLIS).toInt()
            return String.format(res.getString(R.string.duration_minutes_shortest), minutes)
        } else {
            return res.getString(R.string.now_time_range)
        }
    }

    private fun shortFormatEventDay(context: Context, time: Long): String {
        val flags = DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_ABBREV_MONTH
        val f = Formatter(StringBuilder(50), Locale.getDefault())
        return DateUtils.formatDateRange(context, f, time, time, flags).toString()
    }
}
