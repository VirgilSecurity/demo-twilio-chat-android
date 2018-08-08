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

package com.android.virgilsecurity.base.view

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import android.content.Context
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.android.virgilsecurity.base.util.KoinContextName
import com.bluelinelabs.conductor.Controller
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.*
import org.koin.standalone.KoinComponent
import org.koin.standalone.releaseContext

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
abstract class BaseController : Controller(), LayoutContainer, LifecycleOwner, KoinComponent {

    protected val lifecycleRegistry: LifecycleRegistry by lazy { LifecycleRegistry(this) }

    @get:LayoutRes
    protected abstract val layoutResourceId: Int
    @get:KoinContextName
    protected abstract val koinContextName: String?

    /**
     * Used to initialize general options
     */
    protected abstract fun init()

    /**
     * Used to initialize view slices *Before*
     * the [android.arch.lifecycle.Lifecycle.Event.ON_RESUME] event happened
     */
    protected abstract fun initViewSlices(view: View)

    /**
     * Used to setup view slices *After*
     * the [android.arch.lifecycle.Lifecycle.Event.ON_RESUME] event happened
     */
    protected abstract fun setupViewSlices(view: View)

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

    override lateinit var containerView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        containerView = inflater.inflate(layoutResourceId, container, false)

        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)

        init()
        initViewSlices(containerView)

        lifecycleRegistry.markState(Lifecycle.State.CREATED)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)

        setupViewSlices(containerView)
        setupVSActionObservers()
        setupVMStateObservers()
        initData()

        return containerView
    }

    override fun onAttach(view: View) {
        super.onAttach(view)

        lifecycleRegistry.markState(Lifecycle.State.STARTED)

        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        lifecycleRegistry.markState(Lifecycle.State.RESUMED)
    }

    override fun onDetach(view: View) {
        super.onDetach(view)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)

        lifecycleRegistry.markState(Lifecycle.State.STARTED)
    }

    override fun onDestroyView(view: View) {
        super.onDestroyView(view)

        clearFindViewByIdCache()

        if (koinContextName != null)
            releaseContext(koinContextName!!)

        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        lifecycleRegistry.markState(Lifecycle.State.CREATED)
    }

    override fun onDestroy() {
        super.onDestroy()

        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        lifecycleRegistry.markState(Lifecycle.State.DESTROYED)
    }

    override fun getLifecycle(): Lifecycle = lifecycleRegistry

    protected fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)
    }
}