/*
 * Copyright 2016 The BigDL Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.intel.analytics.feature.image.roiImage

case class NormalizedBox(var x1: Float, var y1: Float, var x2: Float, var y2: Float) {

  var label: Float = -1

  def setLabel(l: Float): this.type = {
    label = l
    this
  }

  var difficult: Float = -1

  def setDifficult(d: Float): this.type = {
    difficult = d
    this
  }

  def this(other: NormalizedBox) {
    this(other.x1, other.y1, other.x2, other.y2)
  }

  def centerX(): Float = {
    (x1 + x2) / 2
  }

  def centerY(): Float = {
    (y1 + y2) / 2
  }

  def this() = {
    this(0f, 0f, 1f, 1f)
  }

  def width(): Float = x2 - x1

  def height(): Float = y2 - y1

  def area(): Float = width() * height()

}
