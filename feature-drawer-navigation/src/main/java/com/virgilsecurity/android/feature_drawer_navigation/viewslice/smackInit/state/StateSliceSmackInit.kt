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

package com.virgilsecurity.android.feature_drawer_navigation.viewslice.smackInit.state

import android.animation.ValueAnimator
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import com.sdsmdg.harjot.vectormaster.VectorMasterView
import com.virgilsecurity.android.base.viewslice.BaseViewSlice
import com.virgilsecurity.android.common.util.UiUtils
import com.virgilsecurity.android.feature_drawer_navigation.R
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
 * ....|  _/    7/26/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

/**
 * StateSliceSmackInit
 */
class StateSliceSmackInit : BaseViewSlice() {

    private val debounceSubject: PublishSubject<Int> = PublishSubject.create()
    private var loadingFader: ValueAnimator? = null
    private lateinit var debounceDisposable: Disposable

    private lateinit var btnRetry: TextView
    private lateinit var clContentRoot: ConstraintLayout
    private lateinit var ivLoading: VectorMasterView
    private lateinit var tvInitializing: TextView

    override fun setupViews() {
        with(window) {
            this@StateSliceSmackInit.btnRetry = findViewById(R.id.btnRetry)
            this@StateSliceSmackInit.clContentRoot = findViewById(R.id.clContentRoot)
            this@StateSliceSmackInit.ivLoading = findViewById(R.id.ivLoading)
            this@StateSliceSmackInit.tvInitializing = findViewById(R.id.tvInitializing)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        debounceDisposable = debounceSubject.debounce(DEBOUNCE_INTERVAL, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    show(it)
                }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        debounceDisposable.dispose()
    }

    fun showLoading() = debounceSubject.onNext(LOADING)

    fun showContent() = debounceSubject.onNext(CONTENT)

    fun showError() {
        debounceSubject.onNext(ERROR)
    }

    private fun show(state: Int) {
        when (state) {
            CONTENT, ERROR -> { // For now our content is showing that something happened
                btnRetry.isClickable = true
                btnRetry.isEnabled = true
                clContentRoot.visibility = View.VISIBLE
                ivLoading.visibility = View.INVISIBLE
                if (loadingFader != null)
                    loadingFader!!.end()
                tvInitializing.visibility = View.INVISIBLE
            }
            LOADING -> {
                btnRetry.isClickable = false
                btnRetry.isEnabled = false
                clContentRoot.visibility = View.INVISIBLE
                ivLoading.visibility = View.VISIBLE
                loadingFader = UiUtils.fadeVectorReverse(ivLoading)
                tvInitializing.visibility = View.VISIBLE
            }
        }
    }

    companion object {
        const val CONTENT = 0
        const val LOADING = 1
        const val ERROR = 2

        const val DEBOUNCE_INTERVAL = 200L
    }
}
