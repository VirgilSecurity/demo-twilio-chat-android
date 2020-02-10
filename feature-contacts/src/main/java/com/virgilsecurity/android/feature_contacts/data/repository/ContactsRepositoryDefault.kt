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

package com.virgilsecurity.android.feature_contacts.data.repository

import com.virgilsecurity.android.base.data.dao.ChannelsDao
import com.virgilsecurity.android.base.data.model.ChannelMeta
import com.virgilsecurity.android.base.data.properties.UserProperties
import com.virgilsecurity.android.common.data.helper.virgil.VirgilHelper
import com.virgilsecurity.android.common.data.remote.channels.ChannelIdGenerator
import io.reactivex.Flowable
import io.reactivex.Single

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    8/3/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

/**
 * ContactsRepositoryDefault
 */
class ContactsRepositoryDefault(
        private val contactsDao: ChannelsDao, // TODO In the future here will be contacts, not channels
        private val userProperties: UserProperties,
        private val channelIdGenerator: ChannelIdGenerator
) : ContactsRepository {

    override fun addContact(interlocutor: String): Single<ChannelMeta> =
            Single.create {
                try {
                    val currentUserIdentity = userProperties.currentUser!!.identity
                    val channelId = channelIdGenerator.generatedChannelId(currentUserIdentity,
                                                                          interlocutor)
                    val channelMeta = ChannelMeta(channelId, currentUserIdentity, interlocutor)

                    contactsDao.addChannel(channelMeta)

                    it.onSuccess(channelMeta)
                } catch (throwable: Throwable) {
                    it.onError(throwable)
                }
            }


//    override fun addContact(interlocutor: String): Single<ChannelMeta> =
//            contactsDao.user(userProperties.currentUser!!.identity, interlocutor)
//                    .flatMap {
//                        if (it.isNotEmpty())
//                            throw AddingUserThatExistsException()
//                        else
//                            virgilHelper.searchCards(interlocutor)
//                    }
//                    .flatMap { cards ->
//                        when {
//                            cards.isEmpty() -> throw EmptyCardsException()
//                            cards.size > 1 -> throw ManyCardsException()
//                            else -> {
//                                contactsApi.createChannel(userProperties.currentUser!!.identity,
//                                                          interlocutor)
//                                        .flatMap {
//                                            contactsDao.addChannel(it)
//                                                    .subscribeOn(Schedulers.io())
//                                                    .toSingle { it }
//                                        }
//                            }
//                        }
//                    }

    override fun contacts(): Flowable<List<ChannelMeta>> = contactsDao.getUserChannels()

}