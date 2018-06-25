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

import com.android.virgilsecurity.common.data.local.UserManager
import com.android.virgilsecurity.twiliodemo.data.model.SignInResponse
import com.android.virgilsecurity.twiliodemo.data.remote.fuel.FuelHelper
import com.android.virgilsecurity.twiliodemo.data.remote.virgil.VirgilHelper
import com.android.virgilsecurity.twiliodemo.data.remote.virgil.VirgilRx
import com.github.kittinunf.fuel.core.FuelError
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
                     private val virgilRx: VirgilRx) {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    fun requestSignUp(identity: String,
                      onSignUpSuccess: (SignInResponse) -> Unit,
                      onSignUpError: (Throwable) -> Unit) {

        requestIfKeyExists(identity,
                           onKeyExists = {
                               onSignUpError(Throwable("Private key for this identity already exists on current device"))
                           },
                           onKeyNotExists = {
                               signUp(identity, onSignUpSuccess, onSignUpError)
                           })
    }

    fun requestSignIn(identity: String,
                      onSignInSuccess: (SignInResponse) -> Unit,
                      onSignInError: (Throwable) -> Unit) {
        val signUpDisposable =
                Single.create<SignInResponse> {
                    it.onSuccess(fuelHelper.signIn(identity))
                }.observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribeBy(
                            onSuccess = {

                                requestIfKeyExists(identity,
                                                   onKeyExists = {
                                                       userManager.setCurrentUser(TwilioUser(
                                                           identity))

                                                       val rawCardModel = it.virgilCard
                                                       userManager.setUserCard(Card.parse(
                                                           virgilHelper.cardCrypto,
                                                           rawCardModel))

                                                       onSignInSuccess(it)
                                                   },
                                                   onKeyNotExists = {
                                                       onSignInError(Throwable("No private key for this identity on device"))
                                                   })
                            },
                            onError = {
                                onSignInError(it)
                            })

        compositeDisposable += signUpDisposable
    }

    private fun requestIfKeyExists(keyName: String,
                                   onKeyExists: () -> Unit,
                                   onKeyNotExists: () -> Unit) {
        if (virgilHelper.ifExistsPrivateKey(keyName))
            onKeyExists()
        else
            onKeyNotExists()
    }

    private fun signUp(identity: String,
                       onSignUpSuccess: (SignInResponse) -> Unit,
                       onSignUpError: (Throwable) -> Unit) {

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

                                onSignUpSuccess(it)
                            },
                            onError = {
                                virgilHelper.deletePrivateKey(identity)

                                if (it is FuelError)
                                    onSignUpError(Throwable(String(it.response.data)))
                                else
                                    onSignUpError(it)
                            })

        compositeDisposable += signUpDisposable
    }

    override fun disposeAll() {
        compositeDisposable.clear()
    }
}