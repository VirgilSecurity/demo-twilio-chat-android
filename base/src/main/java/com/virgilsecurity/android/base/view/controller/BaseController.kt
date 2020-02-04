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

package com.virgilsecurity.android.base.view.controller

import android.content.Context
import androidx.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import com.bluelinelabs.conductor.archlifecycle.LifecycleController
import org.koin.core.KoinComponent

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    7/16/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

/**
 * BaseController
 */
abstract class BaseController : LifecycleController(), KoinComponent {

    @get:LayoutRes
    protected abstract val layoutResourceId: Int

    /**
     * Used to initialize general options
     */
    protected abstract fun init(containerView: View)

    /**
     * Used to initialize view slices *Before*
     * the [android.arch.lifecycle.Lifecycle.Event.ON_RESUME] event happened
     */
    protected abstract fun initViewSlices(window: Window)

    /**
     * Used to setup view slices *After*
     * the [android.arch.lifecycle.Lifecycle.Event.ON_RESUME] event happened
     */
    protected abstract fun setupViewSlices(containerView: View)

    /**
     * Used to setup view slices action observers *After*
     * the [android.arch.lifecycle.Lifecycle.Event.ON_RESUME] event happened
     */
    protected abstract fun setupVSActionObservers()

    /**
     * Used to setup view model state observers *After*
     * the [android.arch.lifecycle.Lifecycle.Event.ON_RESUME] event happened
     */
    protected abstract fun setupVMStateObservers()

    /**
     * Used to request data *After*
     * the [android.arch.lifecycle.Lifecycle.Event.ON_RESUME] event happened
     * and all View Slices and ViewModels are set up.
     */
    protected abstract fun initData()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(layoutResourceId, container, false)
    }

    override fun onAttach(containerView: View) {
        super.onAttach(containerView)

        init(containerView)
        initViewSlices(activity!!.window)

        // At this point we should have activity initialized already
        setupViewSlices(containerView)
        setupVSActionObservers()
        setupVMStateObservers()
        initData()
    }

    protected fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)
    }

    /**
     * Posts 'open keyboard' event to the queue to wait until all views are drawn.
     */
    protected fun openKeyboard(view: View) {
        if (view.requestFocus()) {
            view.post {
                val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(view, InputMethodManager.SHOW_FORCED)
            }
        }
    }
}
