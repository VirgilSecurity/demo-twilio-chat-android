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

package com.android.virgilsecurity.feature_contacts.di

import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.android.virgilsecurity.base.data.model.ChannelInfo
import com.android.virgilsecurity.base.view.adapter.DelegateAdapter
import com.android.virgilsecurity.base.view.adapter.DelegateAdapterItem
import com.android.virgilsecurity.base.view.adapter.DelegateAdapterItemDefault
import com.android.virgilsecurity.common.viewslice.StateSliceEmptyable
import com.android.virgilsecurity.feature_contacts.data.interactor.AddContactInteractor
import com.android.virgilsecurity.feature_contacts.data.interactor.AddContactInteractorDefault
import com.android.virgilsecurity.feature_contacts.data.repository.ContactsRepository
import com.android.virgilsecurity.feature_contacts.data.repository.ContactsRepositoryDefault
import com.android.virgilsecurity.feature_contacts.di.Const.CONTEXT_ADD_CONTACT
import com.android.virgilsecurity.feature_contacts.di.Const.CONTEXT_CONTACTS
import com.android.virgilsecurity.feature_contacts.di.Const.ITEM_ADAPTER_CONTACT
import com.android.virgilsecurity.feature_contacts.di.Const.LD_LIST_CONTACTS
import com.android.virgilsecurity.feature_contacts.di.Const.LD_TOOLBAR_ADD_CONTACT
import com.android.virgilsecurity.feature_contacts.di.Const.LD_TOOLBAR_CONTACTS
import com.android.virgilsecurity.feature_contacts.di.Const.MLD_ADD_CONTACT
import com.android.virgilsecurity.feature_contacts.di.Const.MLD_CONTACTS
import com.android.virgilsecurity.feature_contacts.di.Const.STATE_CONTACTS
import com.android.virgilsecurity.feature_contacts.di.Const.TOOLBAR_ADD_CONTACT
import com.android.virgilsecurity.feature_contacts.di.Const.TOOLBAR_CONTACTS_LIST
import com.android.virgilsecurity.feature_contacts.domain.addContact.AddContactDo
import com.android.virgilsecurity.feature_contacts.domain.addContact.AddContactsDoDefault
import com.android.virgilsecurity.feature_contacts.domain.list.*
import com.android.virgilsecurity.feature_contacts.viewmodel.addContact.AddContactVM
import com.android.virgilsecurity.feature_contacts.viewmodel.addContact.AddContactVMDefault
import com.android.virgilsecurity.feature_contacts.viewmodel.list.ContactsVM
import com.android.virgilsecurity.feature_contacts.viewmodel.list.ContactsVMDefault
import com.android.virgilsecurity.feature_contacts.viewslice.addContact.state.StateSliceAddContact
import com.android.virgilsecurity.feature_contacts.viewslice.addContact.state.StateSliceAddContactDefault
import com.android.virgilsecurity.feature_contacts.viewslice.addContact.toolbar.ToolbarSlice
import com.android.virgilsecurity.feature_contacts.viewslice.addContact.toolbar.ToolbarSliceAddContact
import com.android.virgilsecurity.feature_contacts.viewslice.contacts.list.ContactsSlice
import com.android.virgilsecurity.feature_contacts.viewslice.contacts.list.ContactsSliceDefault
import com.android.virgilsecurity.feature_contacts.viewslice.contacts.list.adapter.ContactItem
import com.android.virgilsecurity.feature_contacts.viewslice.contacts.state.StateSliceContacts
import com.android.virgilsecurity.feature_contacts.viewslice.contacts.toolbar.ToolbarSliceContacts
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    7/18/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

/**
 * ContactsModules
 */

val contactsModule: Module = applicationContext {
    bean { ContactsRepositoryDefault(get(), get()) as ContactsRepository }
    bean(STATE_CONTACTS) { StateSliceContacts() as StateSliceEmptyable }

    context(CONTEXT_CONTACTS) {
        bean(LD_TOOLBAR_CONTACTS) { MutableLiveData<ToolbarSlice.Action>() }
        bean(TOOLBAR_CONTACTS_LIST) { ToolbarSliceContacts(get(LD_TOOLBAR_CONTACTS)) as com.android.virgilsecurity.feature_contacts.viewslice.contacts.toolbar.ToolbarSlice }

        bean(LD_LIST_CONTACTS) { MutableLiveData<ContactsSlice.Action>() }
        bean(ITEM_ADAPTER_CONTACT) { ContactItem(get(LD_LIST_CONTACTS), get()) as DelegateAdapterItem<DelegateAdapterItemDefault.KViewHolder<ChannelInfo>, ChannelInfo> }
        bean {
            DelegateAdapter.Builder<ChannelInfo>()
                    .add(get(ITEM_ADAPTER_CONTACT))
                    .diffCallback(get())
                    .build()
        }

        bean { LinearLayoutManager(get()) as RecyclerView.LayoutManager }
        bean { ContactsSliceDefault(get(LD_LIST_CONTACTS), get(), get(), get()) as ContactsSlice }

        bean { GetContactsDoDefault(get()) as GetContactsDo }
        bean(MLD_CONTACTS) { MediatorLiveData<ContactsVM.State>() }
        bean { ObserveContactsChangesDoDefault(get()) as ObserveContactsChangesDo }
        bean { JoinChannelDoDefault(get()) as JoinChannelDo }
        bean { ContactsVMDefault(get(MLD_CONTACTS), get(), get(), get()) as ContactsVM }
    }
}

val addContactModule: Module = applicationContext {
    bean { AddContactInteractorDefault(get(), get(), get()) as AddContactInteractor }
    bean { StateSliceAddContactDefault() as StateSliceAddContact }

    context(CONTEXT_ADD_CONTACT) {
        bean { AddContactsDoDefault(get()) as AddContactDo }
        bean(MLD_ADD_CONTACT) { MediatorLiveData<AddContactVM.State>() }
        bean { AddContactVMDefault(get(MLD_ADD_CONTACT), get()) as AddContactVM }

        bean(LD_TOOLBAR_ADD_CONTACT) { MutableLiveData<ToolbarSlice.Action>() }
        bean(TOOLBAR_ADD_CONTACT) { ToolbarSliceAddContact(get(LD_TOOLBAR_ADD_CONTACT)) as ToolbarSlice }
    }
}

object Const {
    const val LD_TOOLBAR_CONTACTS = "LD_TOOLBAR_CONTACTS"
    const val LD_LIST_CONTACTS = "LD_LIST_CONTACTS"
    const val MLD_CONTACTS = "MLD_CONTACTS"
    const val STATE_CONTACTS = "STATE_CONTACTS"
    const val ITEM_ADAPTER_CONTACT = "ITEM_ADAPTER_CONTACT"
    const val MLD_ADD_CONTACT = "MLD_ADD_CONTACT"
    const val LD_TOOLBAR_ADD_CONTACT = "LD_TOOLBAR_ADD_CONTACT"
    const val TOOLBAR_CONTACTS_LIST = "TOOLBAR_CONTACTS_LIST"
    const val TOOLBAR_ADD_CONTACT = "TOOLBAR_ADD_CONTACT"

    const val CONTEXT_CONTACTS = "CONTEXT_CONTACTS"
    const val CONTEXT_ADD_CONTACT = "CONTEXT_ADD_CONTACT"
}