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

package com.android.virgilsecurity.twiliodemo.ui.chat.channelsList

import com.android.virgilsecurity.twiliodemo.data.local.UserManager
import com.android.virgilsecurity.twiliodemo.data.remote.twilio.TwilioHelper
import com.android.virgilsecurity.twiliodemo.data.remote.virgil.VirgilHelper
import com.android.virgilsecurity.twiliodemo.ui.base.BasePresenter
import com.twilio.chat.Channel
import com.twilio.chat.ChatClient
import com.twilio.chat.ChatClientListener
import com.virgilsecurity.sdk.crypto.HashAlgorithm
import com.virgilsecurity.sdk.utils.ConvertionUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    6/1/186/1/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

/**
 * ChannelsListPresenter
 */
class ChannelsListPresenter(private val twilioHelper: TwilioHelper,
                            private val virgilHelper: VirgilHelper,
                            private val userManager: UserManager) : BasePresenter {

    private var chatClient: ChatClient? = null
    private val compositeDisposable = CompositeDisposable()

    fun startChatClient(identity: String,
                        onStartClientSuccess: () -> Unit,
                        onStartClientError: (Throwable) -> Unit) {
        val startChatClientDisposable =
                twilioHelper.createChatClient(identity,
                                              {
                                                  virgilHelper.generateAuthHeader()
                                              })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeBy(
                            onComplete = {
                                onStartClientSuccess()
                            },
                            onError = {
                                onStartClientError(it)
                            })

        compositeDisposable += startChatClientDisposable
    }

    fun fetchChannels(identity: String? = "",
                      onFetchChannelsSuccess: (List<Channel>?) -> Unit,
                      onFetchChannelsError: ((Throwable) -> Unit)? = null) {
        val channels = chatClient?.channels?.subscribedChannels
        onFetchChannelsSuccess(channels)
    }

    fun createChannel(interlocutor: String,
                      onCreateChannelSuccess: (Channel) -> Unit,
                      onCreateChannelError: (Throwable) -> Unit) {
        twilioHelper.createChannel(interlocutor, generateNewChannelId(interlocutor))
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onSuccess = {
                        onCreateChannelSuccess(it)
                    },
                    onError = {
                        onCreateChannelError(it)
                    })
    }


    private fun generateNewChannelId(interlocutor: String): String {
        val userMe = userManager.getCurrentUser()!!.identity
        val concatenatedHashedUsersData: ByteArray

        concatenatedHashedUsersData = if (userMe >= interlocutor) {
            virgilHelper.virgilCrypto
                    .generateHash((userMe + interlocutor).toByteArray(),
                                  HashAlgorithm.SHA256)
        } else {
            virgilHelper.virgilCrypto
                    .generateHash((interlocutor + userMe).toByteArray(),
                                  HashAlgorithm.SHA256)
        }

        return ConvertionUtils.toHex(concatenatedHashedUsersData).toLowerCase()
    }

    override fun disposeAll() {
        compositeDisposable.clear()
    }

    fun setupChatListener(chatClientListener: ChatClientListener) {
        twilioHelper.setChatListener(chatClientListener)
    }

}