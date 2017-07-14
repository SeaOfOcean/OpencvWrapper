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
import com.intel.analytics.feature.image.LabelTransformMethod

case class RoiLabel(classes: Tensor[Float], bboxes: Tensor[Float]) {
  def copy(target: RoiLabel): Unit = {
    classes.resizeAs(target.classes).copy(target.classes)
    bboxes.resizeAs(target.bboxes).copy(target.bboxes)
  }

  if (classes.dim() == 1) {
    require(classes.size(1) == bboxes.size(1), "the number of classes should be" +
      " equal to the number of bounding box numbers")
  } else if (classes.nElement() > 0 && classes.dim() == 2) {
    require(classes.size(2) == bboxes.size(1), s"the number of classes ${ classes.size(2) }" +
      s"should be equal to the number of bounding box numbers ${ bboxes.size(1) }")
  }

  def size(): Int = {
    if (bboxes.nElement() == 0) 0 else bboxes.size(1)
  }
}

case class RoiNormalize() extends LabelTransformMethod[RoiLabel] {

  override def apply(label: RoiLabel, params: Map[String, Any]): RoiLabel = {
    val height = params("originalHeight").asInstanceOf[Float]
    val width = params("originalWidth").asInstanceOf[Float]
    BboxUtil.scaleBBox(label.bboxes, 1 / height, 1 / width)
    label
  }
}

case class RoiFlip() extends LabelTransformMethod[RoiLabel] {
  override def apply(label: RoiLabel, params: Map[String, Any]): RoiLabel = {
    var i = 1
    while (label.bboxes.nElement() > 0 && i <= label.bboxes.size(1)) {
      val x1 = 1 - label.bboxes.valueAt(i, 1)
      label.bboxes.setValue(i, 1, 1 - label.bboxes.valueAt(i, 3))
      label.bboxes.setValue(i, 3, x1)
      i += 1
    }
    label
  }
}
