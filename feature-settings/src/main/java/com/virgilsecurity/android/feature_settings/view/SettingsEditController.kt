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
import android.view.Window
import androidx.lifecycle.MutableLiveData
import com.virgilsecurity.android.base.data.model.User
import com.virgilsecurity.android.base.extension.observe
import com.virgilsecurity.android.base.view.controller.BaseController
import com.virgilsecurity.android.common.util.ImageStorage
import com.virgilsecurity.android.common.util.UiUtils
import com.virgilsecurity.android.common.util.currentScope
import com.virgilsecurity.android.common.util.currentScopeViewModel
import com.virgilsecurity.android.feature_settings.R
import com.virgilsecurity.android.feature_settings.viewmodel.edit.SettingsEditVM
import com.virgilsecurity.android.feature_settings.viewslice.edit.bottomsheet.BSDSimpleSliceSettingsEdit
import com.virgilsecurity.android.feature_settings.viewslice.edit.footer.FooterSliceSettingsEdit
import com.virgilsecurity.android.feature_settings.viewslice.edit.header.HeaderSliceSettingsEdit
import com.virgilsecurity.android.feature_settings.viewslice.edit.state.StateSliceSettingsEdit
import com.virgilsecurity.android.feature_settings.viewslice.edit.toolbar.ToolbarSliceSettingsEdit
import org.koin.androidx.viewmodel.scope.viewModel
import org.koin.core.inject

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

    private val viewModel: SettingsEditVM by currentScopeViewModel()
    private val imageStorage: ImageStorage by inject()

    private lateinit var logout: () -> Unit
    private lateinit var user: User
    private lateinit var mldToolbarSlice: MutableLiveData<ToolbarSliceSettingsEdit.Action>
    private lateinit var toolbarSlice: ToolbarSliceSettingsEdit
    private lateinit var stateSlice: StateSliceSettingsEdit
    private lateinit var mldHeaderSlice: MutableLiveData<HeaderSliceSettingsEdit.Action>
    private lateinit var headerSlice: HeaderSliceSettingsEdit
    private lateinit var mldFooterSlice: MutableLiveData<FooterSliceSettingsEdit.Action>
    private lateinit var footerSlice: FooterSliceSettingsEdit
    private lateinit var mldBottomSheetSlice: MutableLiveData<BSDSimpleSliceSettingsEdit.Action>
    private lateinit var bottomSheetSlice: BSDSimpleSliceSettingsEdit

    constructor(user: User, logout: () -> Unit) : this() {
        this.user = user
        this.logout = logout
    }

    override fun init(containerView: View) {}

    override fun initViewSlices(window: Window) {
        this.toolbarSlice = ToolbarSliceSettingsEdit(mldToolbarSlice)
        this.stateSlice = StateSliceSettingsEdit()
        this.headerSlice = HeaderSliceSettingsEdit(mldHeaderSlice, imageStorage)
        this.footerSlice = FooterSliceSettingsEdit(mldFooterSlice)
        this.bottomSheetSlice = BSDSimpleSliceSettingsEdit(mldBottomSheetSlice)

        toolbarSlice.init(lifecycle, window)
        stateSlice.init(lifecycle, window)
        headerSlice.init(lifecycle, window)
        footerSlice.init(lifecycle, window)
        bottomSheetSlice.init(lifecycle, window)
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

    private fun onToolbarActionChanged(action: ToolbarSliceSettingsEdit.Action) = when (action) {
        ToolbarSliceSettingsEdit.Action.BackClicked -> {
            hideKeyboard()
            backPress()
        }
        ToolbarSliceSettingsEdit.Action.Idle -> Unit
    }

    private fun onHeaderActionChanged(action: HeaderSliceSettingsEdit.Action) = when (action) {
        HeaderSliceSettingsEdit.Action.ChangePicClicked -> UiUtils.toastUnderDevelopment(this)
        HeaderSliceSettingsEdit.Action.Idle -> Unit
    }

    private fun onFooterActionChanged(action: FooterSliceSettingsEdit.Action) = when (action) {
        FooterSliceSettingsEdit.Action.DeleteAccountClicked -> {
            bottomSheetSlice.setTitle(activity!!.getString(R.string.delete_account_question))
            bottomSheetSlice.setBody(activity!!.getString(R.string.delete_account_beware))
            bottomSheetSlice.show()
        }
        FooterSliceSettingsEdit.Action.Idle -> Unit
    }

    private fun onBottomSheetActionChanged(action: BSDSimpleSliceSettingsEdit.Action) = when (action) {
        BSDSimpleSliceSettingsEdit.Action.YesClicked -> viewModel.deleteAccount()
        BSDSimpleSliceSettingsEdit.Action.NoClicked -> bottomSheetSlice.dismiss()
        BSDSimpleSliceSettingsEdit.Action.Idle -> Unit
    }

    private fun backPress() {
        router.popCurrentController()
    }

    companion object {
        const val KEY_EDIT_SETTINGS_CONTROLLER = "KEY_EDIT_SETTINGS_CONTROLLER"
    }
}
