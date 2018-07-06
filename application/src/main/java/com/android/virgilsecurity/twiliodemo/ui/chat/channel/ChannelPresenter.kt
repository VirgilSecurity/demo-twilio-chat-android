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

package com.android.virgilsecurity.twiliodemo.ui.chat.channel

import com.android.virgilsecurity.base.data.api.UserManager
import com.android.virgilsecurity.common.data.remote.twilio.TwilioHelper
import com.android.virgilsecurity.common.data.remote.virgil.VirgilHelper

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    6/2/186/2/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

/**
 * ChannelPresenter
 */

class ChannelPresenter(private val twilioHelper: TwilioHelper,
                       private val virgilHelper: VirgilHelper,
                       private val userManager: UserManager) {

//    private val compositeDisposable = CompositeDisposable()
//
//    fun requestMessages(channel: Channel,
//                        onGetMessagesSuccess: (MutableList<Message>) -> Unit,
//                        onGetMessagesError: (Throwable) -> Unit) {
//        val getMessagesDisposable =
//                twilioHelper.getMessages(channel)
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribeBy(
//                            onSuccess = {
//                                onGetMessagesSuccess(it)
//                            },
//                            onError = {
//                                onGetMessagesError(it)
//                            }
//                        )
//
//        compositeDisposable += getMessagesDisposable
//    }
//
//    fun requestSendMessage(channel: Channel,
//                           interlocutor: String,
//                           body: String,
//                           interlocutorCard: Card,
//                           onSendMessagesSuccess: (Message) -> Unit,
//                           onSendMessagesError: (Throwable) -> Unit) {
//
//        val publicKeys = ArrayList<VirgilPublicKey>()
//
//        publicKeys.add(userManager.getUserCard().publicKey as VirgilPublicKey)
//        publicKeys.add(interlocutorCard.publicKey as VirgilPublicKey)
//
//        val encryptedText = virgilHelper.encrypt(body, publicKeys)
//
//        twilioHelper.sendMessage(channel, encryptedText, interlocutor)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeBy(
//                    onSuccess = {
//                        onSendMessagesSuccess(it)
//                    },
//                    onError = {
//                        onSendMessagesError(it)
//                    }
//                )
//    }
//
//    fun requestSearchCard(identity: String,
//                          onCardSearchSuccess: (Card) -> Unit,
//                          onCardSearchError: (Throwable) -> Unit) {
//        val searchCardDisposable = virgilHelper.searchCards(identity)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({ cards, throwable ->
//                               if (throwable == null && cards.isNotEmpty())
//                                   onCardSearchSuccess(cards[0])
//                               else
//                                   onCardSearchError(throwable)
//                           })
//
//        compositeDisposable.add(searchCardDisposable)
//    }
//
//    override fun disposeAll() {
//        compositeDisposable.clear()
//    }
//
//    fun requestGetChannelBySid(sid: String,
//                               onGetChannelSuccess: (Channel) -> Unit,
//                               onGetChannelError: (Throwable) -> Unit) {
//        twilioHelper.getChannelBySid(sid)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeBy(
//                    onSuccess = {
//                        onGetChannelSuccess(it)
//                    },
//                    onError = {
//                        onGetChannelError(it)
//                    }
//                )
//    }
}