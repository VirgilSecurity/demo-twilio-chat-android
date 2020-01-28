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

package com.virgilsecurity.android.base.view

import androidx.lifecycle.Lifecycle
import androidx.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.extensions.LayoutContainer
import org.koin.core.scope.Scope
import org.koin.standalone.getKoin

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
abstract class BaseCBWithScope : BaseController(), LayoutContainer {

    private lateinit var session: Scope
    private val koinScopeName = this::class.java.simpleName

    /**
     * Used to initialize view binding
     */
    protected abstract fun initViewBinding(inflater: LayoutInflater,
                                           container: ViewGroup?,
                                           @LayoutRes layoutResourceId: Int): View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        containerView = initViewBinding(inflater, container, layoutResourceId)

        lifecycleRegistry.markState(Lifecycle.State.INITIALIZED)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)

        session = getKoin().createScope(koinScopeName)

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

    override fun onDestroyView(view: View) {
        super.onDestroyView(view)

        session.close()
    }
}
