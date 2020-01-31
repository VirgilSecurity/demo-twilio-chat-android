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

package com.virgilsecurity.android.feature_login.viewslice.registration.state

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import android.text.InputFilter
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import com.virgilsecurity.android.base.viewslice.BaseViewSlice
import com.virgilsecurity.android.feature_login.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    7/10/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

/**
 * StateSliceRegistrationDefault
 */
class StateSliceRegistrationDefault(
        private val symbolsInputFilter: InputFilter
) : BaseViewSlice(), StateSliceRegistration {

    private val debounceSubject: PublishSubject<Boolean> = PublishSubject.create()
    private lateinit var debounceDisposable: Disposable

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        debounceDisposable = debounceSubject.debounce(DEBOUNCE_INTERVAL, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it) btnNext.visibility = View.INVISIBLE else btnNext.visibility = View.VISIBLE
                    if (it) pbLoading.visibility = View.VISIBLE else pbLoading.visibility = View.INVISIBLE
                }

        etUsername.filters = arrayOf(symbolsInputFilter)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        debounceDisposable.dispose()
    }

    override fun showConsistent() {
        activateButton(true)
        loading(false)
    }

    override fun showUsernameEmpty() {
        activateButton(false)
        loading(false)
    }

    override fun showUsernameTooShort() {
        activateButton(false)
        loading(false)
    }

    override fun showUsernameTooLong() {
        activateButton(false)
        loading(false)
    }

    override fun showLoading() = loading(true)

    override fun showError() {
        loading(false)
        if (tvTryAgain.visibility == View.INVISIBLE) {
            tvTryAgain.visibility = View.VISIBLE
            val alphaAnimation = AlphaAnimation(tvTryAgain.alpha, 0.0f)
            alphaAnimation.duration = ERROR_FADE_OUT_DURATION
            alphaAnimation.startOffset = ERROR_START_OFFSET
            alphaAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {}

                override fun onAnimationEnd(animation: Animation?) {
                    tvTryAgain.visibility = View.INVISIBLE
                }

                override fun onAnimationStart(animation: Animation?) {}
            })
            tvTryAgain.startAnimation(alphaAnimation)
        }
    }

    private fun loading(loading: Boolean) {
        debounceSubject.onNext(loading)
    }

    private fun activateButton(active: Boolean) {
        if (active) {
            btnNext.isEnabled = true
            btnNext.isClickable = true
            btnNext.background = context.getDrawable(R.drawable.rect_rounded_accent_2)
        } else {
            btnNext.isEnabled = false
            btnNext.isClickable = false
            btnNext.background = context.getDrawable(R.drawable.rect_rounded_gray_2)
        }
    }

    companion object {
        const val ERROR_FADE_OUT_DURATION = 500L
        const val ERROR_START_OFFSET = 1500L
        const val DEBOUNCE_INTERVAL = 200L
    }
}
