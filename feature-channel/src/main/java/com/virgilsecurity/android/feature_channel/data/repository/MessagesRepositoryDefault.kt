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

package com.virgilsecurity.android.feature_channel.data.repository

import com.twilio.chat.Channel
import com.virgilsecurity.android.base.data.api.MessagesApi
import com.virgilsecurity.android.base.data.dao.MessagesDao
import com.virgilsecurity.android.base.data.model.MessageInfo
import com.virgilsecurity.android.base.extension.comparableListEqual
import com.virgilsecurity.android.common.data.remote.messages.MapperToMessageInfo
import com.virgilsecurity.android.feature_channel.data.model.exception.TooLongMessageException
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import java.nio.charset.Charset

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    8/9/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

/**
 * MessagesRepositoryDefault
 */
class MessagesRepositoryDefault(
        private val messagesApi: MessagesApi,
        private val messagesDao: MessagesDao,
        private val mapper: MapperToMessageInfo
) : MessagesRepository {

    private val debounceCache = mutableListOf<MessageInfo>()

    override fun messages(channel: Channel): Observable<List<MessageInfo>> =
            Observable.concatArray(messagesDao.messages(channel.sid).toObservable(),
                                   fetchMessages(channel).flatMap {
                                       messagesDao.addMessages(it)
                                               .subscribeOn(Schedulers.io())
                                               .toSingle { it }.toObservable()
                                   })
                    .filter {
                        !(it comparableListEqual debounceCache) && it.isNotEmpty()
                    }
                    .doOnNext {
                        debounceCache.addAll(it)
                    }
                    .doOnComplete {
                        debounceCache.clear()
                    }

    private fun fetchMessages(channel: Channel) =
            Observable.zip(messagesApi.messagesCount(channel).toObservable(),
                           messagesDao.messagesCount(channel.sid).toObservable(),
                           BiFunction { remoteCount: Long, localCount: Int -> remoteCount to localCount })
                    .filter {
                        it.first > it.second.toLong()
                    }
                    .flatMap {
                        if (it.first - it.second.toLong() > MAX_TWILIO_QUEUE_SIZE)
                            throw Throwable("Too many un-fetched messages );") // Need to add pagination in the future

                        messagesApi.messagesAfter(channel,
                                                  it.second.toLong(),
                                                  (it.first - it.second.toLong()).toInt())
                                .toObservable()
                    }

    override fun observeChannelChanges(channel: Channel): Flowable<MessagesApi.ChannelChanges> =
            messagesApi.observeChannelChanges(channel)
                    .flatMap { change ->
                        when (change) {
                            is MessagesApi.ChannelChanges.MessageAdded -> {
                                Single.just(change.message)
                                        .map(mapper::mapMessage)
                                        .flatMap { messageInfo ->
                                            messagesDao.addMessage(messageInfo)
                                                    .subscribeOn(Schedulers.io())
                                                    .toSingle { change }
                                        }
                                        .toFlowable()
                            }
                            else -> Flowable.just(change)
                        }
                    }

    override fun sendMessage(channel: Channel, body: String): Completable =
            if (body.toByteArray(Charset.forName("UTF-8")).size > MAX_TWILIO_MESSAGE_BODY_SIZE)
                Completable.error { TooLongMessageException() }
            else
                messagesApi.sendMessage(channel, body)
                        .flatMapCompletable {
                            messagesDao.addMessage(it)
                                    .subscribeOn(Schedulers.io())
                        }

    companion object {
        const val MAX_TWILIO_QUEUE_SIZE = 10000
        const val MAX_TWILIO_MESSAGE_BODY_SIZE = 32000 // 32Kb
    }
}