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

import com.intel.analytics.bigdl.dataset.image.BGRImage
import org.opencv.core.Mat

import scala.reflect.ClassTag

//case class MatImage[T: ClassTag](var image: Mat, path: String = "", var scaleH: Float = 1, var scaleW: Float = 1, var label: Option[T] = None) {
//  def height = image.height()
//  def width = image.width()
//}

//case class ByteImage[T: ClassTag](var image: Array[Byte] = null, var dataLength: Int = 0, var path: String = "", var label: Option[T] = None)

class Image[T: ClassTag](path: String = "",
  var scaleH: Float = 1, var scaleW: Float = 1, var label: Option[T] = None) extends BGRImage {
  var mat: Mat = _
  var bytes: Array[Byte] = _
  var dataLength: Int = 0

  def setHeight(h: Int): this.type = {
    _height = h
    this
  }

  def setWidth(w: Int): this.type = {
    _width = w
    this
  }

  def this() = {
    this("")
    data = new Array[Float](0)
  }

  def matToFloatArray(mat: Mat): this.type = {
    _height = mat.height()
    _width = mat.width()
    if (data.length < height * width * 3) {
      bytes = new Array[Byte](height * width * 3)
      data = new Array[Float](height * width * 3)
    }
    MatWrapper.matToFloatArray(mat, bytes, data)
    this
  }

}

