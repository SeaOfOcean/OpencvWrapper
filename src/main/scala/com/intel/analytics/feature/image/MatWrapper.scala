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
import org.opencv.core.{Mat, MatOfByte}
import org.opencv.imgcodecs.Imgcodecs

object MatWrapper {
  OpenCV.load()

  def read(path: String): Mat = {
    Imgcodecs.imread(path)
  }

  def toMat(bytes: Array[Byte]): Mat = {
    Imgcodecs.imdecode(new MatOfByte(bytes: _*), Imgcodecs.CV_LOAD_IMAGE_COLOR)
  }

  def toBytes(mat: Mat, encoding: String = "jpg"): Array[Byte] = {
    val buf = new MatOfByte()
    Imgcodecs.imencode("." + encoding, mat, buf)
    buf.toArray
  }

  def toBytesBuf(mat: Mat, bytes: Array[Byte]): Array[Byte] = {
    require(bytes.length == mat.channels() * mat.height() * mat.width())
    mat.get(0, 0, bytes)
    bytes
  }

  def matToFloatArray(mat: Mat, bytes: Array[Byte], data: Array[Float]): this.type = {
    require(bytes.length == mat.height() * mat.width() * 3)
    require(data.length == mat.height() * mat.width() * 3)
    mat.get(0, 0, bytes)
    var i = 0
    while (i < mat.height() * mat.width() * 3) {
      data(i) = bytes(i) & 0xff
      i += 1
    }
    this
  }
}
