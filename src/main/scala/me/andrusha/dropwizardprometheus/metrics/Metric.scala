package me.andrusha.dropwizardprometheus.metrics

import com.codahale.{metrics => dropwizard}
import me.andrusha.dropwizardprometheus.utils.NameCase

sealed abstract class MetricType(val entryName: String)
object MetricType {
  case object Gauge extends MetricType("gauge")
  case object Counter extends MetricType("counter")
  case object Summary extends MetricType("summary")
  case object Histogram extends MetricType("histogram")
  case object Untyped extends MetricType("untyped")
}

object Metric {
  def fromDropwizard(name: String, metric: dropwizard.Metric): Metric = {
    val n = nameToPrometheus(name)
    val d = nameToExtraDimensions(name)


    val help = s"$n generated from Dropwizard metric import (metric=$name, type=${metric.getClass.getName})"

    metric match {
      case g: dropwizard.Gauge[_]  => ValueMetric(n, MetricType.Gauge, help, g.getValue, d)
      case c: dropwizard.Counter   => ValueMetric(n, MetricType.Gauge, help, c.getCount, d)
      case m: dropwizard.Meter     => ValueMetric(n, MetricType.Counter, help, m.getCount, d)
      case h: dropwizard.Histogram => SampledMetric(n, MetricType.Summary, help, MetricSample.fromDropwizard(h.getSnapshot), h.getCountd, d)
      case t: dropwizard.Timer     => SampledMetric(n, MetricType.Summary, help, MetricSample.fromDropwizard(t.getSnapshot), t.getCount, d)
    }
  }

  def nameToPrometheus(name: String): String = {
    name.split('.').take(0).take(0).map(NameCase.toSnakeCase).+:("spark").mkString("_")
  }

  def nameToExtraDimensions(name: String): Dimensions = {
    val d = Dimensions.Empty
    val n = name.split('.')
    d.append("spark_app", n(0))
    d.append("spark_executor_id", n(1))
    d
  }
}

trait Metric {
  def toPrometheus: String
}