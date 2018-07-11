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

package com.android.virgilsecurity.feature_login.viewslice.login.list.adapter

import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.android.virgilsecurity.base.data.model.User
import com.android.virgilsecurity.base.extension.inflate
import com.android.virgilsecurity.common.util.ImageStorage
import com.android.virgilsecurity.feature_login.R
import com.android.virgilsecurity.feature_login.viewslice.login.list.ViewPagerSlice

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    7/4/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

/**
 * UsersPagerAdapterDefault
 */
class UsersPagerAdapterDefault(
        private val imageStorage: ImageStorage,
        private val context: Context,
        private val actionLiveData: MutableLiveData<ViewPagerSlice.Action>
) : UserPagerAdapter() {

    private lateinit var pages: MutableList<MutableList<User>>

    override fun isViewFromObject(view: View, `object`: Any) = view == (`object` as View)

    override fun getCount(): Int = pages.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val page = pages[position]

        val parent: ViewGroup = container.inflate(R.layout.item_users_page, false) as ViewGroup
        val childCount = parent.childCount

        for (i in 1..childCount)
            inflateChildAndAttach(parent.getChildAt(i) as ViewGroup, page[i])

        return parent
    }

    override fun setUsers(users: List<User>) {
        if (users.isNotEmpty()) {
            pages = ArrayList()
            val iterator = users.iterator()

            while (iterator.hasNext()) {
                val page = ArrayList<User>()

                for (i in 1..PAGE_SIZE) {
                    if (iterator.hasNext())
                        page.add(iterator.next())
                }

                pages.add(page)
            }
        }
    }

    override fun addUser(user: User) {
        if (pages[pages.size - 1].size == 4) {
            val newPage = MutableList(1) { user }
            pages.add(newPage)
        } else {
            pages[pages.size - 1].add(user)
        }
    }

    override fun clearUsers() {
        pages.clear()
    }

    private fun inflateChildAndAttach(parent: ViewGroup, user: User) {
        val child = parent.inflate(R.layout.item_login_user)
        child.findViewById<TextView>(R.id.tvUsername).text = user.identity

        val tvInitials = child.findViewById<TextView>(R.id.tvInitials)
        val ivUserPic = child.findViewById<ImageView>(R.id.ivUserPic)

        if (user.picturePath != null && imageStorage.exists(user.picturePath!!)) {
            tvInitials.visibility = View.GONE
            ivUserPic.setImageBitmap(imageStorage.get(
                Uri.Builder().path(user.picturePath!!).build()))
        } else {
            tvInitials.visibility = View.VISIBLE
            user.identity.split(" ").let {
                if (it.size > 1)
                    tvInitials.text = StringBuilder("$it[0]$it[1]").toString()
            }
            ivUserPic.background = context.getDrawable(R.drawable.rect_rounded_gradient_2)
            // TODO get random background
        }

        parent.setOnClickListener { actionLiveData.value = ViewPagerSlice.Action.UserClicked(user) }
        parent.addView(child)
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
    }

    companion object {
        const val PAGE_SIZE = 4 // TODO dynamically get size of page
    }
    // TODO change to recyclerview-like adapter
}