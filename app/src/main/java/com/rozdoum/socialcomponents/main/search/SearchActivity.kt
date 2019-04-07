/*
 * Copyright 2018 Rozdoum
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.rozdoum.socialcomponents.main.search

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem

import com.rozdoum.socialcomponents.R
import com.rozdoum.socialcomponents.adapters.viewPager.TabsPagerAdapter
import com.rozdoum.socialcomponents.main.base.BaseActivity
import com.rozdoum.socialcomponents.main.search.posts.SearchPostsFragment
import com.rozdoum.socialcomponents.main.search.users.SearchUsersFragment
import com.rozdoum.socialcomponents.utils.LogUtil

/**
 * Created by Alexey on 08.05.18.
 */

class SearchActivity : BaseActivity<SearchView, SearchPresenter>(), SearchView {
    private var tabsAdapter: TabsPagerAdapter? = null
    private var viewPager: ViewPager? = null
    private var tabLayout: TabLayout? = null
    private var searchView: android.support.v7.widget.SearchView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        actionBar = supportActionBar

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        initContentView()
    }

    override fun createPresenter(): SearchPresenter {
        return if (presenter == null) {
            SearchPresenter(this)
        } else presenter
    }

    private fun initContentView() {
        viewPager = findViewById(R.id.pager)
        tabLayout = findViewById(R.id.tabLayout)

        initTabs()
    }

    private fun initTabs() {

        tabsAdapter = TabsPagerAdapter(this, supportFragmentManager)

        val argsPostsTab = Bundle()
        tabsAdapter!!.addTab(SearchPostsFragment::class.java, argsPostsTab, resources.getString(R.string.posts_tab_title))

        val argsUsersTab = Bundle()
        tabsAdapter!!.addTab(SearchUsersFragment::class.java, argsUsersTab, resources.getString(R.string.users_tab_title))

        viewPager!!.adapter = tabsAdapter
        tabLayout!!.setupWithViewPager(viewPager)

        viewPager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                search(searchView!!.query.toString())
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
    }

    private fun search(searchText: String) {
        val fragment = tabsAdapter!!.getItem(viewPager!!.currentItem)
        (fragment as Searchable).search(searchText)
        LogUtil.logDebug(TAG, "search text: $searchText")
    }

    private fun initSearch(searchMenuItem: MenuItem) {
        // Associate searchable configuration with the SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = searchMenuItem.actionView as android.support.v7.widget.SearchView
        searchView!!.maxWidth = Integer.MAX_VALUE
        searchView!!.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        searchMenuItem.expandActionView()

        searchView!!.setOnQueryTextListener(object : android.support.v7.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                search(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                search(newText)
                return true
            }
        })

        searchMenuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                finish()
                return false
            }
        })
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.search_menu, menu)

        val searchMenuItem = menu.findItem(R.id.action_search)
        initSearch(searchMenuItem)

        return true
    }

    companion object {
        private val TAG = SearchActivity::class.java.simpleName
    }

}
