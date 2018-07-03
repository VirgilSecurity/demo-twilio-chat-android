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

package com.android.virgilsecurity.feature_login.view

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.android.virgilsecurity.base.view.BaseFragment
import com.android.virgilsecurity.base.view.adapter.DelegateAdapter
import com.android.virgilsecurity.common.data.model.UserVT
import com.android.virgilsecurity.feature_login.R
import com.android.virgilsecurity.twiliodemo.ui.login.AuthActivity
import kotlinx.android.synthetic.main.fragment_users_page.*
import org.koin.android.ext.android.inject

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    7/3/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

/**
 * UsersPageFragment
 */
class UsersPageFragment : BaseFragment<AuthActivity>() {

    private val adapter: DelegateAdapter<UserVT> by inject()
    private val layoutManage: GridLayoutManager by inject()

    override fun layoutResourceId(): Int = R.layout.fragment_users_page

    override fun init(view: View, savedInstanceState: Bundle?) {
        rvUsersGrid.adapter = adapter
    }

    override fun initViewSlices() {
        // TODO Implement body or it will be empty ):
    }

    override fun setupVSObservers() {
        // TODO Implement body or it will be empty ):
    }

    override fun setupVMStateObservers() {
        // TODO Implement body or it will be empty ):
    }

    companion object {
        const val KEY_USERS = "KEY_USERS"

        fun newInstance() = UsersPageFragment()

        fun newInstance(users: ArrayList<UserVT>) =
                UsersPageFragment().apply {
                    arguments = Bundle().apply { putParcelableArrayList(KEY_USERS, users) }
                }
    }
}