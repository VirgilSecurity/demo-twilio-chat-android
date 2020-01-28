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

package com.virgilsecurity.android.feature_settings.di

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.virgilsecurity.android.feature_settings.di.Const.LIVE_DATA_ABOUT_TOOLBAR
import com.virgilsecurity.android.feature_settings.di.Const.LIVE_DATA_SETTINGS_EDIT_BSD
import com.virgilsecurity.android.feature_settings.di.Const.LIVE_DATA_SETTINGS_EDIT_FOOTER
import com.virgilsecurity.android.feature_settings.di.Const.LIVE_DATA_SETTINGS_EDIT_HEADER
import com.virgilsecurity.android.feature_settings.di.Const.LIVE_DATA_SETTINGS_EDIT_TOOLBAR
import com.virgilsecurity.android.feature_settings.di.Const.LIVE_DATA_SETTINGS_FOOTER
import com.virgilsecurity.android.feature_settings.di.Const.LIVE_DATA_SETTINGS_HEADER
import com.virgilsecurity.android.feature_settings.di.Const.LIVE_DATA_SETTINGS_MENU
import com.virgilsecurity.android.feature_settings.di.Const.LIVE_DATA_SETTINGS_TOOLBAR
import com.virgilsecurity.android.feature_settings.di.Const.LIVE_DATA_VERSION_HISTORY_TOOLBAR
import com.virgilsecurity.android.feature_settings.domain.DeleteAccountDo
import com.virgilsecurity.android.feature_settings.domain.DeleteAccountDoDefault
import com.virgilsecurity.android.feature_settings.domain.LogoutDo
import com.virgilsecurity.android.feature_settings.domain.LogoutDoDefault
import com.virgilsecurity.android.feature_settings.view.SettingsController
import com.virgilsecurity.android.feature_settings.view.SettingsEditController
import com.virgilsecurity.android.feature_settings.viewmodel.edit.SettingsEditVM
import com.virgilsecurity.android.feature_settings.viewmodel.edit.SettingsEditVMDefault
import com.virgilsecurity.android.feature_settings.viewmodel.settings.SettingsVM
import com.virgilsecurity.android.feature_settings.viewmodel.settings.SettingsVMDefault
import com.virgilsecurity.android.feature_settings.viewslice.about.toolbar.ToolbarSliceSettingsAbout
import com.virgilsecurity.android.feature_settings.viewslice.edit.bottomsheet.BSDSimpleSlice
import com.virgilsecurity.android.feature_settings.viewslice.edit.bottomsheet.BSDSimpleSliceSettingsEdit
import com.virgilsecurity.android.feature_settings.viewslice.edit.footer.FooterSliceSettingsEdit
import com.virgilsecurity.android.feature_settings.viewslice.edit.header.HeaderSliceSettingsEdit
import com.virgilsecurity.android.feature_settings.viewslice.edit.state.StateSliceSettingsEdit
import com.virgilsecurity.android.feature_settings.viewslice.edit.toolbar.ToolbarSliceSettingsEdit
import com.virgilsecurity.android.feature_settings.viewslice.settings.footer.FooterSlice
import com.virgilsecurity.android.feature_settings.viewslice.settings.footer.FooterSliceSettings
import com.virgilsecurity.android.feature_settings.viewslice.settings.header.HeaderSlice
import com.virgilsecurity.android.feature_settings.viewslice.settings.header.HeaderSliceSettings
import com.virgilsecurity.android.feature_settings.viewslice.settings.menu.MenuSlice
import com.virgilsecurity.android.feature_settings.viewslice.settings.menu.MenuSliceSettings
import com.virgilsecurity.android.feature_settings.viewslice.settings.state.StateSlice
import com.virgilsecurity.android.feature_settings.viewslice.settings.state.StateSliceSettings
import com.virgilsecurity.android.feature_settings.viewslice.settings.toolbar.ToolbarSlice
import com.virgilsecurity.android.feature_settings.viewslice.settings.toolbar.ToolbarSliceSettings
import com.virgilsecurity.android.feature_settings.viewslice.versions.toolbar.ToolbarSliceSettingsVersions
import org.koin.dsl.module.Module
import org.koin.dsl.module.module

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    7/18/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

/**
 * SettingsModules
 */

val settingsModule: Module = module {

    factory { StateSliceSettings() as StateSlice }
    factory {
        StateSliceSettingsEdit() as
                com.virgilsecurity.android.feature_settings.viewslice.edit.state.StateSlice
    }

    // Settings
    module(SettingsController::class.java.simpleName) {
        factory { MediatorLiveData<SettingsVM.State>() }
        factory { SettingsVMDefault(get(), get()) as SettingsVM }
    }

    factory(LIVE_DATA_SETTINGS_FOOTER) { MutableLiveData<FooterSlice.Action>() }
    factory { FooterSliceSettings(get(LIVE_DATA_SETTINGS_FOOTER)) as FooterSlice }

    factory(LIVE_DATA_SETTINGS_TOOLBAR) { MutableLiveData<ToolbarSlice.Action>() }
    factory { ToolbarSliceSettings(get(LIVE_DATA_SETTINGS_TOOLBAR)) as ToolbarSlice }

    factory(LIVE_DATA_SETTINGS_MENU) { MutableLiveData<MenuSlice.Action>() }
    factory { MenuSliceSettings(get(LIVE_DATA_SETTINGS_MENU)) as MenuSlice }

    factory(LIVE_DATA_SETTINGS_HEADER) { MutableLiveData<HeaderSlice.Action>() }
    factory { HeaderSliceSettings(get(LIVE_DATA_SETTINGS_HEADER), get()) as HeaderSlice }

    factory { LogoutDoDefault(get(), get()) as LogoutDo }

    // Settings edit
    module(SettingsEditController::class.java.simpleName) {
        factory { MediatorLiveData<SettingsEditVM.State>() }
        factory { SettingsEditVMDefault(get(), get()) as SettingsEditVM }
    }

    factory(LIVE_DATA_SETTINGS_EDIT_TOOLBAR) {
        MutableLiveData<com.virgilsecurity.android.feature_settings.viewslice.edit.toolbar.ToolbarSlice.Action>()
    }
    factory {
        ToolbarSliceSettingsEdit(get(LIVE_DATA_SETTINGS_EDIT_TOOLBAR)) as
                com.virgilsecurity.android.feature_settings.viewslice.edit.toolbar.ToolbarSlice
    }

    factory(LIVE_DATA_SETTINGS_EDIT_HEADER) {
        MutableLiveData<com.virgilsecurity.android.feature_settings.viewslice.edit.header.HeaderSlice.Action>()
    }
    factory {
        HeaderSliceSettingsEdit(get(LIVE_DATA_SETTINGS_EDIT_HEADER), get()) as
                com.virgilsecurity.android.feature_settings.viewslice.edit.header.HeaderSlice
    }

    factory(LIVE_DATA_SETTINGS_EDIT_FOOTER) {
        MutableLiveData<com.virgilsecurity.android.feature_settings.viewslice.edit.footer.FooterSlice.Action>()
    }
    factory {
        FooterSliceSettingsEdit(get(LIVE_DATA_SETTINGS_EDIT_FOOTER)) as
                com.virgilsecurity.android.feature_settings.viewslice.edit.footer.FooterSlice
    }

    factory(LIVE_DATA_SETTINGS_EDIT_BSD) { MutableLiveData<BSDSimpleSlice.Action>() }
    factory { BSDSimpleSliceSettingsEdit(get(LIVE_DATA_SETTINGS_EDIT_BSD)) as BSDSimpleSlice }

    factory { DeleteAccountDoDefault(get(), get(), get(), get()) as DeleteAccountDo }

    // About screen
    factory(LIVE_DATA_ABOUT_TOOLBAR) {
        MutableLiveData<com.virgilsecurity.android.feature_settings.viewslice.about.toolbar.ToolbarSlice.Action>()
    }
    factory {
        ToolbarSliceSettingsAbout(get(LIVE_DATA_ABOUT_TOOLBAR)) as
                com.virgilsecurity.android.feature_settings.viewslice.about.toolbar.ToolbarSlice

    }

    // Version history screen
    factory(LIVE_DATA_VERSION_HISTORY_TOOLBAR) {
        MutableLiveData<com.virgilsecurity.android.feature_settings.viewslice.versions.toolbar.ToolbarSlice.Action>()
    }
    factory {
        ToolbarSliceSettingsVersions(get(LIVE_DATA_VERSION_HISTORY_TOOLBAR)) as
                com.virgilsecurity.android.feature_settings.viewslice.versions.toolbar.ToolbarSlice
    }
}

object Const {
    const val LIVE_DATA_SETTINGS_TOOLBAR = "LIVE_DATA_SETTINGS_TOOLBAR"
    const val LIVE_DATA_SETTINGS_FOOTER = "LIVE_DATA_SETTINGS_FOOTER"
    const val LIVE_DATA_SETTINGS_MENU = "LIVE_DATA_SETTINGS_MENU"
    const val LIVE_DATA_SETTINGS_HEADER = "LIVE_DATA_SETTINGS_HEADER"

    const val LIVE_DATA_SETTINGS_EDIT_TOOLBAR = "LIVE_DATA_SETTINGS_EDIT_TOOLBAR"
    const val LIVE_DATA_SETTINGS_EDIT_HEADER = "LIVE_DATA_SETTINGS_EDIT_HEADER"
    const val LIVE_DATA_SETTINGS_EDIT_FOOTER = "LIVE_DATA_SETTINGS_EDIT_FOOTER"
    const val LIVE_DATA_SETTINGS_EDIT_BSD = "LIVE_DATA_SETTINGS_EDIT_BSD"

    const val LIVE_DATA_ABOUT_TOOLBAR = "LIVE_DATA_ABOUT_TOOLBAR"
    const val LIVE_DATA_VERSION_HISTORY_TOOLBAR = "LIVE_DATA_VERSION_HISTORY_TOOLBAR"
}
