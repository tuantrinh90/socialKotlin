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

package com.rozdoum.socialcomponents.adapters.viewPager

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager

import com.rozdoum.socialcomponents.main.search.Searchable

import java.util.ArrayList

class TabsPagerAdapter(private val mActivity: FragmentActivity, fragmentManager: FragmentManager) : SmartFragmentStatePagerAdapter(fragmentManager) {

    private val mTabs = ArrayList<TabInfo>()

    override fun getItem(position: Int): Fragment {
        return mTabs[position].fragment
    }

    override fun getCount(): Int {
        return mTabs.size
    }

    fun addTab(clss: Class<out Searchable>, args: Bundle?, title: String) {
        mTabs.add(TabInfo(title, Fragment.instantiate(mActivity, clss.name, args)))
    }

    // Returns the page title for the top indicator
    override fun getPageTitle(position: Int): CharSequence? {
        return mTabs[position].title
    }

    internal class TabInfo(var title: String, var fragment: Fragment)
}
