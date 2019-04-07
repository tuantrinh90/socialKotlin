package com.rozdoum.socialcomponents.main.search.users

import com.rozdoum.socialcomponents.main.base.BaseFragmentView
import com.rozdoum.socialcomponents.model.Profile

/**
 * Created by Alexey on 08.06.18.
 */
interface SearchUsersView : BaseFragmentView {
    fun onSearchResultsReady(profiles: List<Profile>)

    fun showLocalProgress()

    fun hideLocalProgress()

    fun showEmptyListLayout()

    fun updateSelectedItem()
}
