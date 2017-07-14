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

import com.intel.analytics.bigdl.tensor.Tensor

object BboxUtil {
  // inplace scale
  def scaleBBox(classBboxes: Tensor[Float], height: Float, width: Float): Unit = {
    if (classBboxes.nElement() == 0) return
    classBboxes.select(2, 1).mul(width)
    classBboxes.select(2, 2).mul(height)
    classBboxes.select(2, 3).mul(width)
    classBboxes.select(2, 4).mul(height)
  }
}