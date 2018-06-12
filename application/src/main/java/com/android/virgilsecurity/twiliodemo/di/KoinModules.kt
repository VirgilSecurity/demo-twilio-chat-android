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

package com.android.virgilsecurity.twiliodemo.di

import android.content.Context
import com.android.virgilsecurity.twiliodemo.data.local.UserManager
import com.android.virgilsecurity.twiliodemo.data.remote.fuel.FuelHelper
import com.android.virgilsecurity.twiliodemo.data.remote.twilio.TwilioHelper
import com.android.virgilsecurity.twiliodemo.data.remote.twilio.TwilioRx
import com.android.virgilsecurity.twiliodemo.data.remote.virgil.GetTokenCallbackImpl
import com.android.virgilsecurity.twiliodemo.data.remote.virgil.VirgilHelper
import com.android.virgilsecurity.twiliodemo.data.remote.virgil.VirgilRx
import com.android.virgilsecurity.twiliodemo.util.Utils
import com.virgilsecurity.sdk.cards.CardManager
import com.virgilsecurity.sdk.cards.validation.CardVerifier
import com.virgilsecurity.sdk.cards.validation.VirgilCardVerifier
import com.virgilsecurity.sdk.crypto.*
import com.virgilsecurity.sdk.jwt.accessProviders.CallbackJwtProvider
import com.virgilsecurity.sdk.jwt.contract.AccessTokenProvider
import com.virgilsecurity.sdk.storage.JsonFileKeyStorage
import com.virgilsecurity.sdk.storage.KeyStorage
import com.virgilsecurity.sdk.storage.PrivateKeyStorage
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    5/30/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

/**
 * Modules
 */
object Keys {
    const val STORAGE_PATH = "storagePath"
}

val utilsModule : Module = applicationContext {
    bean { UserManager(get())}
    bean { Utils(get(), get(), get())}
}

val networkModule : Module = applicationContext {
    bean { FuelHelper() }
}

val virgilModule : Module = applicationContext {
    bean { VirgilCardCrypto() as CardCrypto }
    bean { VirgilCrypto() }
    bean { VirgilCardVerifier(get()) as CardVerifier }
    bean { GetTokenCallbackImpl(get(), get(), get()) as CallbackJwtProvider.GetTokenCallback }
    bean { CallbackJwtProvider(get()) as AccessTokenProvider }
    bean { VirgilPrivateKeyExporter() as PrivateKeyExporter }
    bean { JsonFileKeyStorage(get(Keys.STORAGE_PATH)) as KeyStorage }
    bean { PrivateKeyStorage(get(), get()) }
    bean { VirgilHelper(get(), get(), get(), get()) }
    bean { CardManager(get(), get(), get()) }
    bean { VirgilRx(get()) }
}

val twilioModule : Module = applicationContext {
    bean { TwilioRx(get(), get()) }
    bean { TwilioHelper(get(), get()) }
}

val paramsModule : Module = applicationContext {
    bean(Keys.STORAGE_PATH) { ((get() as Context).filesDir.absolutePath) as String }
}
