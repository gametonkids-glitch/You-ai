package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.ai.ShortsAiEnhancer
import com.example.util.TimeFormatUtil
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("ShortsFlow", appName)
    }

    @Test
    fun `test AI plan generation`() {
        val plan = ShortsAiEnhancer.generatePlan("Coding Tips", "tech")
        assertNotNull(plan)
        assertTrue(plan.title.contains("#Shorts"))
        assertTrue(plan.tags.isNotEmpty())
    }

    @Test
    fun `test YouTube publishAt format`() {
        val epochMs = 1792652122000L
        val formatted = TimeFormatUtil.formatYoutubePublishAt(epochMs)
        assertTrue(formatted.endsWith("Z"))
    }
}
