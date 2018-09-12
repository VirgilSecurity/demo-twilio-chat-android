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

package com.android.virgilsecurity.feature_channel.domain

import com.android.virgilsecurity.base.data.model.MessageInfo
import com.android.virgilsecurity.base.data.properties.UserProperties
import com.android.virgilsecurity.base.domain.BaseDo
import com.android.virgilsecurity.common.data.helper.virgil.VirgilHelper
import com.android.virgilsecurity.feature_channel.data.model.exception.TooLongMessageException
import com.android.virgilsecurity.feature_channel.data.repository.MessagesRepositoryDefault
import com.virgilsecurity.sdk.crypto.VirgilPublicKey
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
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
 * SendMessageDoDefault
 */
class ShowMessagePreviewDoDefault(
        private val virgilHelper: VirgilHelper,
        private val userProperties: UserProperties
) : BaseDo<ShowMessagePreviewDo.Result>(), ShowMessagePreviewDo {

    override fun execute(body: String,
                         publicKeys: List<VirgilPublicKey>) =
            (if (body.toByteArray(Charset.forName("UTF-8")).size >
                 MessagesRepositoryDefault.MAX_TWILIO_MESSAGE_BODY_SIZE)
                Single.error { TooLongMessageException() }
            else
                Single.just<MessageInfo>(
                    MessageInfo(
                        PREVIEW_SID,
                        PREVIEW_CHANNEL_SID,
                        virgilHelper.encrypt(body, publicKeys),
                        PREVIEW_ATTRIBUTES,
                        userProperties.currentUser!!.identity,
                        false // TODO change when have attachments
                    )
                )).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(::success, ::error)
                    .track()

    private fun success(message: MessageInfo) {
        liveData.value = ShowMessagePreviewDo.Result.OnSuccess(message)
    }

    private fun error(throwable: Throwable) {
        if (throwable is TooLongMessageException)
            liveData.value = ShowMessagePreviewDo.Result.MessageIsTooLong
        else
            liveData.value = ShowMessagePreviewDo.Result.OnError(throwable)
    }

    companion object {
        const val PREVIEW_SID = "TEMP_SID"
        const val PREVIEW_CHANNEL_SID = "TEMP_CHANNEL_SID"
        const val PREVIEW_ATTRIBUTES = "TEMP_ATTRIBUTES"
    }
}