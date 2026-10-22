package com.example.ui.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import java.util.Calendar

object DateTimePickerUtil {

    fun showDateTimePicker(
        context: Context,
        initialEpochMs: Long,
        onDateTimeSelected: (Long) -> Unit
    ) {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = initialEpochMs
        }

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(context, { _, selectedYear, selectedMonth, selectedDay ->
            calendar.set(Calendar.YEAR, selectedYear)
            calendar.set(Calendar.MONTH, selectedMonth)
            calendar.set(Calendar.DAY_OF_MONTH, selectedDay)

            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            TimePickerDialog(context, { _, selectedHour, selectedMinute ->
                calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                calendar.set(Calendar.MINUTE, selectedMinute)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)

                onDateTimeSelected(calendar.timeInMillis)
            }, hour, minute, false).show()

        }, year, month, day).apply {
            datePicker.minDate = System.currentTimeMillis()
        }.show()
    }
}
