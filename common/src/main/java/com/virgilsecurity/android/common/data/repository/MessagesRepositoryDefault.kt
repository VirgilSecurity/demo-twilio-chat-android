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

package com.virgilsecurity.android.common.data.repository

import com.virgilsecurity.android.base.data.api.MessagesApi
import com.virgilsecurity.android.base.data.dao.MessagesDao
import com.virgilsecurity.android.base.data.model.ChannelMeta
import com.virgilsecurity.android.base.data.model.MessageMeta
import com.virgilsecurity.android.common.data.model.exception.TooLongMessageException
import com.virgilsecurity.sdk.utils.ConvertionUtils
import io.reactivex.Completable
import io.reactivex.Flowable
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
        private val messagesDao: MessagesDao,
        private val messagesApi: MessagesApi
) : MessagesRepository {

    override fun messages(channelMeta: ChannelMeta): Flowable<List<MessageMeta>> =
            messagesDao.messages(channelMeta)

    override fun sendMessage(channelMeta: ChannelMeta, body: String): Completable =
            if (body.toByteArray(Charset.forName("UTF-8")).size > MAX_MESSAGE_BODY_SIZE)
                Completable.error { TooLongMessageException() }
            else {
                val time = System.currentTimeMillis()
                val data = ConvertionUtils.toBase64String(ConvertionUtils.serializeToJson(mapOf(
                        "date" to time,
                        "ciphertext" to body
                )))

                messagesApi.sendMessage(channelMeta, data)
                        .flatMapCompletable {
                            messagesDao.addMessage(it)
                                    .subscribeOn(Schedulers.io())
                        }
            }

    override fun observeChatMessages(): Flowable<Pair<ChannelMeta, MessageMeta>> =
            messagesApi.observeChatMessages()
                    .flatMap {
                        messagesDao.addMessage(it.second).andThen(Flowable.just(it))
                    }

    companion object {
        const val MAX_MESSAGE_BODY_SIZE = 32000 // 32Kb
    }
}
