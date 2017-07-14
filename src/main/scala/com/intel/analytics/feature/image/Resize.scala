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

import org.opencv.core.{Mat, Size}
import org.opencv.imgproc.Imgproc

import scala.reflect.ClassTag
import scala.util.Random

/**
 *
 * @param resizeH
 * @param resizeW
 * @param resizeMode if resizeMode = -1, random select a mode from
 * (Imgproc.INTER_LINEAR, Imgproc.INTER_CUBIC, Imgproc.INTER_AREA,
 *                   Imgproc.INTER_NEAREST, Imgproc.INTER_LANCZOS4)
 * @tparam T
 */
class Resize[T: ClassTag](resizeH: Int, resizeW: Int, resizeMode: Int = Imgproc.INTER_LINEAR)
  extends ImageTransformer[T]() with LabelTransformer[T]{

  val interpMethods = Array(Imgproc.INTER_LINEAR, Imgproc.INTER_CUBIC, Imgproc.INTER_AREA,
    Imgproc.INTER_NEAREST, Imgproc.INTER_LANCZOS4)

  private var params: Map[String, Any] = Map()

  override def apply(matImage: Image[T]): Image[T] = {
    params += ("originalHeight" -> matImage.height, "originalWidth" -> matImage.width)
    matImage.scaleH = matImage.height / resizeH.toFloat
    matImage.scaleW = matImage.width / resizeW.toFloat
    val output = if (inplace) matImage.mat else new Mat()
    val interpMethod = if (resizeMode == -1) {
      interpMethods(new Random().nextInt(interpMethods.length))
    } else {
      resizeMode
    }
    Imgproc.resize(matImage.mat, output, new Size(resizeW, resizeH), 0, 0, interpMethod)
    matImage.mat = output
    matImage.label = transformLabel(matImage.label, params)
    println("resize done")
    matImage
  }
}

object Resize {
  def apply[T: ClassTag](resizeH: Int, resizeW: Int, resizeMode: Int = Imgproc.INTER_LINEAR,
    inplace: Boolean = true): Resize[T] = new Resize[T](resizeH, resizeW, resizeMode)
}

