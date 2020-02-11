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

package com.virgilsecurity.android.virgilmessenger

import android.app.Application
import android.os.StrictMode
import authActivityModule
import com.virgilsecurity.android.common.di.*
import com.virgilsecurity.android.feature_channel.di.channelModule
import com.virgilsecurity.android.feature_channels_list.di.channelsListModule
import com.virgilsecurity.android.feature_contacts.di.addContactModule
import com.virgilsecurity.android.feature_contacts.di.contactsModule
import com.virgilsecurity.android.feature_drawer_navigation.di.smackInitModule
import com.virgilsecurity.android.feature_settings.di.settingsEditModule
import com.virgilsecurity.android.feature_settings.di.settingsModule
import com.virgilsecurity.android.virgilmessenger.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import registrationControllerModule

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    5/29/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

class VirgilMorseApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@VirgilMorseApp)
            modules(
                // Base modules
                utilsModule, networkModule, virgilModule, smackModule, paramsModule,

                // Common modules
                commonModules, appModule, messagesModule,
                channelsModule, // used in 'contacts' and 'channels list' for now

                // Auth modules
                authActivityModule, registrationControllerModule,

                // Drawer navigation modules
                smackInitModule,

                // Contacts modules
                contactsModule, addContactModule,

                // Channels list modules
                channelsListModule,

                // Channel modules
                channelModule,

                // Settings modules
                settingsModule, settingsEditModule
            )
        }

        initStrictMode()
    }

    private fun KoinApplication.modules(vararg modules: Module): KoinApplication =
            modules(modules.toList())

    private fun initStrictMode() {
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                                           .detectAll()
                                           .penaltyLog()
                                           .detectNetwork()
                                           .penaltyFlashScreen()
                                           .penaltyDeathOnNetwork()
                                           .build())

        StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                                       .detectAll()
                                       .penaltyLog()
                                       .build())
    }
}
