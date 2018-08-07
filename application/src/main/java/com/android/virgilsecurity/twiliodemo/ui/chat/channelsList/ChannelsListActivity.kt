package com.android.virgilsecurity.twiliodemo.ui.chat.channelsList

import com.android.virgilsecurity.base.view.BaseActivity

/**
 * . _  _
 * .| || | _
 * -| || || |   Created by:
 * .| || || |-  Danylo Oliinyk
 * ..\_  || |   on
 * ....|  _/    5/29/18
 * ...-| | \    at Virgil Security
 * ....|_|-
 */

//class ChannelsListActivity : BaseActivity() {
//
//    private val threadsListTag = "threadsListTag"
//    private val userManager: UserProperties by inject()
//    private lateinit var createThreadDialog: CreateThreadDialog
//    private var secondPress: Boolean = false
//
//    companion object {
//        fun startWithFinish(from: AppCompatActivity) {
//            from.startActivity(Intent(from, ChannelsListActivity::class.java))
//            from.finish()
//        }
//    }
//
//    override fun provideLayoutId() = R.layout.activity_channels_list
//
//    override fun preInitUi() {
//        // TODO Implement body or it will be empty ):
//    }
//
//    override fun initUi() {
//        initToolbar(toolbar, getString(R.string.app_name))
//        initDrawer()
//
//        UiUtils.replaceFragmentNoBackStack(supportFragmentManager,
//                                                                                  R.id.flBaseContainer,
//                                                                                  ChannelsListFragment.newInstance(),
//                                                                                  threadsListTag)
//    }
//
//    override fun initViewCallbacks() {
//        showHamburger(true, View.OnClickListener {
//            if (!dlDrawer.isDrawerOpen(Gravity.START))
//                dlDrawer.openDrawer(Gravity.START)
//            else
//                dlDrawer.closeDrawer(Gravity.START)
//        })
//    }
//
//    override fun initData() {
//        // TODO Implement body or it will be empty ):
//    }
//
//    private fun initDrawer() {
//        val tvUsernameDrawer = nvNavigation.getHeaderView(0)
//                .findViewById<TextView>(R.id.tvUsernameDrawer)
//        tvUsernameDrawer.text = userManager.getCurrentUser()!!.identity
//
//        actionBar?.setHomeButtonEnabled(true)
//
//        nvNavigation.setNavigationItemSelectedListener(
//            { drawerItem ->
//                when (drawerItem.itemId) {
//                    R.id.itemNewChat -> {
//                        dlDrawer.closeDrawer(Gravity.START)
//                        createThreadDialog = CreateThreadDialog(this,
//                                                                R.style.NotTransBtnsDialogTheme,
//                                                                getString(R.string.create_thread),
//                                                                getString(R.string.enter_username))
//
//                        createThreadDialog.setOnClickListener(
//                            { _, identity ->
//                                if (userManager.getCurrentUser()!!.identity == identity) {
//                                    UiUtils.toast(this, R.string.no_chat_with_yourself)
//                                } else {
//                                    createThreadDialog.showProgress(true)
//                                    val threadsListFragment =
//                                            supportFragmentManager.findFragmentByTag(threadsListTag) as ChannelsListFragment
//                                    threadsListFragment.issueCreateChannel(identity)
//                                }
//                            },
//                            {
//                                (it as CreateThreadDialog).showProgress(false)
//                                it.cancel()
//                            })
//
//                        createThreadDialog.show()
//
//                        return@setNavigationItemSelectedListener true
//                    }
//                    R.id.itemLogOut -> {
//                        dlDrawer.closeDrawer(Gravity.START)
//
//                        val threadsListFragment =
//                                supportFragmentManager.findFragmentByTag(threadsListTag) as ChannelsListFragment
//                        threadsListFragment.onLogOut()
//
//                        userManager.clearCurrentUser()
//                        userManager.clearUserCard()
//
//                        AuthActivity.startClearTop(this)
//                        return@setNavigationItemSelectedListener true
//                    }
//                    else -> return@setNavigationItemSelectedListener false
//                }
//            })
//    }
//
//    fun openChannel(channel: Channel) {
//        ChannelActivity.startWithExtras(this, Constants.KEY_CHANNEL, channel)
//    }
//
//    fun dialogNewChannelStopLoading() {
//        createThreadDialog.showProgress(false)
//    }
//
//    fun dialogNewChannelCancel() {
//        createThreadDialog.cancel()
//    }
//}
