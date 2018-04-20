package me.andrusha.dropwizardprometheus.metrics

case class Dimensions(d: Map[String, String]) extends AnyVal {
  def toPrometheus: String = {
    d.map { case (k, v) => s"""$k="$v"""" }.mkString("{", ",", "}")
  }

  def merge(other: Map[String, String]): Dimensions =
    Dimensions(other.foldLeft(d) { case (m, (k, v)) => m.updated(k, v) })
}

object Dimensions {
  val Empty = Dimensions(Map())
}