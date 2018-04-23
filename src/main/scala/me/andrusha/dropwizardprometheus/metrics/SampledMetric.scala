package me.andrusha.dropwizardprometheus.metrics

import com.codahale.metrics.Snapshot

case class MetricSample(quantile: String, value: Double)

object MetricSample {
  def fromDropwizard(snapshot: Snapshot): List[MetricSample] = {
    MetricSample("0.5", snapshot.getMedian) ::
      MetricSample("0.75", snapshot.get75thPercentile()) ::
      MetricSample("0.95", snapshot.get95thPercentile()) ::
      MetricSample("0.98", snapshot.get98thPercentile()) ::
      MetricSample("0.99", snapshot.get99thPercentile()) ::
      MetricSample("0.999", snapshot.get999thPercentile()) ::
      Nil
  }
}

case class SampledMetric(
    name: String,
    tpe: MetricType,
    desc: String,
    samples: List[MetricSample],
    count: Long,
    dimensions: Dimensions = Dimensions.Empty) extends Metric {
  override def toPrometheus: String = {
    val s = samples.map { s =>
      val d = dimensions.merge(Map("quantile" -> s.quantile))
      s"""$name${d.toPrometheus} ${s.value}"""
    }.mkString("\n")

    s"""
       |# HELP $desc
       |# TYPE $name ${tpe.entryName}
       |$s
       |${name}_count${dimensions.toPrometheus} $count
    """.stripMargin
  }
}
