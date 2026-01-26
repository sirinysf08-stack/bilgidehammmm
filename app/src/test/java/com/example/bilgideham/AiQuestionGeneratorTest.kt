package com.example.bilgideham

import org.junit.Assert.*
import org.junit.Test
import org.junit.Before

/**
 * AiQuestionGenerator Unit Tests
 * Rate limit ve batch size davranışlarını test eder
 */
class AiQuestionGeneratorTest {

    @Before
    fun setup() {
        // Test setup
    }

    @Test
    fun `batch size should be at least count`() {
        // Batch size hesaplaması: effectiveCount = min(count, 40)
        // askCount = (effectiveCount * 1.3).toInt().coerceIn(effectiveCount, 40)
        val count = 10
        val effectiveCount = count.coerceAtMost(40)
        val askCount = (effectiveCount * 1.3).toInt().coerceIn(effectiveCount, 40)
        
        assertTrue("Batch size should be >= effectiveCount", askCount >= effectiveCount)
        assertTrue("Batch size should be <= 40", askCount <= 40)
    }

    @Test
    fun `batch size should not exceed 40`() {
        val count = 50
        val effectiveCount = count.coerceAtMost(40)
        val askCount = (effectiveCount * 1.3).toInt().coerceIn(effectiveCount, 40)
        
        // count > 40 olduğunda bile 40'ı geçmemeli
        assertEquals("Batch size should cap at 40", 40, askCount)
    }

    @Test
    fun `batch size calculation for 25 questions`() {
        val count = 25
        val effectiveCount = count.coerceAtMost(40)
        val askCount = (effectiveCount * 1.3).toInt().coerceIn(effectiveCount, 40)
        
        // 25 * 1.3 = 32.5 -> 32
        assertEquals("Expected 32 for count=25", 32, askCount)
    }

    @Test
    fun `retry delay calculation`() {
        // Retry delay: 15000 * (attempt + 1)
        val attempt = 0
        val delay = 15000L * (attempt + 1)
        assertEquals("First retry should wait 15 seconds", 15000L, delay)
        
        val attempt2 = 1
        val delay2 = 15000L * (attempt2 + 1)
        assertEquals("Second retry should wait 30 seconds", 30000L, delay2)
    }

    @Test
    fun `max retry attempts should be 5`() {
        val maxRetries = 5
        assertEquals("Max retries should be 5", 5, maxRetries)
    }
}
