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
import com.android.virgilsecurity.twiliodemo.data.remote.virgil.VirgilRx
import com.android.virgilsecurity.twiliodemo.ui.base.BasePresenter
import com.virgilsecurity.sdk.cards.Card
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
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
                     private val userManager: UserManager,
                     private val virgilRx: VirgilRx) : BasePresenter {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    fun requestSingIn(identity: String,
                      onSignInSuccess: (SignInResponse) -> Unit,
                      onSignInError: (Throwable) -> Unit) {
        val keyPair = virgilHelper.generateKeyPair()
        val rawCard = virgilHelper.generateRawCard(keyPair, identity)

        virgilHelper.storePrivateKey(keyPair.privateKey, identity)

        val signUpDisposable =
                Single.create<SignInResponse> {
                    it.onSuccess(fuelHelper.signUp(rawCard))
                }.observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribeBy(
                            onSuccess = {
                                userManager.setCurrentUser(TwilioUser(identity))

                                val rawCardModel = it.virgilCard
                                userManager.setUserCard(Card.parse(virgilHelper.cardCrypto,
                                                                   rawCardModel))

                                onSignInSuccess(it)
                            },
                            onError = {
                                virgilHelper.deletePrivateKey(identity)
                                onSignInError(it)
                            })

        compositeDisposable += signUpDisposable
    }

    fun requestSearchCards(identity: String,
                           onSearchCardSuccess: (List<Card>) -> Unit,
                           onSearchCardError: (Throwable) -> Unit) {
        val searchCardDisposable = virgilRx.searchCards(identity)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ cards, throwable ->
                               if (throwable == null && cards.isNotEmpty())
                                   onSearchCardSuccess(cards)
                               else
                                   onSearchCardError(throwable)
                           })

        compositeDisposable += searchCardDisposable
    }

    fun requestPublishCard(identity: String,
                           onPublishCardSuccess: (Card) -> Unit,
                           onPublishCardError: (Throwable) -> Unit) {
        val publishCardDisposable = virgilRx.publishCard(identity)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ card, throwable ->
                               if (throwable == null) {
                                   onPublishCardSuccess(card)
                               } else {
                                   virgilHelper.deletePrivateKey(identity)
                                   onPublishCardError(throwable)
                               }
                           })

        compositeDisposable += publishCardDisposable
    }

    fun requestIfKeyExists(keyName: String,
                           onKeyExists: () -> Unit,
                           onKeyNotExists: () -> Unit) {
        if (virgilHelper.ifExistsPrivateKey(keyName))
            onKeyExists()
        else
            onKeyNotExists()
    }

    override fun disposeAll() {
        compositeDisposable.clear()
    }
}