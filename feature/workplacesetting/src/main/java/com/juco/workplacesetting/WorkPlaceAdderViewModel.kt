package com.juco.workplacesetting

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juco.common.Red
import com.juco.domain.repository.WorkPlaceRepository
import com.juco.workplacesetting.mapper.toDomain
import com.juco.workplacesetting.model.UiPayDay
import com.juco.workplacesetting.model.UiPayDayType
import com.juco.workplacesetting.model.UiWorkTime
import com.juco.workplacesetting.model.WorkDayType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class WorkPlaceViewModel @Inject constructor(
    private val repository: WorkPlaceRepository
) : ViewModel() {

    var workPlaceName = MutableStateFlow("")
    var wage = MutableStateFlow("")
    var selectedWorkDayType = MutableStateFlow<WorkDayType?>(null)
    var selectedWorkDays = MutableStateFlow<List<LocalDate>>(emptyList())
    var selectedPayDay = MutableStateFlow(
        UiPayDay(
            type = UiPayDayType.MONTHLY,
            value = "1일"
        )
    )
    var workTime = MutableStateFlow(
        UiWorkTime(
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(18, 0)
        )
    )
    var breakTime = MutableStateFlow("60")
    var selectedWorkPlaceCardColor = MutableStateFlow(Red)
    var isWeeklyHolidayAllowance = MutableStateFlow(false)

    fun setWorkDays(dayOfWeeks: Set<DayOfWeek>) {
        val today = LocalDate.now()
        val days = generateSequence(today) { it.plusDays(1) }
            .take(365)
            .filter { it.dayOfWeek in dayOfWeeks }
            .toList()

        selectedWorkDays.value = days
    }

    fun setCustomWorkDays(dates: List<LocalDate>) {
        selectedWorkDayType.value = WorkDayType.CUSTOM
        selectedWorkDays.value = dates
    }

    fun setPayDay(payDay: UiPayDay) {
        selectedPayDay.value = payDay
    }

    fun setWorkTime(time: UiWorkTime) {
        workTime.value = time
    }

    fun setBreakTime(time: String) {
        breakTime.value = time
    }

    fun setWorkPlaceCardColor(color: Color) {
        selectedWorkPlaceCardColor.value = color
    }

    fun setWeeklyHolidayAllowanceEnabled(enabled: Boolean) {
        isWeeklyHolidayAllowance.value = enabled
    }

    fun saveWorkPlace() {
        val name = workPlaceName.value.trim()
        val wageValue = wage.value.toLongOrNull() ?: return
        val workDays = selectedWorkDays.value
        val payDay = selectedPayDay.value.toDomain()
        val workTime = workTime.value.toDomain()
        val breakTimeValue = breakTime.value.toIntOrNull() ?: return
        val workCardColor = selectedWorkPlaceCardColor.value.toArgb()
        val isWeeklyHolidayAllowance = isWeeklyHolidayAllowance.value

        viewModelScope.launch {
            repository.saveWorkPlace(
                name = name,
                wage = wageValue,
                workDays = workDays,
                payDay = payDay,
                workTime = workTime,
                breakTime = breakTimeValue,
                workPlaceCardColor = workCardColor,
                isWeeklyHolidayAllowance = isWeeklyHolidayAllowance
            )
            workPlaceName.value = ""
            wage.value = ""
            selectedWorkDays.value = emptyList()
            selectedWorkDayType.value = null
            selectedPayDay.value = UiPayDay(UiPayDayType.MONTHLY, "1일")
            selectedWorkPlaceCardColor.value = Red
            breakTime.value = "60"
        }
    }
}