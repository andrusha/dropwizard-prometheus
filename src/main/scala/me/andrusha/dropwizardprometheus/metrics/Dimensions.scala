package me.andrusha.dropwizardprometheus.metrics

case class Dimensions(d: Map[String, String]) extends AnyVal {
  def toPrometheus: String = {
    d.map { case (k, v) => s"""$k="$v"""" }.mkString("{", ",", "}")
  }

  def append(k: String, v: String): Dimensions = {
    Dimensions(d.updated(k, v))
  }
}

object Dimensions {
  val Empty = Dimensions(Map())
}