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

import java.util

import com.intel.analytics.bigdl.utils.RandomGenerator._
import com.intel.analytics.feature.image.roiImage.NormalizedBox
import org.opencv.core.{Core, Mat, Rect, Scalar}

import scala.reflect.ClassTag

class Expand[T: ClassTag](expandProb: Double = 0.5, maxExpandRatio: Double = 4.0,
  meansR: Int = 123, meansG: Int = 117, meansB: Int = 104,
  inplace: Boolean = true) extends ImageTransformer[T] with LabelTransformer[T]{

  var params: Map[String, Any] = Map()
  def apply(matImage: Image[T]): Image[T] = {
    val output = if (inplace) matImage.mat else new Mat()
    if (Math.abs(maxExpandRatio - 1) < 1e-2 || RNG.uniform(0, 1) > expandProb) {
      if (!inplace) {
        matImage.mat.copyTo(output)
        matImage.mat = output
      }
      return matImage
    }
    val expandRatio = RNG.uniform(1, maxExpandRatio)
    val expandBbox = new NormalizedBox()
    expandMat(matImage.mat, expandRatio, expandBbox, output, meansR, meansG, meansB)
    if (matImage.label.isDefined) {
      params += "expandRatio" -> expandRatio
      params += "expandBbox" -> expandBbox
      matImage.label = transformLabel(matImage.label, params)
    }
    matImage
  }

  private def expandMat(mat: Mat, expandRatio: Double, expandBbox: NormalizedBox, expandedMat: Mat,
    meansR: Int = 123, meansG: Int = 117, meansB: Int = 104): Unit = {
    val imgHeight = mat.rows()
    val imgWidth = mat.cols()
    val height = (imgHeight * expandRatio).toInt
    val width = (imgWidth * expandRatio).toInt
    val hOff = RNG.uniform(0, height - imgHeight).floor.toFloat
    val wOff = RNG.uniform(0, width - imgWidth).floor.toFloat
    expandBbox.x1 = -wOff / imgWidth
    expandBbox.y1 = -hOff / imgHeight
    expandBbox.x2 = (width - wOff) / imgWidth
    expandBbox.y2 = (height - hOff) / imgHeight

    expandedMat.create(height, width, mat.`type`())

    // Split the image to 3 channels.
    val channels = new util.ArrayList[Mat]()
    Core.split(expandedMat, channels)
    require(channels.size() == 3)
    channels.get(0).setTo(new Scalar(meansB))
    channels.get(1).setTo(new Scalar(meansG))
    channels.get(2).setTo(new Scalar(meansR))
    Core.merge(channels, expandedMat)

    val bboxRoi = new Rect(wOff.toInt, hOff.toInt, imgWidth.toInt, imgHeight.toInt)
    mat.copyTo(expandedMat.submat(bboxRoi))
  }
}

