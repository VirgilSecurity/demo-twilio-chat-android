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

package com.virgilsecurity.android.feature_contacts.view

import android.annotation.SuppressLint
import android.view.View
import com.twilio.chat.Channel
import com.virgilsecurity.android.base.data.api.ChannelsApi
import com.virgilsecurity.android.base.data.model.ChannelInfo
import com.virgilsecurity.android.base.extension.observe
import com.virgilsecurity.android.base.view.BaseControllerWithScope
import com.virgilsecurity.android.common.data.remote.channels.MapperToChannelInfo
import com.virgilsecurity.android.common.viewslice.StateSliceEmptyable
import com.virgilsecurity.android.feature_contacts.R
import com.virgilsecurity.android.feature_contacts.di.Const.STATE_CONTACTS
import com.virgilsecurity.android.feature_contacts.di.Const.VM_CONTACTS
import com.virgilsecurity.android.feature_contacts.viewmodel.list.ContactsVM
import com.virgilsecurity.android.feature_contacts.viewslice.contacts.list.ContactsSlice
import com.virgilsecurity.android.feature_contacts.viewslice.contacts.toolbar.ToolbarSlice
import io.reactivex.Single
import org.koin.core.inject
import org.koin.core.qualifier.named

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    7/12/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

/**
 * ContactsController
 */
class ContactsController() : BaseControllerWithScope() {

    override val layoutResourceId: Int = R.layout.controller_contacts

    private val toolbarSlice: ToolbarSlice by inject()
    private val contactsSlice: ContactsSlice by inject()
    private val stateSlice: StateSliceEmptyable by inject(named(STATE_CONTACTS))
    private val viewModel: ContactsVM by inject(named(VM_CONTACTS))
    private val mapper: MapperToChannelInfo by inject()

    private lateinit var openDrawer: () -> Unit
    private lateinit var addContact: () -> Unit
    private lateinit var openChannel: (ChannelInfo) -> Unit

    constructor(openDrawer: () -> Unit,
                addContact: () -> Unit,
                openChannel: (interlocutor: ChannelInfo) -> Unit) : this() {
        this.openDrawer = openDrawer
        this.addContact = addContact
        this.openChannel = openChannel
    }

    override fun init() {}

    override fun initViewSlices(view: View) {
        toolbarSlice.init(lifecycle, view)
        contactsSlice.init(lifecycle, view)
        stateSlice.init(lifecycle, view)
    }

    override fun setupViewSlices(view: View) {}

    override fun setupVSActionObservers() {
        observe(toolbarSlice.getAction(), ::onToolbarActionChanged)
        observe(contactsSlice.getAction(), ::onContactsActionChanged)
    }

    private fun onContactsActionChanged(action: ContactsSlice.Action) = when (action) {
        is ContactsSlice.Action.ContactClicked -> openChannel(action.contact)
        ContactsSlice.Action.Idle -> Unit
    }

    override fun setupVMStateObservers() {
        observe(viewModel.getState(), ::onStateChanged)
    }

    override fun initData() {
        viewModel.contacts()
        viewModel.observeContactsChanges()
    }

    private fun onToolbarActionChanged(action: ToolbarSlice.Action) = when (action) {
        ToolbarSlice.Action.HamburgerClicked -> openDrawer()
        ToolbarSlice.Action.AddClicked -> addContact()
        ToolbarSlice.Action.Idle -> Unit
    }

    private fun onStateChanged(state: ContactsVM.State): Unit = when (state) {
        is ContactsVM.State.ContactsLoaded -> contactsSlice.showContacts(state.contacts)
        ContactsVM.State.ShowEmpty -> stateSlice.showEmpty()
        ContactsVM.State.ShowContent -> stateSlice.showContent()
        ContactsVM.State.ShowLoading -> stateSlice.showLoading()
        ContactsVM.State.ShowError -> stateSlice.showError()
        is ContactsVM.State.ContactChanged -> onContactsChanged(state.change)
    }

    private fun onContactsChanged(change: ChannelsApi.ChannelsChanges) = when(change) {
        is ChannelsApi.ChannelsChanges.ChannelDeleted -> Unit
        is ChannelsApi.ChannelsChanges.InvitedToChannelNotification -> Unit
        is ChannelsApi.ChannelsChanges.ClientSynchronization -> Unit
        ChannelsApi.ChannelsChanges.NotificationSubscribed -> Unit
        is ChannelsApi.ChannelsChanges.UserSubscribed -> Unit
        is ChannelsApi.ChannelsChanges.ChannelUpdated -> Unit
        is ChannelsApi.ChannelsChanges.RemovedFromChannelNotification -> Unit
        is ChannelsApi.ChannelsChanges.NotificationFailed -> Unit
        is ChannelsApi.ChannelsChanges.ChannelJoined -> Unit
        is ChannelsApi.ChannelsChanges.ChannelAdded -> Unit
        is ChannelsApi.ChannelsChanges.ChannelSynchronizationChange -> Unit
        is ChannelsApi.ChannelsChanges.UserUnsubscribed -> Unit
        is ChannelsApi.ChannelsChanges.AddedToChannelNotification -> Unit
        is ChannelsApi.ChannelsChanges.ChannelInvited -> showChannel(change.channel!!)
        is ChannelsApi.ChannelsChanges.NewMessageNotification -> Unit
        is ChannelsApi.ChannelsChanges.ConnectionStateChange -> Unit
        is ChannelsApi.ChannelsChanges.Error -> Unit
        is ChannelsApi.ChannelsChanges.UserUpdated -> Unit
        is ChannelsApi.ChannelsChanges.Exception -> Unit
    }

    @SuppressLint("CheckResult")
    private fun showChannel(channel: Channel) {
        Single.just(channel)
                .map(mapper::mapChannel)
                .subscribe { channelInfo ->
                    contactsSlice.addContact(channelInfo)
                    stateSlice.showContent()
                }
    }

    companion object {
        const val KEY_CONTACTS_CONTROLLER = "KEY_CONTACTS_CONTROLLER"
        private val TAG = ContactsController::class.java.simpleName
    }
}
