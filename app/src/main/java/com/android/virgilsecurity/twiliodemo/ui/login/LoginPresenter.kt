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

package com.android.virgilsecurity.twiliodemo.ui.login

import com.android.virgilsecurity.twiliodemo.data.local.UserManager
import com.android.virgilsecurity.twiliodemo.data.model.SignInResponse
import com.android.virgilsecurity.twiliodemo.data.model.TwilioUser
import com.android.virgilsecurity.twiliodemo.data.remote.fuel.FuelHelper
import com.android.virgilsecurity.twiliodemo.data.remote.virgil.VirgilHelper
import com.android.virgilsecurity.twiliodemo.ui.base.BasePresenter
import com.virgilsecurity.sdk.cards.Card
import com.virgilsecurity.sdk.cards.model.RawSignedModel
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    5/31/185/31/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

/**
 * LoginPresenter
 */
class LoginPresenter(private val virgilHelper: VirgilHelper,
                     private val fuelHelper: FuelHelper,
                     private val userManager: UserManager) : BasePresenter {

    fun requestSingIn(identity: String,
                      onSignInSuccess: (SignInResponse) -> Unit,
                      onSignInError: (Throwable) -> Unit) {
        val keyPair = virgilHelper.generateKeyPair()
        val rawCard = virgilHelper.generateRawCard(keyPair, identity)

        Single.create<SignInResponse> {
            it.onSuccess(fuelHelper.signUp(rawCard.exportAsJson()))
        }.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeBy(
                    onSuccess = {
                        userManager.setCurrentUser(TwilioUser(identity))

                        val rawCardModel = RawSignedModel.fromJson(it.virgilCard)
                        userManager.setUserCard(Card.parse(virgilHelper.cardCrypto, rawCardModel))

                        onSignInSuccess(it)
                    },
                    onError = {
                        onSignInError(it)
                    })
    }

    override fun disposeAll() {
        // TODO Implement body or it will be empty ):
    }

}