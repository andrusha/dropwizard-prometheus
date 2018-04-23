package me.andrusha.dropwizardprometheus.metrics

case class Dimensions(d: Map[String, String]) extends AnyVal {
  /**
    * https://prometheus.io/docs/concepts/data_model
    * @todo escape values, verify that keys are correct
    * @return prometheus-compatible dimension string
    */
  def toPrometheus: String = {
    d.map { case (k, v) => s"""$k="$v"""" }.mkString("{", ",", "}")
  }

  /**
    * @todo allow to merge with Dimensions object
    * @return merged dimensions, other has precedence
    */
  def merge(other: Map[String, String]): Dimensions =
    Dimensions(other.foldLeft(d) { case (m, (k, v)) => m.updated(k, v) })
}

object Dimensions {
  val Empty = Dimensions(Map())
}