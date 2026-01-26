package com.example.bilgideham

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

/**
 * Animation Utilities for Performance Optimization
 * 
 * Provides:
 * 1. Low-end device detection
 * 2. Reduced animations preference
 * 3. Lifecycle-aware animation control
 * 4. Optimized infinite transition helpers
 */

/**
 * Detects if the device is a low-end device based on RAM and performance class
 */
fun isLowEndDevice(context: Context): Boolean {
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
    val memoryInfo = ActivityManager.MemoryInfo()
    activityManager?.getMemoryInfo(memoryInfo)
    
    // Device is low-end if RAM is less than 3GB
    val totalRamMB = memoryInfo.totalMem / (1024 * 1024)
    val isLowRam = totalRamMB < 3000
    
    // Also check if system reports it as low RAM device
    val isLowRamDevice = activityManager?.isLowRamDevice == true
    
    return isLowRam || isLowRamDevice
}

/**
 * CompositionLocal for animation reduction state
 */
val LocalReduceAnimations = compositionLocalOf { false }

/**
 * Returns whether animations should be reduced based on user preference or device capability
 */
@Composable
fun shouldReduceAnimations(): Boolean {
    val context = LocalContext.current
    val userPreference = remember { AppPrefs.getReduceAnimations(context) }
    val autoDetect = remember { AppPrefs.getAutoDetectLowEnd(context) }
    val isLowEnd = remember { isLowEndDevice(context) }
    
    return userPreference || (autoDetect && isLowEnd)
}

/**
 * Lifecycle-aware infinite transition that stops when app is in background
 */
@Composable
fun rememberLifecycleAwareInfiniteTransition(label: String): InfiniteTransition? {
    val lifecycleOwner = LocalLifecycleOwner.current
    var isActive by remember { mutableStateOf(true) }
    
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            isActive = when (event) {
                Lifecycle.Event.ON_RESUME -> true
                Lifecycle.Event.ON_PAUSE -> false
                else -> isActive
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    
    return if (isActive) rememberInfiniteTransition(label) else null
}

/**
 * Optimized infinite float animation that respects reduce animations preference
 * Returns a static value if animations are reduced
 */
@Composable
fun rememberOptimizedAnimatedFloat(
    initialValue: Float,
    targetValue: Float,
    animationSpec: InfiniteRepeatableSpec<Float>,
    label: String
): State<Float> {
    val reduceAnimations = shouldReduceAnimations()
    
    if (reduceAnimations) {
        // Return the average value as static
        val staticValue = (initialValue + targetValue) / 2f
        return remember { mutableStateOf(staticValue) }
    }
    
    val infiniteTransition = rememberInfiniteTransition(label = "$label-transition")
    return infiniteTransition.animateFloat(
        initialValue = initialValue,
        targetValue = targetValue,
        animationSpec = animationSpec,
        label = label
    )
}
/**
 * Consolidated animation state for multiple related animations
 * Reduces the number of InfiniteTransition objects
 */
@Composable
fun rememberConsolidatedAnimations(): ConsolidatedAnimationState {
    val reduceAnimations = shouldReduceAnimations()
    
    if (reduceAnimations) {
        return remember { ConsolidatedAnimationState.Static }
    }
    
    val infiniteTransition = rememberInfiniteTransition(label = "consolidated")
    
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
        label = "pulse"
    )
    
    val glow by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(tween(1500), RepeatMode.Reverse),
        label = "glow"
    )
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(20000, easing = LinearEasing), RepeatMode.Restart),
        label = "rotation"
    )
    
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(15000, easing = LinearEasing), RepeatMode.Restart),
        label = "phase"
    )
    
    return ConsolidatedAnimationState(
        pulse = pulse,
        glow = glow,
        rotation = rotation,
        phase = phase,
        isAnimating = true
    )
}

/**
 * Data class holding consolidated animation values
 */
data class ConsolidatedAnimationState(
    val pulse: Float,
    val glow: Float,
    val rotation: Float,
    val phase: Float,
    val isAnimating: Boolean
) {
    companion object {
        val Static = ConsolidatedAnimationState(
            pulse = 1f,
            glow = 0.5f,
            rotation = 0f,
            phase = 0f,
            isAnimating = false
        )
    }
}
