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

package com.virgilsecurity.android.feature_drawer_navigation.view

import android.view.View
import android.view.Window
import androidx.lifecycle.MutableLiveData
import com.virgilsecurity.android.base.data.model.User
import com.virgilsecurity.android.base.extension.observe
import com.virgilsecurity.android.base.view.controller.BaseController
import com.virgilsecurity.android.common.util.currentScopeViewModel
import com.virgilsecurity.android.feature_drawer_navigation.R
import com.virgilsecurity.android.feature_drawer_navigation.viewmodel.InitSmackVM
import com.virgilsecurity.android.feature_drawer_navigation.viewslice.smackInit.interaction.SliceSmackInit
import com.virgilsecurity.android.feature_drawer_navigation.viewslice.smackInit.state.StateSliceSmackInit

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
 * SmackInitController
 */
class SmackInitController() : BaseController() {

    override val layoutResourceId: Int = R.layout.controller_smack_init

    private val viewModel: InitSmackVM by currentScopeViewModel()

    private lateinit var user: User
    private lateinit var initSuccess: () -> Unit
    private lateinit var stateSlice: StateSliceSmackInit
    private lateinit var mldSliceSmackInit: MutableLiveData<SliceSmackInit.Action>
    private lateinit var sliceSmackInit: SliceSmackInit

    constructor(user: User, initSuccess: () -> Unit) : this() {
        this.user = user
        this.initSuccess = initSuccess
    }

    override fun init(containerView: View) {
        viewModel.initChatClient(user)
        this.mldSliceSmackInit = MutableLiveData()
    }

    override fun initViewSlices(window: Window) {
        this.stateSlice = StateSliceSmackInit() // TODO check if this order is correct, so VM doesn't call slices before they're ready
        this.sliceSmackInit = SliceSmackInit(mldSliceSmackInit)

        sliceSmackInit.init(lifecycle, window)
        stateSlice.init(lifecycle, window)
    }

    override fun setupViewSlices(view: View) {}

    private fun onActionChanged(action: SliceSmackInit.Action) = when (action) {
        SliceSmackInit.Action.RetryClicked -> viewModel.initChatClient(user)
        SliceSmackInit.Action.Idle -> Unit
    }

    override fun setupVSActionObservers() {
        observe(sliceSmackInit.getAction(), ::onActionChanged)
    }

    override fun setupVMStateObservers() {
        observe(viewModel.getState(), ::onStateChanged)
    }

    override fun initData() {}

    private fun onStateChanged(state: InitSmackVM.State) = when (state) {
        InitSmackVM.State.InitSuccess -> initSuccess()
        InitSmackVM.State.ShowLoading -> stateSlice.showLoading()
        InitSmackVM.State.ShowContent -> stateSlice.showContent()
        InitSmackVM.State.ShowError -> stateSlice.showError()
    }

    override fun handleBack(): Boolean {
        return if (viewModel.getState().value != InitSmackVM.State.ShowLoading)
            true
        else
            super.handleBack()
    }
}
