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

package com.android.virgilsecurity.twiliodemo.data.remote.twilio

import android.content.Context
import com.android.virgilsecurity.twiliodemo.data.model.exception.ErrorInfoWrapper
import com.android.virgilsecurity.twiliodemo.data.remote.fuel.FuelHelper
import com.twilio.accessmanager.AccessManager
import com.twilio.chat.CallbackListener
import com.twilio.chat.ChatClient
import com.twilio.chat.ErrorInfo
import com.twilio.chat.StatusListener
import io.reactivex.*
import io.reactivex.schedulers.Schedulers

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
 * TwilioRx
 */
class TwilioRx(private val fuelHelper: FuelHelper) {
    fun getToken(identity: String, authHeader: String): Single<String> = Single.create<String> {
        it.onSuccess(fuelHelper.getTwilioTokenSync(identity, authHeader).token)
    }.subscribeOn(Schedulers.io())

    fun createClient(context: Context, token: String): Single<ChatClient> = Single.create<ChatClient> {
        val props = ChatClient.Properties.Builder().createProperties()

        ChatClient.create(context.applicationContext,
                token,
                props,
                object : CallbackListener<ChatClient>() {
                    override fun onSuccess(chatClient: ChatClient) {
                        it.onSuccess(chatClient)
                    }

                    override fun onError(errorInfo: ErrorInfo?) {
                        it.onError(ErrorInfoWrapper(errorInfo))
                    }
                })
    }.subscribeOn(Schedulers.io())

    fun createAccessManager(token: String,
                            identity: String,
                            authHeader: String,
                            chatClient: ChatClient): Completable = Flowable.create<String>({
        val accessManager = AccessManager(token, object : AccessManager.Listener {
            override fun onTokenExpired(accessManager: AccessManager?) {
                val newToken = fuelHelper.getTwilioTokenSync(identity, authHeader).token
                accessManager?.updateToken(newToken)
            }

            override fun onTokenWillExpire(accessManager: AccessManager?) {
                val newToken = fuelHelper.getTwilioTokenSync(identity, authHeader).token
                accessManager?.updateToken(newToken)
            }

            override fun onError(accessManager: AccessManager?, errorMessage: String?) {
                it.onError(Throwable(errorMessage))
            }
        })

        accessManager.addTokenUpdateListener { token ->
            it.onNext(token)
        }
        it.setCancellable { }
    }, BackpressureStrategy.BUFFER)
            .flatMapCompletable { newToken ->
                Completable.create({
                    chatClient.updateToken(newToken, object : StatusListener() {
                        override fun onSuccess() {
                            it.onComplete()
                        }

                        override fun onError(errorInfo: ErrorInfo?) {
                            it.onError(ErrorInfoWrapper(errorInfo))
                        }
                    })
                })
            }.observeOn(Schedulers.io())

}