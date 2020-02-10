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

import android.view.View
import android.view.Window
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.virgilsecurity.android.base.data.model.ChannelMeta
import com.virgilsecurity.android.base.data.properties.UserProperties
import com.virgilsecurity.android.base.extension.observe
import com.virgilsecurity.android.base.view.adapter.BaseViewHolder
import com.virgilsecurity.android.base.view.adapter.DelegateAdapter
import com.virgilsecurity.android.base.view.adapter.DelegateAdapterItem
import com.virgilsecurity.android.base.view.adapter.DiffCallback
import com.virgilsecurity.android.base.view.controller.BControllerScope
import com.virgilsecurity.android.common.di.CommonDiConst
import com.virgilsecurity.android.common.util.currentScope
import com.virgilsecurity.android.feature_contacts.R
import com.virgilsecurity.android.feature_contacts.viewmodel.list.ContactsVM
import com.virgilsecurity.android.feature_contacts.viewslice.contacts.list.ContactsSlice
import com.virgilsecurity.android.feature_contacts.viewslice.contacts.list.adapter.ContactItem
import com.virgilsecurity.android.feature_contacts.viewslice.contacts.state.StateSliceContacts
import com.virgilsecurity.android.feature_contacts.viewslice.contacts.toolbar.ToolbarSliceContacts
import org.koin.androidx.viewmodel.scope.viewModel
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
class ContactsController() : BControllerScope() {

    override val layoutResourceId: Int = R.layout.controller_contacts

    private val viewModel: ContactsVM by currentScope.viewModel(this)
    private val diffCallback: DiffCallback<ChannelMeta>
            by inject(named(CommonDiConst.KEY_DIFF_CALLBACK_CHANNEL_META))
    private val userProperties: UserProperties by inject()
    private val itemDecoration: RecyclerView.ItemDecoration by inject()

    private lateinit var openDrawer: () -> Unit
    private lateinit var addContact: () -> Unit
    private lateinit var openChannel: (ChannelMeta) -> Unit
    private lateinit var mldToolbarSlice: MutableLiveData<ToolbarSliceContacts.Action>
    private lateinit var toolbarSlice: ToolbarSliceContacts
    private lateinit var mldContactsSlice: MutableLiveData<ContactsSlice.Action>
    private lateinit var contactsSlice: ContactsSlice
    private lateinit var stateSlice: StateSliceContacts
    private lateinit var adapter: DelegateAdapter<ChannelMeta>

    constructor(openDrawer: () -> Unit,
                addContact: () -> Unit,
                openChannel: (interlocutor: ChannelMeta) -> Unit) : this() {
        this.openDrawer = openDrawer
        this.addContact = addContact
        this.openChannel = openChannel
    }

    override fun init(containerView: View) {
        this.mldToolbarSlice = MutableLiveData()
        this.mldContactsSlice = MutableLiveData()
        val contactItem = ContactItem(mldContactsSlice, userProperties)
            as DelegateAdapterItem<BaseViewHolder<ChannelMeta>, ChannelMeta>
        this.adapter = DelegateAdapter.Builder<ChannelMeta>()
                .add(contactItem)
                .diffCallback(diffCallback)
                .build()
    }

    override fun initViewSlices(window: Window) {
        this.toolbarSlice = ToolbarSliceContacts(mldToolbarSlice)
        val layoutManager = LinearLayoutManager(activity)
        this.contactsSlice = ContactsSlice(mldContactsSlice, adapter, itemDecoration, layoutManager)
        this.stateSlice = StateSliceContacts()

        toolbarSlice.init(lifecycle, window)
        contactsSlice.init(lifecycle, window)
        stateSlice.init(lifecycle, window)
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
    }

    private fun onToolbarActionChanged(action: ToolbarSliceContacts.Action) = when (action) {
        ToolbarSliceContacts.Action.HamburgerClicked -> openDrawer()
        ToolbarSliceContacts.Action.AddClicked -> addContact()
        ToolbarSliceContacts.Action.Idle -> Unit
    }

    private fun onStateChanged(state: ContactsVM.State): Unit = when (state) {
        is ContactsVM.State.ContactsLoaded -> contactsSlice.showContacts(state.contacts)
        ContactsVM.State.ShowEmpty -> stateSlice.showEmpty()
        ContactsVM.State.ShowContent -> stateSlice.showContent()
        ContactsVM.State.ShowLoading -> stateSlice.showLoading()
        ContactsVM.State.ShowError -> stateSlice.showError()
    }

    companion object {
        const val KEY_CONTACTS_CONTROLLER = "KEY_CONTACTS_CONTROLLER"
        private val TAG = ContactsController::class.java.simpleName
    }
}
