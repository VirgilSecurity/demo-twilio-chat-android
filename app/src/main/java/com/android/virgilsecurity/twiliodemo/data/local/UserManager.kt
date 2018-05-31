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

package com.android.virgilsecurity.twiliodemo.data.local

import android.content.Context
import com.android.virgilsecurity.twiliodemo.data.local.PreferenceHelper.edit
import com.android.virgilsecurity.twiliodemo.data.local.PreferenceHelper.get
import com.android.virgilsecurity.twiliodemo.data.local.PreferenceHelper.set
import com.android.virgilsecurity.twiliodemo.data.model.Token
import com.android.virgilsecurity.twiliodemo.data.model.TwilioUser
import com.android.virgilsecurity.twiliodemo.data.model.User
import com.android.virgilsecurity.twiliodemo.data.model.VirgilToken
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.virgilsecurity.sdk.cards.Card
import com.virgilsecurity.sdk.cards.model.RawSignedModel
import com.virgilsecurity.sdk.crypto.VirgilCardCrypto
import java.util.*

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

class UserManager(context: Context) {

    companion object {
        private const val CURRENT_USER = "CURRENT_USER"
        private const val USER_CARDS = "USER_CARDS"
        private const val VIRGIL_TOKEN = "VIRGIL_TOKEN"
        private const val TWILIO_TOKEN = "TWILIO_TOKEN"
    }

    private val preferences = PreferenceHelper.defaultPrefs(context)

    fun setCurrentUser(user: User) {
        preferences[CURRENT_USER] = Gson().toJson(user)
    }

    fun getCurrentUser(): TwilioUser {
        val serialized: String? = preferences[CURRENT_USER]
        return Gson().fromJson(serialized, TwilioUser::class.java)
    }

    fun clearCurrentUser() {
        preferences.edit { it.remove(CURRENT_USER) }
    }

    fun setUserCard(card: Card) {
        val serialized = Gson().toJson(card.rawCard)
        preferences[USER_CARDS] = serialized
    }

    fun getUserCard(): Card {
        val serialized: String? = preferences[USER_CARDS]
        val rawSignedModel = Gson().fromJson<RawSignedModel>(serialized,
                object : TypeToken<RawSignedModel>() {}.type)

        return Card.parse(VirgilCardCrypto(), rawSignedModel)
    }

    fun clearUserCard() {
        preferences.edit { it.remove(USER_CARDS) }
    }

    fun setVigilToken(token: Token) {
        preferences[VIRGIL_TOKEN] = Gson().toJson(token)
    }

    fun getVirgilToken(): VirgilToken {
        val serialized: String? = preferences[VIRGIL_TOKEN]
        return Gson().fromJson<VirgilToken>(serialized, VirgilToken::class.java)
    }

    fun clearVirgilToken() {
        preferences.edit { it.remove(VIRGIL_TOKEN) }
    }

    fun setTwilioToken(token: Token) {
        preferences[TWILIO_TOKEN] = Gson().toJson(token)
    }

    fun getTwilioToken(): VirgilToken {
        val serialized: String? = preferences[TWILIO_TOKEN]
        return Gson().fromJson<VirgilToken>(serialized, VirgilToken::class.java)
    }

    fun clearTwilioToken() {
        preferences.edit { it.remove(TWILIO_TOKEN) }
    }
}