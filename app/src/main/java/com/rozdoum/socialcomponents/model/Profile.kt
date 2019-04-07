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

import com.google.firebase.database.IgnoreExtraProperties
import com.rozdoum.socialcomponents.enums.ItemType

import java.io.Serializable

@IgnoreExtraProperties
class Profile : Serializable, LazyLoading {

    var id: String? = null
    var username: String? = null
    var email: String? = null
    var photoUrl: String? = null
    var likesCount: Long = 0
    var registrationToken: String? = null
    override var itemType: ItemType? = null

    constructor() {
        // Default constructor required for calls to DataSnapshot.getValue(Profile.class)
    }

    constructor(id: String) {
        this.id = id
    }

    constructor(load: ItemType) {
        itemType = load
    }
}
