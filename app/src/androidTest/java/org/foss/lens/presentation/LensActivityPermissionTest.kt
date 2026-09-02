package org.foss.lens.presentation

import android.Manifest
import android.content.Context
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.foss.lens.R
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LensActivityPermissionTest {

    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val context: Context = instrumentation.targetContext
    private val packageName: String = context.packageName

    @Before
    fun revokeCameraPermission() {
        instrumentation.uiAutomation.executeShellCommand(
            "pm revoke $packageName android.permission.CAMERA"
        )
    }

    @Test
    fun permissionDeniedShowsError() {
        ActivityScenario.launch(LensActivity::class.java).use {
            onView(withId(R.styleable.AppCompatTheme_windowActionBar)) // Ajusta según tu vista de error
        }
    }
}
