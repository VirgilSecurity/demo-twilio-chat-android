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
import com.virgilsecurity.android.feature_settings.R
import com.virgilsecurity.android.feature_settings.viewmodel.settings.SettingsVM
import com.virgilsecurity.android.feature_settings.viewslice.settings.footer.FooterSliceSettings
import com.virgilsecurity.android.feature_settings.viewslice.settings.header.HeaderSliceSettings
import com.virgilsecurity.android.feature_settings.viewslice.settings.menu.MenuSliceSettings
import com.virgilsecurity.android.feature_settings.viewslice.settings.state.StateSliceSettings
import com.virgilsecurity.android.feature_settings.viewslice.settings.toolbar.ToolbarSliceSettings
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
class SettingsController() : BaseController() {

    override val layoutResourceId: Int = R.layout.controller_settings

    private val viewModel: SettingsVM by currentScope.viewModel(this)
    private val imageStorage: ImageStorage by inject()

    private lateinit var edit: () -> Unit
    private lateinit var logout: () -> Unit
    private lateinit var about: () -> Unit
    private lateinit var versionHistory: () -> Unit
    private lateinit var user: User
    private lateinit var mldToolbarSlice: MutableLiveData<ToolbarSliceSettings.Action>
    private lateinit var toolbarSlice: ToolbarSliceSettings
    private lateinit var mldMenuSlice: MutableLiveData<MenuSliceSettings.Action>
    private lateinit var menuSlice: MenuSliceSettings
    private lateinit var mldHeaderSlice: MutableLiveData<HeaderSliceSettings.Action>
    private lateinit var headerSlice: HeaderSliceSettings
    private lateinit var mldFooterSlice: MutableLiveData<FooterSliceSettings.Action>
    private lateinit var footerSlice: FooterSliceSettings
    private lateinit var stateSlice: StateSliceSettings

    constructor(user: User,
                edit: () -> Unit,
                logout: () -> Unit,
                about: () -> Unit,
                versionHistory: () -> Unit) : this() {
        this.user = user
        this.edit = edit
        this.logout = logout
        this.about = about
        this.versionHistory = versionHistory
    }

    override fun init(containerView: View) {
        this.mldToolbarSlice = MutableLiveData()
        this.mldMenuSlice = MutableLiveData()
        this.mldHeaderSlice = MutableLiveData()
        this.mldFooterSlice = MutableLiveData()
    }

    override fun initViewSlices(window: Window) {
        this.toolbarSlice = ToolbarSliceSettings(mldToolbarSlice)
        this.menuSlice = MenuSliceSettings(mldMenuSlice)
        this.headerSlice = HeaderSliceSettings(mldHeaderSlice, imageStorage)
        this.footerSlice = FooterSliceSettings(mldFooterSlice)
        this.stateSlice = StateSliceSettings()

        toolbarSlice.init(lifecycle, window)
        menuSlice.init(lifecycle, window)
        headerSlice.init(lifecycle, window)
        footerSlice.init(lifecycle, window)
        stateSlice.init(lifecycle, window)
    }

    override fun setupViewSlices(view: View) {
        headerSlice.setName(user.identity)
        headerSlice.setUserPic(user.identity, user.picturePath)
    }

    override fun setupVSActionObservers() {
        observe(toolbarSlice.getAction(), ::onToolbarActionChanged)
        observe(menuSlice.getAction(), ::onMenuActionChanged)
        observe(headerSlice.getAction(), ::onHeaderActionChanged)
        observe(footerSlice.getAction(), ::onFooterActionChanged)
    }

    override fun setupVMStateObservers() {
        observe(viewModel.getState(), ::onStateChanged)
    }

    override fun initData() {}

    private fun onStateChanged(state: SettingsVM.State) = when (state) {
        SettingsVM.State.LogoutSuccessful -> logout()
        SettingsVM.State.ShowLoading -> stateSlice.showLoading()
        SettingsVM.State.ShowError -> {
            stateSlice.showError()
            UiUtils.toast(this, activity!!.getString(R.string.logout_error))
        }
        SettingsVM.State.Idle -> Unit
    }

    private fun onToolbarActionChanged(action: ToolbarSliceSettings.Action) = when (action) {
        ToolbarSliceSettings.Action.BackClicked -> {
            hideKeyboard()
            backPress()
        }
        is ToolbarSliceSettings.Action.MenuClicked -> menuSlice.show(action.showPoint)
        ToolbarSliceSettings.Action.Idle -> Unit
    }

    private fun onMenuActionChanged(action: MenuSliceSettings.Action) = when (action) {
        MenuSliceSettings.Action.EditClicked -> {
            menuSlice.dismiss()
            edit()
        }
        MenuSliceSettings.Action.LogoutClicked -> {
            menuSlice.dismiss()
            viewModel.logout()
        }
        MenuSliceSettings.Action.Idle -> Unit
    }

    private fun onHeaderActionChanged(action: HeaderSliceSettings.Action) = when (action) {
        HeaderSliceSettings.Action.ChangePicClicked -> UiUtils.toastUnderDevelopment(this)
        HeaderSliceSettings.Action.Idle -> Unit
    }

    private fun onFooterActionChanged(action: FooterSliceSettings.Action) = when (action) {
        FooterSliceSettings.Action.AboutClicked -> about()
        FooterSliceSettings.Action.AskQuestionClicked -> UiUtils.toastUnderDevelopment(this)
        FooterSliceSettings.Action.VersionClicked -> versionHistory()
        FooterSliceSettings.Action.Idle -> Unit
    }

    private fun backPress() {
        router.popCurrentController()
    }

    companion object {
        const val KEY_SETTINGS_CONTROLLER = "KEY_SETTINGS_CONTROLLER"
    }
}
