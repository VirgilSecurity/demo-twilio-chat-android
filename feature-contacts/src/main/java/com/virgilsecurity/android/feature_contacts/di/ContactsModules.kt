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

package com.virgilsecurity.android.feature_contacts.di

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.virgilsecurity.android.base.data.model.ChannelMeta
import com.virgilsecurity.android.base.view.adapter.DelegateAdapter
import com.virgilsecurity.android.base.view.adapter.DelegateAdapterItem
import com.virgilsecurity.android.base.view.adapter.DelegateAdapterItemDefault
import com.virgilsecurity.android.common.di.CommonDiConst.KEY_DIFF_CALLBACK_CHANNEL_META
import com.virgilsecurity.android.common.viewslice.StateSliceEmptyable
import com.virgilsecurity.android.feature_contacts.data.repository.ContactsRepository
import com.virgilsecurity.android.feature_contacts.data.repository.ContactsRepositoryDefault
import com.virgilsecurity.android.feature_contacts.di.Const.ADAPTER_CONTACTS
import com.virgilsecurity.android.feature_contacts.di.Const.ITEM_ADAPTER_CONTACT
import com.virgilsecurity.android.feature_contacts.di.Const.LD_LIST_CONTACTS
import com.virgilsecurity.android.feature_contacts.di.Const.LD_TOOLBAR_ADD_CONTACT
import com.virgilsecurity.android.feature_contacts.di.Const.LD_TOOLBAR_CONTACTS
import com.virgilsecurity.android.feature_contacts.di.Const.MLD_CONTACTS
import com.virgilsecurity.android.feature_contacts.di.Const.STATE_CONTACTS
import com.virgilsecurity.android.feature_contacts.di.Const.VM_ADD_CONTACT
import com.virgilsecurity.android.feature_contacts.di.Const.VM_CONTACTS
import com.virgilsecurity.android.feature_contacts.domain.addContact.AddContactDo
import com.virgilsecurity.android.feature_contacts.domain.addContact.AddContactsDoDefault
import com.virgilsecurity.android.feature_contacts.domain.list.GetContactsDo
import com.virgilsecurity.android.feature_contacts.domain.list.GetContactsDoDefault
import com.virgilsecurity.android.feature_contacts.domain.list.ObserveContactsChangesDo
import com.virgilsecurity.android.feature_contacts.domain.list.ObserveContactsChangesDoDefault
import com.virgilsecurity.android.feature_contacts.viewmodel.addContact.AddContactVM
import com.virgilsecurity.android.feature_contacts.viewmodel.addContact.AddContactVMDefault
import com.virgilsecurity.android.feature_contacts.viewmodel.list.ContactsVM
import com.virgilsecurity.android.feature_contacts.viewmodel.list.ContactsVMDefault
import com.virgilsecurity.android.feature_contacts.viewslice.addContact.state.StateSliceAddContact
import com.virgilsecurity.android.feature_contacts.viewslice.addContact.state.StateSliceAddContactDefault
import com.virgilsecurity.android.feature_contacts.viewslice.addContact.toolbar.ToolbarSlice
import com.virgilsecurity.android.feature_contacts.viewslice.addContact.toolbar.ToolbarSliceAddContact
import com.virgilsecurity.android.feature_contacts.viewslice.contacts.list.ContactsSlice
import com.virgilsecurity.android.feature_contacts.viewslice.contacts.list.ContactsSliceDefault
import com.virgilsecurity.android.feature_contacts.viewslice.contacts.list.adapter.ContactItem
import com.virgilsecurity.android.feature_contacts.viewslice.contacts.state.StateSliceContacts
import com.virgilsecurity.android.feature_contacts.viewslice.contacts.toolbar.ToolbarSliceContacts
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

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

val contactsModule: Module = module {
    single { ContactsRepositoryDefault(get(), get(), get(), get(), get()) as ContactsRepository }
    single(named(STATE_CONTACTS)) { StateSliceContacts() as StateSliceEmptyable }

    factory(named(LD_TOOLBAR_CONTACTS)) { MutableLiveData<ToolbarSlice.Action>() }
    factory {
        ToolbarSliceContacts(get(named(LD_TOOLBAR_CONTACTS)))
                as com.virgilsecurity.android.feature_contacts.viewslice.contacts.toolbar.ToolbarSlice
    }

    factory(named(LD_LIST_CONTACTS)) { MutableLiveData<ContactsSlice.Action>() }
    factory(named(ITEM_ADAPTER_CONTACT)) {
        ContactItem(get(named(LD_LIST_CONTACTS)), get())
                as DelegateAdapterItem<DelegateAdapterItemDefault.KViewHolder<ChannelMeta>, ChannelMeta>
    }

    factory(named(ADAPTER_CONTACTS)) {
        DelegateAdapter.Builder<ChannelMeta>()
                .add(get(named(ITEM_ADAPTER_CONTACT)))
                .diffCallback(get(named(KEY_DIFF_CALLBACK_CHANNEL_META)))
                .build()
    }
    factory {
        ContactsSliceDefault(get(named(LD_LIST_CONTACTS)),
                             get(named(ADAPTER_CONTACTS)),
                             get(),
                             get()) as ContactsSlice
    }

    factory { GetContactsDoDefault(get()) as GetContactsDo }
    factory { ObserveContactsChangesDoDefault(get()) as ObserveContactsChangesDo }

    module {
        factory(named(MLD_CONTACTS)) { MediatorLiveData<ContactsVM.State>() }
        factory(named(VM_CONTACTS)) {
            ContactsVMDefault(get(named(MLD_CONTACTS)),
                              get(),
                              get()) as ContactsVM
        }
    }
}

val addContactModule: Module = module {
    single { StateSliceAddContactDefault(get()) as StateSliceAddContact }

    factory { AddContactsDoDefault(get()) as AddContactDo }
    factory(named(LD_TOOLBAR_ADD_CONTACT)) { MutableLiveData<ToolbarSlice.Action>() }
    factory { ToolbarSliceAddContact(get(named(LD_TOOLBAR_ADD_CONTACT))) as ToolbarSlice }

    module {
        factory { MediatorLiveData<AddContactVM.State>() }
        factory(named(VM_ADD_CONTACT)) { AddContactVMDefault(get(), get(), get()) as AddContactVM }
    }
}

object Const {
    const val LD_TOOLBAR_CONTACTS = "LD_TOOLBAR_CONTACTS"
    const val LD_LIST_CONTACTS = "LD_LIST_CONTACTS"
    const val MLD_CONTACTS = "MLD_CONTACTS"
    const val STATE_CONTACTS = "STATE_CONTACTS"
    const val ITEM_ADAPTER_CONTACT = "ITEM_ADAPTER_CONTACT"
    const val LD_TOOLBAR_ADD_CONTACT = "LD_TOOLBAR_ADD_CONTACT"
    const val ADAPTER_CONTACTS = "ADAPTER_CONTACTS"
    const val VM_CONTACTS = "VM_CONTACTS"
    const val VM_ADD_CONTACT = "VM_ADD_CONTACT"
}
