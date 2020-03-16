/*
 * Copyright (c) 2015-2018, Virgil Security, Inc.
 *
 * Lead Maintainer: Virgil Security Inc. <support@virgilsecurity.com>
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     (1) Redistributions of source code must retain the above copyright notice, this
 *     list of conditions and the following disclaimer.
 *
 *     (2) Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 *     (3) Neither the name of virgil nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.virgilsecurity.android.bcommon.util

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.IdRes
import android.util.DisplayMetrics
import android.util.Log
import android.widget.Toast
import com.bluelinelabs.conductor.Controller
import com.sdsmdg.harjot.vectormaster.VectorMasterView
import com.virgilsecurity.android.bcommon.BuildConfig
import java.util.*

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    5/31/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

/**
 * UiUtils
 */
object UiUtils {

    fun toast(context: Context, text: String) =
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()

    fun toast(context: Context, stringResId: Int) =
            Toast.makeText(context,
                           context.getString(stringResId),
                           Toast.LENGTH_SHORT).show()

    fun toast(controller: Controller, text: String) =
            Toast.makeText(controller.activity, text, Toast.LENGTH_SHORT).show()

    fun toast(controller: Controller, stringResId: Int) =
            Toast.makeText(controller.activity,
                           controller.activity!!.getString(stringResId),
                           Toast.LENGTH_SHORT).show()

    fun toastUnderDevelopment(controller: Controller) = toast(controller, "Under development")

    fun toastUnderDevelopment(context: Context) = toast(context, "Under development")

    fun log(tag: String, text: String) {
        if (BuildConfig.DEBUG)
            Log.d(tag, text)
    }

    /**
     * This method converts density independent pixels (dp) to equivalent pixels,
     * depending on device density.
     * Where [dp] is a dp value which we need to convert into pixels.
     */
    fun dpToPixels(dp: Float, context: Context): Int =
            context.resources.displayMetrics.let {
                dp * (it.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
            }.toInt()

    /**
     * This method converts device specific pixels (dp) to density independent pixels.
     * Where [px] is a value in pixels, which we need to convert into dp.
     */
    fun pixelsToDp(px: Float, context: Context): Int =
            context.resources.displayMetrics.let {
                px / (it.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
            }.toInt()

    /**
     * This method converts density independent pixels (dp) to equivalent pixels,
     * depending on device density.
     * Where [dp] is a dp value which we need to convert into pixels.
     */
    fun dpToPixels(dp: Int, context: Context): Int =
            context.resources.displayMetrics.let {
                dp * (it.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
            }.toInt()

    /**
     * This method converts device specific pixels (dp) to density independent pixels.
     * Where [px] is a value in pixels, which we need to convert into dp.
     */
    fun pixelsToDp(px: Int, context: Context): Int =
            context.resources.displayMetrics.let {
                px / (it.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
            }.toInt()

    fun randomDrawable(context: Context, drawablesArrayId: Int): Drawable =
            context.resources.obtainTypedArray(drawablesArrayId).let { backgrounds ->
                Random().nextInt(backgrounds.length()).let {
                    val result = backgrounds.getDrawable(it)
                    backgrounds.recycle()
                    result
                }
            }

    @SuppressLint("ResourceType")
    fun letterBasedDrawable(context: Context, drawablesArrayId: Int, letter: String): Drawable =
            context.resources.obtainTypedArray(drawablesArrayId).let { backgrounds ->
                val result = when (letter) {
                    in "a".."h" -> backgrounds.getDrawable(0)!!
                    in "i".."q" -> backgrounds.getDrawable(1)!!
                    in "r".."z" -> backgrounds.getDrawable(2)!!
                    else -> backgrounds.getDrawable(0)!!
                }

                backgrounds.recycle()

                result
            }

    fun fadeVectorReverse(vectorView: VectorMasterView,
                          pathToAnimate: String = "outline",
                          startValue: Float = 1.0f,
                          endValue: Float = 0.0f,
                          duration: Long = 900L,
                          @IdRes fillColor: Int? = null): ValueAnimator {
        val outline = vectorView.getPathModelByName(pathToAnimate)
        if (fillColor != null)
            outline.fillColor = fillColor

        val valueAnimatorIn = ValueAnimator.ofFloat(startValue, endValue)

        valueAnimatorIn.duration = duration

        valueAnimatorIn.addUpdateListener { animator ->
            outline.fillAlpha = animator.animatedValue as Float
            vectorView.update()
        }

        valueAnimatorIn.repeatMode = ValueAnimator.REVERSE
        valueAnimatorIn.repeatCount = ValueAnimator.INFINITE
        valueAnimatorIn.start()

        return valueAnimatorIn
    }
}