package com.intel.analytics.feature.image


trait LabelTransformer[T] {

  var labelTransformer: LabelTransformMethod[T] = Default[T]

  def setLabelTransfomer(method: LabelTransformMethod[T]): this.type = {
    labelTransformer = method
    this
  }

  def transformLabel(label: Option[T], params: Map[String, Any]): Option[T] = {
    if (label.isDefined) {
      Some(labelTransformer(label.get, params))
    } else {
      label
    }
  }
}

trait LabelTransformMethod[T] {
  def apply(label: T, params: Map[String, Any]): T
}

case class Default[T]() extends LabelTransformMethod[T] {
  override def apply(label: T, params: Map[String, Any]): T = label
}

