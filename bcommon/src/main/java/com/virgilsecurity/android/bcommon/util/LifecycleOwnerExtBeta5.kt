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

import android.content.ComponentCallbacks
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import org.koin.android.ext.android.getKoin
import org.koin.androidx.scope.ScopeObserver
import org.koin.core.KoinComponent
import org.koin.core.context.GlobalContext
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.TypeQualifier
import org.koin.core.scope.Scope

/**
 * Provide an scope for given LifecycleOwner component
 *
 * Taken from 2.0.0-beta-5 version
 *
 * @author Arnaud Giuliani
 */

private fun LifecycleOwner.getKoin() = when (this) {
    is ComponentCallbacks -> (this as ComponentCallbacks).getKoin()
    is KoinComponent -> this.getKoin()
    else -> GlobalContext.get()
}

private fun LifecycleOwner.getScopeName() = TypeQualifier(this::class)
private fun LifecycleOwner.getScopeId() = this.toString()

private fun LifecycleOwner.getOrCreateCurrentScope(): Scope {
    val scopeId = getScopeId()
    return getKoin().getScopeOrNull(scopeId) ?: createAndBindScope(scopeId, getScopeName())
}

private fun LifecycleOwner.createAndBindScope(scopeId: String, qualifier: Qualifier): Scope {
    val scope = getKoin().createScope(scopeId, qualifier)
    bindScope(scope)
    return scope
}

/**
 * Bind given scope to current LifecycleOwner
 * @param scope
 * @param event
 */
fun LifecycleOwner.bindScope(scope: Scope, event: Lifecycle.Event = Lifecycle.Event.ON_DESTROY) {
    lifecycle.addObserver(ScopeObserver(event, this, scope))
}

/**
 * Get current Koin scope, bound to current lifecycle
 */
val LifecycleOwner.currentScope: Scope
    get() = getOrCreateCurrentScope()
