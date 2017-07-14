package com.intel.analytics.feature.image.roiImage

import com.intel.analytics.feature.image._
import org.opencv.imgproc.Imgproc

class RoiImageResize(resizeH: Int, resizeW: Int, resizeMode: Int = Imgproc.INTER_LINEAR)
  extends Resize[RoiLabel](resizeH, resizeW, resizeMode) {
  setLabelTransfomer(RoiNormalize())
}

class RoiImageNormalize(meanRGB: (Int, Int, Int))
  extends Normalize[RoiLabel](meanRGB._1, meanRGB._2, meanRGB._3)
