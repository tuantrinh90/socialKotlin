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

package com.rozdoum.socialcomponents.adapters

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

import com.rozdoum.socialcomponents.R
import com.rozdoum.socialcomponents.adapters.holders.UserViewHolder
import com.rozdoum.socialcomponents.managers.ProfileManager
import com.rozdoum.socialcomponents.managers.listeners.OnObjectChangedListenerSimple
import com.rozdoum.socialcomponents.model.Profile

import java.util.ArrayList

/**
 * Created by Alexey on 03.05.18.
 */

class SearchUsersAdapter(private val activity: Activity) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val itemsList = ArrayList<Profile>()

    private var callback: UserViewHolder.Callback? = null

    fun setCallback(callback: UserViewHolder.Callback) {
        this.callback = callback
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return UserViewHolder(inflater.inflate(R.layout.user_item_list_view, parent, false),
                callback, activity)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as UserViewHolder).bindData(itemsList[position])
    }

    fun setList(list: List<Profile>) {
        itemsList.clear()
        itemsList.addAll(list)
        notifyDataSetChanged()
    }

    fun updateItem(position: Int) {
        val profile = getItemByPosition(position)
        ProfileManager.getInstance(activity.applicationContext).getProfileSingleValue(profile.id, object : OnObjectChangedListenerSimple<Profile>() {
            override fun onObjectChanged(updatedProfile: Profile) {
                itemsList[position] = updatedProfile
                notifyItemChanged(position)
            }
        })
    }

    fun getItemByPosition(position: Int): Profile {
        return itemsList[position]
    }

    companion object {
        val TAG = SearchUsersAdapter::class.java.simpleName
    }
}
