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

package com.android.virgilsecurity.feature_contacts.data.repository

import com.android.virgilsecurity.base.data.api.ChannelsApi
import com.android.virgilsecurity.base.data.dao.ChannelsDao
import com.android.virgilsecurity.base.data.model.ChannelInfo
import com.android.virgilsecurity.base.data.properties.UserProperties
import com.android.virgilsecurity.base.extension.comparableListEqual
import com.android.virgilsecurity.common.data.exception.EmptyCardsException
import com.android.virgilsecurity.common.data.exception.ManyCardsException
import com.android.virgilsecurity.common.data.helper.virgil.VirgilHelper
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import kotlin.collections.contentEquals

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
        private val contactsApi: ChannelsApi, // In the future here will be contacts, not channels
        private val contactsDao: ChannelsDao,
        private val virgilHelper: VirgilHelper,
        private val userProperties: UserProperties
) : ContactsRepository {

    private val debounceCache = mutableListOf<ChannelInfo>()

    override fun addContact(interlocutor: String): Single<ChannelInfo> =
            virgilHelper.searchCards(interlocutor)
                    .flatMap { cards ->
                        when {
                            cards.isEmpty() -> throw EmptyCardsException()
                            cards.size > 1 -> throw ManyCardsException()
                            else -> {
                                contactsApi.createChannel(userProperties.currentUser!!.identity,
                                                          interlocutor)
                                        .flatMap {
                                            contactsDao.addChannel(it)
                                                    .subscribeOn(Schedulers.io())
                                                    .toSingle { it }
                                        }
                            }
                        }
                    }

    override fun contacts(): Observable<List<ChannelInfo>> =
            Observable.concatArray(contactsDao.getUserChannels().toObservable(),
                                   contactsApi.userChannels().flatMap {
                                       contactsDao.addChannels(it)
                                               .subscribeOn(Schedulers.io())
                                               .toSingle { it }.toObservable()
                                   })
                    .map {
                        it.sortedBy { channel -> channel.sid }
                    }
                    .filter {
                        !(it comparableListEqual debounceCache) && it.isNotEmpty()
                    }
                    .doOnNext {
                        debounceCache.addAll(it)
                    }
                    .doOnComplete {
                        debounceCache.clear()
                    }

    override fun observeChannelsChanges(): Flowable<ChannelsApi.ChannelsChanges> =
            contactsApi.observeChannelsChanges()
}
