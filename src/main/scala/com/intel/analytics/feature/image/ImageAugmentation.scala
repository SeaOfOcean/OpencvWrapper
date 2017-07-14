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

package com.intel.analytics.feature.image

import com.intel.analytics.OpenCV
import com.intel.analytics.bigdl.dataset.Transformer

import scala.reflect.ClassTag

class ImageAugmentation[T: ClassTag](dataAugs: Array[ImageTransformer[T]])
  extends Transformer[Image[T], Image[T]] {

  override def apply(prev: Iterator[Image[T]]): Iterator[Image[T]] = {
    var image = new Image[T]()

    prev.map(byteImage => {
      byteImage.mat = MatWrapper.toMat(byteImage.bytes)
      image = byteImage
//      var matImage = Image[T](mat, byteImage.path, mat.height(), mat.width(),
//        label = byteImage.label)
      dataAugs.foreach(dataAug => image = dataAug(image))
//      image.setHeight( matImage.height)
//      image.setWidth(matImage.width)
//      image.scaleH = matImage.scaleH
//      image.scaleW = matImage.scaleW
//      image.label = matImage.label
      image.matToFloatArray(image.mat)
      image
    })
  }
}

object ImageAugmentation {

  OpenCV.load()

  def apply[T: ClassTag](dataAugs: Array[ImageTransformer[T]]): ImageAugmentation[T] = new ImageAugmentation[T](dataAugs)
}

abstract class ImageTransformer[T: ClassTag]() extends Serializable {
  var inplace: Boolean = true

  def apply(matImage: Image[T]): Image[T]
}
