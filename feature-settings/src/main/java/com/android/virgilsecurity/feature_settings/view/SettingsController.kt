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

package com.android.virgilsecurity.feature_settings.view

import android.view.View
import com.android.virgilsecurity.base.data.model.User
import com.android.virgilsecurity.base.extension.observe
import com.android.virgilsecurity.base.view.BaseController
import com.android.virgilsecurity.common.util.UiUtils
import com.android.virgilsecurity.feature_settings.R
import com.android.virgilsecurity.feature_settings.di.Const.CONTEXT_SETTINGS
import com.android.virgilsecurity.feature_settings.viewmodel.SettingsVM
import com.android.virgilsecurity.feature_settings.viewslice.footer.FooterSlice
import com.android.virgilsecurity.feature_settings.viewslice.header.HeaderSlice
import com.android.virgilsecurity.feature_settings.viewslice.menu.MenuSlice
import com.android.virgilsecurity.feature_settings.viewslice.state.StateSlice
import com.android.virgilsecurity.feature_settings.viewslice.toolbar.ToolbarSlice
import org.koin.standalone.inject

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
 * SettingsController
 */
class SettingsController() : BaseController() {

    override val layoutResourceId: Int = R.layout.controller_settings
    override val koinContextName: String? = CONTEXT_SETTINGS

    private val toolbarSlice: ToolbarSlice by inject()
    private val menuSlice: MenuSlice by inject()
    private val headerSlice: HeaderSlice by inject()
    private val footerSlice: FooterSlice by inject()
    private val stateSlice: StateSlice by inject()
    private val viewModel: SettingsVM by inject()

    private lateinit var logout: () -> Unit
    private lateinit var user: User

    constructor(user: User, logout: () -> Unit) : this() {
        this.user = user
        this.logout = logout
    }

    override fun init() {}

    override fun initViewSlices(view: View) {
        toolbarSlice.init(lifecycle, view)
        menuSlice.init(lifecycle, view)
        headerSlice.init(lifecycle, view)
        footerSlice.init(lifecycle, view)
        stateSlice.init(lifecycle, view)
    }

    override fun setupViewSlices(view: View) {
        headerSlice.setName(user.identity)
        headerSlice.setUserPic(user.identity, user.picturePath)
    }

    override fun setupVSActionObservers() {
        observe(toolbarSlice.getAction()) { onToolbarActionChanged(it) }
        observe(menuSlice.getAction()) { onMenuActionChanged(it) }
        observe(headerSlice.getAction()) { onHeaderActionChanged(it) }
        observe(footerSlice.getAction()) { onFooterActionChanged(it) }
    }

    override fun setupVMStateObservers() {
        observe(viewModel.getState()) { onStateChanged(it) }
    }

    override fun initData() {}

    private fun onStateChanged(state: SettingsVM.State) = when (state) {
        SettingsVM.State.LogoutSuccessful -> logout()
        SettingsVM.State.ShowLoading -> stateSlice.showLoading()
        SettingsVM.State.ShowError -> stateSlice.showError()
        SettingsVM.State.Idle -> Unit
    }

    private fun onToolbarActionChanged(action: ToolbarSlice.Action) = when (action) {
        ToolbarSlice.Action.BackClicked -> {
            hideKeyboard()
            backPress()
        }
        is ToolbarSlice.Action.MenuClicked -> menuSlice.showMenu(action.showPoint)
        ToolbarSlice.Action.Idle -> Unit
    }

    private fun onMenuActionChanged(action: MenuSlice.Action) = when (action) {
        MenuSlice.Action.EditClicked -> UiUtils.toast(this, "Touch once more (;")
        MenuSlice.Action.LogoutClicked -> viewModel.logout()
        MenuSlice.Action.Idle -> Unit
    }

    private fun onHeaderActionChanged(action: HeaderSlice.Action) = when (action) {
        HeaderSlice.Action.ChangePicClicked -> UiUtils.toastUnderDevelopment(this)
        HeaderSlice.Action.Idle -> Unit
    }

    private fun onFooterActionChanged(action: FooterSlice.Action) = when (action) {
        FooterSlice.Action.AboutClicked -> UiUtils.toastUnderDevelopment(this)
        FooterSlice.Action.AskQuestionClicked -> UiUtils.toastUnderDevelopment(this)
        FooterSlice.Action.VersionClicked -> UiUtils.toastUnderDevelopment(this)
        FooterSlice.Action.Idle -> Unit
    }

    private fun backPress() {
        router.popCurrentController()
    }

    companion object {
        const val KEY_SETTINGS_CONTROLLER = "KEY_SETTINGS_CONTROLLER"
    }
}