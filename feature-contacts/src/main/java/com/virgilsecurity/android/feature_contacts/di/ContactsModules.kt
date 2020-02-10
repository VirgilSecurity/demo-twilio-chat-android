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
import com.virgilsecurity.android.base.extension.moduleWithScope
import com.virgilsecurity.android.feature_contacts.data.repository.ContactsRepository
import com.virgilsecurity.android.feature_contacts.data.repository.ContactsRepositoryDefault
import com.virgilsecurity.android.feature_contacts.domain.addContact.AddContactDo
import com.virgilsecurity.android.feature_contacts.domain.addContact.AddContactsDoDefault
import com.virgilsecurity.android.feature_contacts.domain.list.GetContactsDo
import com.virgilsecurity.android.feature_contacts.domain.list.GetContactsDoDefault
import com.virgilsecurity.android.feature_contacts.view.AddContactController
import com.virgilsecurity.android.feature_contacts.view.ContactsController
import com.virgilsecurity.android.feature_contacts.viewmodel.addContact.AddContactVM
import com.virgilsecurity.android.feature_contacts.viewmodel.addContact.AddContactVMDefault
import com.virgilsecurity.android.feature_contacts.viewmodel.list.ContactsVM
import com.virgilsecurity.android.feature_contacts.viewmodel.list.ContactsVMDefault
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.core.qualifier.named

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

val contactsModule: Module = moduleWithScope(named<ContactsController>()) {
    scoped { ContactsRepositoryDefault(get(), get(), get()) as ContactsRepository }
    scoped { GetContactsDoDefault(get()) as GetContactsDo }
    scoped { MediatorLiveData<ContactsVM.State>() }
    viewModel { ContactsVMDefault(get(), get()) as ContactsVM }
}

val addContactModule: Module = moduleWithScope(named<AddContactController>()) {
    scoped { AddContactsDoDefault(get()) as AddContactDo }
    scoped { MediatorLiveData<AddContactVM.State>() }
    viewModel { AddContactVMDefault(get(), get(), get()) as AddContactVM }
}
