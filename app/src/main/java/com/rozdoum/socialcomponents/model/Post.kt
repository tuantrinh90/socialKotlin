/*
 * Copyright 2017 Rozdoum
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

package com.rozdoum.socialcomponents.model

import com.rozdoum.socialcomponents.enums.ItemType
import com.rozdoum.socialcomponents.utils.FormatterUtil

import java.io.Serializable
import java.util.Date
import java.util.HashMap

/**
 * Created by Kristina on 10/28/16.
 */

class Post : Serializable, LazyLoading {

    var id: String? = null
    var title: String? = null
    var description: String? = null
    var createdDate: Long = 0
    var imagePath: String? = null
    var imageTitle: String? = null
    var authorId: String? = null
    var commentsCount: Long = 0
    var likesCount: Long = 0
    var watchersCount: Long = 0
    var isHasComplain: Boolean = false
    private var itemType: ItemType? = null

    constructor() {
        this.createdDate = Date().time
        itemType = ItemType.ITEM
    }

    constructor(itemType: ItemType) {
        this.itemType = itemType
        id = itemType.toString()
    }

    fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()

        result["title"] = title
        result["description"] = description
        result["createdDate"] = createdDate
        result["imagePath"] = imagePath
        result["imageTitle"] = imageTitle
        result["authorId"] = authorId
        result["commentsCount"] = commentsCount
        result["likesCount"] = likesCount
        result["watchersCount"] = watchersCount
        result["hasComplain"] = isHasComplain
        result["createdDateText"] = FormatterUtil.firebaseDateFormat.format(Date(createdDate))

        return result
    }

    override fun getItemType(): ItemType? {
        return itemType
    }

    override fun setItemType(itemType: ItemType) {

    }
}
