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

package com.virgilsecurity.android.feature_contacts.viewslice.contacts.list.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.virgilsecurity.android.base.data.model.ChannelMeta
import com.virgilsecurity.android.base.data.properties.UserProperties
import com.virgilsecurity.android.base.view.adapter.DelegateAdapterItemDefault
import com.virgilsecurity.android.common.util.UiUtils
import com.virgilsecurity.android.common.util.UserUtils
import com.virgilsecurity.android.feature_contacts.R
import com.virgilsecurity.android.feature_contacts.viewslice.contacts.list.ContactsSlice

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    7/27/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

/**
 * ContactItem
 */
class ContactItem(
        private val actionLiveData: MutableLiveData<ContactsSlice.Action>,
        private val userProperties: UserProperties,
        override val layoutResourceId: Int = R.layout.item_contact
) : DelegateAdapterItemDefault<ChannelMeta>() {

    override fun onBind(item: ChannelMeta, viewHolder: DelegateAdapterItemDefault.KViewHolder<ChannelMeta>) =
            with(viewHolder.containerView) {
                val tvUsernameContact = findViewById<TextView>(R.id.tvUsernameContact)
                val tvInitialsContact = findViewById<TextView>(R.id.tvInitialsContact)
                val ivUserPicContact = findViewById<ImageView>(R.id.ivUserPicContact)

                tvUsernameContact.text = item.localizedInterlocutor(userProperties)
                tvInitialsContact.text =
                        UserUtils.firstInitials(item.localizedInterlocutor(userProperties))

                Glide.with(context)
                        .load(UiUtils.letterBasedDrawable(context, R.array.loginBackgrounds,
                                                          tvInitialsContact.text[0]
                                                                  .toLowerCase()
                                                                  .toString()))
                        .apply(RequestOptions.circleCropTransform())
                        .into(ivUserPicContact)

                setOnClickListener {
                    actionLiveData.value = ContactsSlice.Action.ContactClicked(item)
                    actionLiveData.value = ContactsSlice.Action.Idle
                }

                tvInitialsContact.visibility = View.VISIBLE
            }

    override fun onRecycled(holder: DelegateAdapterItemDefault.KViewHolder<ChannelMeta>) {}

    override fun isForViewType(items: List<*>, position: Int): Boolean =
            items[position] is ChannelMeta
}
