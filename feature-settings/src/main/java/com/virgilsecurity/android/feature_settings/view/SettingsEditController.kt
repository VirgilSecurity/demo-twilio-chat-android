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

package com.virgilsecurity.android.feature_settings.view

import android.view.View
import com.virgilsecurity.android.base.data.model.User
import com.virgilsecurity.android.base.extension.observe
import com.virgilsecurity.android.base.extension.toKoinPath
import com.virgilsecurity.android.base.view.BaseController
import com.virgilsecurity.android.common.util.UiUtils
import com.virgilsecurity.android.feature_settings.R
import com.virgilsecurity.android.feature_settings.di.Const.VM_SETTINGS_EDIT
import com.virgilsecurity.android.feature_settings.viewmodel.edit.SettingsEditVM
import com.virgilsecurity.android.feature_settings.viewmodel.settings.SettingsVM
import com.virgilsecurity.android.feature_settings.viewslice.edit.bottomsheet.BSDSimpleSlice
import com.virgilsecurity.android.feature_settings.viewslice.edit.footer.FooterSlice
import com.virgilsecurity.android.feature_settings.viewslice.edit.header.HeaderSlice
import com.virgilsecurity.android.feature_settings.viewslice.edit.state.StateSlice
import com.virgilsecurity.android.feature_settings.viewslice.edit.toolbar.ToolbarSlice
import org.koin.core.inject
import org.koin.core.qualifier.named

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
class SettingsEditController() : BaseController() {

    override val layoutResourceId: Int = R.layout.controller_settings_edit

    private val toolbarSlice: ToolbarSlice by inject()
    private val stateSlice: StateSlice by inject()
    private val headerSlice: HeaderSlice by inject()
    private val footerSlice: FooterSlice by inject()
    private val bottomSheetSlice: BSDSimpleSlice by inject()
    private val viewModel: SettingsEditVM by inject(named(VM_SETTINGS_EDIT))

    private lateinit var logout: () -> Unit
    private lateinit var user: User

    constructor(user: User, logout: () -> Unit) : this() {
        this.user = user
        this.logout = logout
    }

    override fun init() {}

    override fun initViewSlices(view: View) {
        toolbarSlice.init(lifecycle, view)
        stateSlice.init(lifecycle, view)
        headerSlice.init(lifecycle, view)
        footerSlice.init(lifecycle, view)
        bottomSheetSlice.init(lifecycle, view)
    }

    override fun setupViewSlices(view: View) {
        headerSlice.setName(user.identity)
        headerSlice.setUserPic(user.identity, user.picturePath)
    }

    override fun setupVSActionObservers() {
        observe(toolbarSlice.getAction(), ::onToolbarActionChanged)
        observe(headerSlice.getAction(), ::onHeaderActionChanged)
        observe(footerSlice.getAction(), ::onFooterActionChanged)
        observe(bottomSheetSlice.getAction(), ::onBottomSheetActionChanged)
    }

    override fun setupVMStateObservers() {
        observe(viewModel.getState(), ::onStateChanged)
    }

    override fun initData() {}

    private fun onStateChanged(state: SettingsEditVM.State) = when (state) {
        SettingsEditVM.State.ShowLoading -> stateSlice.showLoading()
        SettingsEditVM.State.ShowError -> stateSlice.showError()
        SettingsEditVM.State.DeleteAccountSuccess -> logout()
        SettingsEditVM.State.Idle -> Unit
    }

    private fun onToolbarActionChanged(action: ToolbarSlice.Action) = when (action) {
        ToolbarSlice.Action.BackClicked -> {
            hideKeyboard()
            backPress()
        }
        ToolbarSlice.Action.Idle -> Unit
    }

    private fun onHeaderActionChanged(action: HeaderSlice.Action) = when (action) {
        HeaderSlice.Action.ChangePicClicked -> UiUtils.toastUnderDevelopment(this)
        HeaderSlice.Action.Idle -> Unit
    }

    private fun onFooterActionChanged(action: FooterSlice.Action) = when (action) {
        FooterSlice.Action.DeleteAccountClicked -> {
            bottomSheetSlice.setTitle(activity!!.getString(R.string.delete_account_question))
            bottomSheetSlice.setBody(activity!!.getString(R.string.delete_account_beware))
            bottomSheetSlice.show()
        }
        FooterSlice.Action.Idle -> Unit
    }

    private fun onBottomSheetActionChanged(action: BSDSimpleSlice.Action) = when (action) {
        BSDSimpleSlice.Action.YesClicked -> viewModel.deleteAccount()
        BSDSimpleSlice.Action.NoClicked -> bottomSheetSlice.dismiss()
        BSDSimpleSlice.Action.Idle -> Unit
    }

    private fun backPress() {
        router.popCurrentController()
    }

    companion object {
        const val KEY_EDIT_SETTINGS_CONTROLLER = "KEY_EDIT_SETTINGS_CONTROLLER"
    }
}
