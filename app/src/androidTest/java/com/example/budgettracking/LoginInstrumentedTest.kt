package com.example.budgettracking

import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.budgettracking.ui.activity.LogInActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class CalculationInstrumentedTest {
    @get:Rule
    val testRule = ActivityScenarioRule(LogInActivity::class.java)

    @Test
    fun checkSum() {

        onView(withId(R.id.mail_input)).perform(
            typeText("dhakalabhinav4@gmail.com")
        )

        onView(withId(R.id.passwordField)).perform(
            typeText("abh123")
        )

        closeSoftKeyboard()
        Thread.sleep(1500)

        onView(withId(R.id.login_btn)).perform(
            click()
        )

        Thread.sleep(4000)

        onView(withId(R.id.testResult)).check(matches(withText("Login success")))
    }
}