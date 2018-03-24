package me.andrusha.dropwizardprometheus.metrics

case class ValueMetric[A](
    name: String,
    tpe: MetricType,
    desc: String,
    value: A,
    dimensions: Dimensions = Dimensions.Empty) extends Metric {
  override def toPrometheus: String = {
    s"""
       |# HELP $desc
       |# TYPE $name ${tpe.entryName}
       |$name${dimensions.toPrometheus} $value
    """.stripMargin
  }
}
