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
import org.opencv.core.{Core, Mat}
import org.opencv.imgproc.Imgproc

import scala.reflect.ClassTag

/**
 * Random adjust brightness, contrast, hue, saturation
 * @param brightnessProb probability to adjust brightness
 * @param brightnessDelta brightness parameter
 * @param contrastProb probability to adjust contrast
 * @param contrastLower contrast lower parameter
 * @param contrastUpper contrast upper parameter
 * @param hueProb probability to adjust hue
 * @param hueDelta hue parameter
 * @param saturationProb probability to adjust saturation
 * @param saturationLower saturation lower parameter
 * @param saturationUpper saturation upper parameter
 * @param randomOrderProb random order for different operation
 */
class ColorJitter[T: ClassTag](
  brightnessProb: Double, brightnessDelta: Double,
  contrastProb: Double, contrastLower: Double, contrastUpper: Double,
  hueProb: Double, hueDelta: Double,
  saturationProb: Double, saturationLower: Double, saturationUpper: Double,
  randomOrderProb: Double) extends ImageTransformer[T] {

  require(contrastUpper >= contrastLower, "contrast upper must be >= lower.")
  require(contrastLower >= 0, "contrast lower must be non-negative.")
  require(saturationUpper >= saturationLower, "saturation upper must be >= lower.")
  require(saturationLower >= 0, "saturation lower must be non-negative.")

  private def adjustBrightness(inImg: Mat, outImg: Mat, delta: Float): Mat = {
    if (delta != 0) {
      inImg.convertTo(outImg, -1, 1, delta)
      outImg
    } else {
      inImg
    }
  }

  private def adjustContrast(inImg: Mat, outImg: Mat, delta: Float): Mat = {
    if (Math.abs(delta - 1) > 1e-3) {
      inImg.convertTo(outImg, -1, delta, 0)
      outImg
    } else {
      inImg
    }
  }

  private def adjustSaturation(inImg: Mat, outImg: Mat, delta: Float): Mat = {
    if (Math.abs(delta - 1) != 1e-3) {
      // Convert to HSV colorspace
      Imgproc.cvtColor(inImg, outImg, Imgproc.COLOR_BGR2HSV)

      // Split the image to 3 channels.
      val channels = new util.ArrayList[Mat]()
      Core.split(outImg, channels)

      // Adjust the saturation.
      channels.get(1).convertTo(channels.get(1), -1, delta, 0)
      Core.merge(channels, outImg)
      // Back to BGR colorspace.
      Imgproc.cvtColor(outImg, outImg, Imgproc.COLOR_HSV2BGR)
      outImg
    } else {
      inImg
    }
  }

  private  def adjustHue(inImg: Mat, outImg: Mat, delta: Float): Mat = {
    if (delta != 0) {
      // Convert to HSV colorspae
      Imgproc.cvtColor(inImg, outImg, Imgproc.COLOR_BGR2HSV)

      // Split the image to 3 channels.
      val channels = new util.ArrayList[Mat]()
      Core.split(outImg, channels)

      // Adjust the hue.
      channels.get(0).convertTo(channels.get(0), -1, 1, delta)
      Core.merge(channels, outImg)

      // Back to BGR colorspace.
      Imgproc.cvtColor(outImg, outImg, Imgproc.COLOR_HSV2BGR)
      outImg
    } else {
      inImg
    }
  }

  private  def randomOrderChannels(inImg: Mat, outImg: Mat, randomOrderProb: Double = 0): Mat = {
    val prob = RNG.uniform(0, 1)
    if (prob < randomOrderProb) {
      // Split the image to 3 channels.
      val channels = new util.ArrayList[Mat]()
      Core.split(outImg, channels)
      // Shuffle the channels.
      util.Collections.shuffle(channels)
      Core.merge(channels, outImg)
      outImg
    } else {
      inImg
    }
  }

  private def randomOperation(operation: ((Mat, Mat, Float) => Mat),
    input: Mat, output: Mat, lower: Double, upper: Double, threshProb: Double): Mat = {
    val prob = RNG.uniform(0, 1)
    if (prob < threshProb) {
      val delta = RNG.uniform(lower, upper).toFloat
      return operation(input, output, delta)
    }
    input
  }

  def apply(matImage: Image[T]): Image[T] = {
    val image = matImage.mat
    val prob = RNG.uniform(0, 1)
    if (prob > 0.5) {
      // Do random brightness distortion.
      randomOperation(adjustBrightness, image, image,
        -brightnessDelta, brightnessDelta, brightnessProb)
      // Do random contrast distortion.
      randomOperation(adjustContrast, image, image,
        contrastLower, contrastUpper, contrastProb)
      // Do random saturation distortion.
      randomOperation(adjustSaturation, image, image,
        saturationLower, saturationUpper, contrastProb)
      // Do random hue distortion.
      randomOperation(adjustHue, image, image, -hueDelta, hueDelta, hueProb)
      // Do random reordering of the channels.
      randomOrderChannels(image, image, randomOrderProb)
    } else {
      // Do random brightness distortion.
      randomOperation(adjustBrightness, image, image,
        -brightnessDelta, brightnessDelta, brightnessProb)
      // Do random saturation distortion.
      randomOperation(adjustSaturation, image, image,
        saturationLower, saturationUpper, contrastProb)
      // Do random hue distortion.
      randomOperation(adjustHue, image, image, -hueDelta, hueDelta, hueProb)
      // Do random contrast distortion.
      randomOperation(adjustContrast, image, image,
        contrastLower, contrastUpper, contrastProb)
      // Do random reordering of the channels.
      randomOrderChannels(image, image, randomOrderProb)
    }
    matImage.mat = image
    matImage
  }
}

object ColorJitter {
  def apply[T: ClassTag](
    brightnessProb: Double, brightnessDelta: Double,
    contrastProb: Double, contrastLower: Double, contrastUpper: Double,
    hueProb: Double, hueDelta: Double,
    saturationProb: Double, saturationLower: Double, saturationUpper: Double,
    randomOrderProb: Double
  ): ColorJitter[T] =
    new ColorJitter[T](brightnessProb, brightnessDelta, contrastProb,
      contrastLower, contrastUpper, hueProb, hueDelta, saturationProb,
      saturationLower, saturationUpper, randomOrderProb)
}
