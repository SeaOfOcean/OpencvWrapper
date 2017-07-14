package com.intel.analytics.feature.image

import java.util

import org.opencv.core.{Core, Mat, MatOfByte, Scalar}

import scala.reflect.ClassTag

class Normalize[T: ClassTag](meanR: Int, meanG: Int, meanB: Int)
  extends ImageTransformer[T]() {
//  var bytes: MatOfByte = _

  override def apply(matImage: Image[T]): Image[T] = {
//    val bytes = new Array[Byte](matImage.mat.channels() * matImage.mat.height() * matImage.mat.width())
//    val data = new Array[Float](matImage.mat.channels() * matImage.mat.height() * matImage.mat.width())
//    MatWrapper.matToFloatArray(matImage.mat, bytes, data)
//    val content = matImage.content
//    require(content.length % 3 == 0)
//    var i = 0
//    while (i < content.length) {
//      content(i + 2) = content(i + 2) - meanR
//      content(i + 1) = content(i + 1) - meanG
//      content(i + 0) = content(i + 0) - meanB
//      i += 3
//    }
//    matImage.mat.get(0, 0, content)
//    if (bytes == null) bytes = new MatOfByte()
//    MatWrapper.toBytes(matImage.image)

//    val content = new Array[Byte](matImage.height * matImage.width * 3)
//    MatWrapper.toBytesBuf(matImage.image, content)
//    var i = 0
//    while (i < content.length) {
//      content(i + 2) = (content(i + 2) & 0xff - meanR).toByte
//      content(i + 1) = (content(i + 1) & 0xff - meanG).toByte
//      content(i + 0) = (content(i + 0) & 0xff - meanB).toByte
//      i += 3
//    }
//    matImage.image.put(0, 0, content)
//    matImage.image.ge
//
//    val meanMat = new Mat()
//    if (meanMat.height() != matImage.height && meanMat.width() != matImage.width) {
//      meanMat.create(matImage.height, matImage.width, matImage.image.`type`())
//      // Split the image to 3 channels.
//      val channels = new util.ArrayList[Mat]()
//      Core.split(meanMat, channels)
//      require(channels.size() == 3)
//      channels.get(0).setTo(new Scalar(meanB))
//      channels.get(1).setTo(new Scalar(meanG))
//      channels.get(2).setTo(new Scalar(meanR))
//      Core.merge(channels, meanMat)
//    }
//
//    Core.subtract(matImage.image, meanMat, matImage.image)
    val channels = new util.ArrayList[Mat]()
    Core.split(matImage.mat, channels)
    require(channels.size() == 3)
////    channels.get(0).(new Scalar(meanB))
////    channels.get(1).setTo(new Scalar(meanG))
////    channels.get(2).setTo(new Scalar(meanR))
//    // blue channel
//    val bChannel = new Mat()
//    Core.subtract(channels.get(0), new Scalar(meanB), bChannel)
//    // green channel
//    val gChannel = new Mat()
//    Core.subtract(channels.get(1), new Scalar(meanG), gChannel)
//    // red channel
//    val rChannel = new Mat()
//    Core.subtract(channels.get(2), new Scalar(meanR), rChannel)
//    val dehazed = new Mat()
//    Core.merge(new util.ArrayList[Mat](util.Arrays.asList(bChannel, gChannel, rChannel)), dehazed)

    Core.subtract(channels.get(0), new Scalar(meanB), channels.get(0))
    Core.subtract(channels.get(1), new Scalar(meanG), channels.get(1))
    Core.subtract(channels.get(2), new Scalar(meanR), channels.get(2))
    Core.merge(channels, matImage.mat)
//    println("normalize done")
    matImage
  }
}

object RoiImageNormalizer {
  def apply[T: ClassTag](mean: (Int, Int, Int)): Normalize[T] = {
    new Normalize[T](mean._1, mean._2, mean._3)
  }
}