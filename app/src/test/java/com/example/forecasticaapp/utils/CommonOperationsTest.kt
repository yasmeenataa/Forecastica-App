package com.example.forecasticaapp.utils

import org.hamcrest.core.Is
import org.junit.Assert.*

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class CommonOperationsTest {

    @Test
    fun getDayFormat_longOfTuesday_Tuesday() {
        //Given
        val dayLong: Long = 1680606000
        val language: String = "en"

        //when
        val result= getDayFormat(dayLong,language)

        //then
        assertThat(result, Is.`is`("Tuesday"))
    }

    @Test
    fun getTimeHourlyFormat_longOf10_houris10PM() {
        //Given
        val hourLong: Long = 1680638400
        val language: String = "en"

        //when
        val result= getTimeHourlyFormat(hourLong,language)

        //then
        assertThat(result, Is.`is`("10:00 PM"))
    }

    @Test
    fun convertDateToLong_dateValue_longODate() {
        //Given
        val date = "4 Apr, 2023"
        //when
        val result= convertDateToLong(date)

        //then
        assertThat(result, Is.`is`(1680559200000))
    }

    @Test
    fun convertTimeToLong_hour12PM_longValueOf12PM() {
        //Given
        val hour= "12:00 PM"

        //when
        val result= convertTimeToLong(hour)

        //then
        assertThat(result, Is.`is`(36000000))
    }
}