package com.intel.analytics.feature.image

class MatWrapperSpec extends org.scalatest.FlatSpec {
  "read image" should "work properly" in {
    val resource = getClass().getClassLoader().getResource("image/000025.jpg")
    val image = MatWrapper.read(resource.getFile)
    val matImage = new Image()
    matImage.mat = image
    val normalize = new Normalize(2, 3, 4)
    normalize(matImage)
    println(image.height())
  }

  "toBytes" should "work properly" in {
    val resource = getClass().getClassLoader().getResource("image/000025.jpg")
    val img = MatWrapper.read(resource.getFile)
    val matImage = new Image()
    matImage.mat = img

    val bytes = new Array[Byte](img.height() * img.width() * 3)
    MatWrapper.toBytesBuf(img, bytes)


    val normalize = new Normalize(117, 30, 200)
    normalize(matImage)
    val image = new Image()
    image.setHeight( matImage.height)
    image.setWidth(matImage.width)
    image.scaleH = matImage.scaleH
    image.scaleW = matImage.scaleW
    image.label = matImage.label
    image.matToFloatArray(matImage.mat)

    val img2 = MatWrapper.read(resource.getFile)
    val matImage2 = new Image()
    matImage2.mat = img2
    val image2 = new Image()
    image2.setHeight( matImage2.height)
    image2.setWidth(matImage2.width)
    image2.scaleH = matImage2.scaleH
    image2.scaleW = matImage2.scaleW
    image2.label = matImage2.label
    image2.matToFloatArray(matImage2.mat)
    val content = image2.content
    var i = 0
    while (i < content.length) {
      content(i + 2) = content(i + 2) - 117
      content(i + 1) = content(i + 1) - 30
      content(i + 0) = content(i + 0) - 200
      i += 3
    }



//    normalize(matImage)
//
    image.content.zip(image2.content).foreach(x => {
      if (x._2 != x._1) println(x._2 + "!=" + x._1)
    })
  }
}
