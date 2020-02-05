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

package com.virgilsecurity.android.feature_contacts.viewslice.addContact.state

import android.text.InputFilter
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import com.virgilsecurity.android.base.viewslice.BaseViewSlice
import com.virgilsecurity.android.feature_contacts.R
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
 * StateSliceAddContactDefault
 */
class StateSliceAddContact(
        private val symbolsInputFilter: InputFilter
) : BaseViewSlice() {

    private val debounceSubject: PublishSubject<Boolean> = PublishSubject.create()
    private lateinit var debounceDisposable: Disposable

    private lateinit var btnAdd: Button
    private lateinit var pbLoading: ProgressBar
    private lateinit var etUsername: EditText
    private lateinit var tvYourUsername: TextView
    private lateinit var tvTryAgain: TextView

    override fun setupViews() {
        with(window) {
            this@StateSliceAddContact.btnAdd = findViewById(R.id.btnAdd)
            this@StateSliceAddContact.pbLoading = findViewById(R.id.pbLoading)
            this@StateSliceAddContact.etUsername = findViewById(R.id.etUsername)
            this@StateSliceAddContact.tvYourUsername = findViewById(R.id.tvYourUsername)
            this@StateSliceAddContact.tvTryAgain = findViewById(R.id.tvTryAgain)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        debounceDisposable = debounceSubject.debounce(DEBOUNCE_INTERVAL, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it) btnAdd.visibility = View.INVISIBLE else btnAdd.visibility = View.VISIBLE
                    if (it) pbLoading.visibility = View.VISIBLE else pbLoading.visibility = View.INVISIBLE
                }

        etUsername.filters = arrayOf(symbolsInputFilter)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        debounceDisposable.dispose()
    }

    fun showConsistent() {
        activateButton(true)
        loading(false)
        tvYourUsername.visibility = View.INVISIBLE
    }

    fun showUsernameEmpty() {
        activateButton(false)
        loading(false)
        tvYourUsername.visibility = View.INVISIBLE
    }

    fun showUsernameTooShort() {
        activateButton(false)
        loading(false)
        tvYourUsername.visibility = View.INVISIBLE
    }

    fun showUsernameTooLong() {
        activateButton(false)
        loading(false)
        tvYourUsername.visibility = View.INVISIBLE
    }

    fun showYourOwnUsername() {
        activateButton(false)
        loading(false)
        tvYourUsername.visibility = View.VISIBLE
    }

    fun showLoading() = loading(true)

    fun showTryAgain() {
        loading(false)
        tvYourUsername.visibility = View.INVISIBLE

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
            btnAdd.isEnabled = true
            btnAdd.isClickable = true
            btnAdd.background = context.getDrawable(R.drawable.rect_rounded_accent_2)
        } else {
            btnAdd.isEnabled = false
            btnAdd.isClickable = false
            btnAdd.background = context.getDrawable(R.drawable.rect_rounded_gray_2)
        }
    }

    companion object {
        const val ERROR_FADE_OUT_DURATION = 500L
        const val ERROR_START_OFFSET = 1500L
        const val DEBOUNCE_INTERVAL = 200L
    }
}
