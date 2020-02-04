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

import com.twilio.chat.Channel
import com.virgilsecurity.android.base.data.model.ChannelMeta
import com.virgilsecurity.android.base.util.GeneralConstants
import com.virgilsecurity.android.common.data.remote.channels.MapperToChannelInfo
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    8/8/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

/**
 * ChannelsRepositoryDefault
 */
class ChannelsRepositoryDefault(
        private val channelsApi: ChannelsApi,
        private val channelsDao: ChannelsDao,
        private val mapper: MapperToChannelInfo
) : ChannelsRepository {

    private val debounceCache = mutableListOf<ChannelMeta>()

    override fun channels(): Observable<List<ChannelMeta>> =
            Observable.concatArray(channelsDao.getUserChannels().toObservable(),
                                   channelsApi.userChannels()
                                           .flatMap(::joinFetchedChannels)
                                           .flatMap {
                                               channelsDao.addChannels(it)
                                                       .subscribeOn(Schedulers.io())
                                                       .toSingle { it }.toObservable()
                                           })
                    .map { channels ->
                        channels.toMutableList().let {
                            it.removeAll(debounceCache)
                            it
                        }
                    }
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

    @Suppress("UNCHECKED_CAST")
    private fun joinFetchedChannels(userChannels: List<ChannelMeta>): Observable<List<ChannelMeta>> {
        val fetchChannelStreams = mutableListOf<Observable<Channel>>()

        for (channel in userChannels)
            fetchChannelStreams.add(channelsApi.userChannelById(channel.sid).toObservable())

        return Observable.zip(fetchChannelStreams) { channels -> channels.toList() }
                .flatMap { fetchedChannels ->
                    val joinChannelStreams = mutableListOf<Observable<ChannelMeta>>()

                    for (fetchedChannel in fetchedChannels as List<Channel>)
                        if (fetchedChannel.status.value == Channel.ChannelStatus.INVITED.value)
                            joinChannelStreams.add(channelsApi.joinChannel(fetchedChannel).toObservable())

                    Observable.zip(joinChannelStreams) { userChannels }
                }
    }

    override fun observeChannelsChanges(): Flowable<ChannelsApi.ChannelsChanges> =
            channelsApi.observeChannelsChanges()
                    .filter { channelChange ->
                        if (channelChange is ChannelsApi.ChannelsChanges.ChannelInvited) { // Thanks for twilio attributes fun
                            System.currentTimeMillis().let {
                                while (channelChange.channel!!.attributes.toString() == GeneralConstants.EMPTY_ATTRIBUTES ||
                                       (System.currentTimeMillis() - it) < ATTRIBUTES_LOAD_TIMEOUT) {
                                    continue
                                }

                                channelChange.channel!!.attributes[GeneralConstants.KEY_TYPE] == GeneralConstants.TYPE_SINGLE
                            }
                        } else {
                            true
                        }
                    } // TODO While we don't have group chats - after change this
                    .flatMap { change ->
                        when (change) {
                            is ChannelsApi.ChannelsChanges.ChannelInvited -> {
                                Single.just(change.channel!!)
                                        .map(mapper::mapChannel)
                                        .flatMap { channel ->
                                            joinAndAddToDbChannel(change, channel).let { pair ->
                                                Single.zip(pair.first,
                                                           pair.second,
                                                           BiFunction
                                                           { _: ChannelMeta,
                                                             _: ChannelsApi.ChannelsChanges ->
                                                               change
                                                           })
                                            }
                                        }
                                        .toFlowable()
                            }
                            else -> Flowable.just(change)
                        }
                    }

    private fun joinAndAddToDbChannel(change: ChannelsApi.ChannelsChanges.ChannelInvited,
                                      channel: ChannelMeta) =
            (channelsApi.joinChannel(change.channel!!).subscribeOn(Schedulers.io())
                    to
                    channelsDao.addChannel(channel).subscribeOn(Schedulers.io()).toSingle { change })

    override fun getUserChannelById(id: String): Single<Channel> = channelsApi.userChannelById(id)

    companion object {
        const val ATTRIBUTES_LOAD_TIMEOUT = 2500L
    }
}
