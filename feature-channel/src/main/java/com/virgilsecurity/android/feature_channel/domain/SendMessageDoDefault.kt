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

package com.virgilsecurity.android.feature_channel.domain

import com.virgilsecurity.android.base.data.model.ChannelMeta
import com.virgilsecurity.android.base.domain.BaseDo
import com.virgilsecurity.android.common.data.helper.virgil.VirgilHelper
import com.virgilsecurity.android.feature_channel.data.model.exception.TooLongMessageException
import com.virgilsecurity.android.feature_channel.data.repository.MessagesRepository
import com.virgilsecurity.sdk.crypto.VirgilPublicKey
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

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
 * SendMessageDoDefault
 */
class SendMessageDoDefault(
        private val messagesRepository: MessagesRepository,
        private val virgilHelper: VirgilHelper
) : BaseDo<SendMessageDo.Result>(), SendMessageDo {

    override fun execute(channel: ChannelMeta,
                         body: String,
                         publicKeys: List<VirgilPublicKey>) =
            messagesRepository.sendMessage(channel,
                                           virgilHelper.encrypt(body, publicKeys))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(::success, ::error)
                    .track()

    private fun success() {
        liveData.value = SendMessageDo.Result.OnSuccess
    }

    private fun error(throwable: Throwable) {
        if (throwable is TooLongMessageException)
            liveData.value = SendMessageDo.Result.MessageIsTooLong
        else
            liveData.value = SendMessageDo.Result.OnError(throwable)
    }
}