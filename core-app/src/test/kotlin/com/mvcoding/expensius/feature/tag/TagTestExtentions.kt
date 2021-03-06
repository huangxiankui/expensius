/*
 * Copyright (C) 2015 Mantas Varnagiris.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package com.mvcoding.expensius.feature.tag

import com.mvcoding.expensius.model.ModelState.NONE
import com.mvcoding.expensius.model.Tag
import java.util.UUID.randomUUID

fun aTag(): Tag = Tag(randomUUID().toString(), NONE, "title", 1)
fun someTags() = setOf(aTag(), aTag())
fun aNewTag(): Tag = Tag()
fun Tag.withTitle(title: String) = copy(title = title)
