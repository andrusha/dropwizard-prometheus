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
    val help = s"$n generated from Dropwizard metric import (metric=$name, type=${metric.getClass.getName})"

    metric match {
      case g: dropwizard.Gauge[_]  => ValueMetric(n, MetricType.Gauge, help, g.getValue)
      case c: dropwizard.Counter   => ValueMetric(n, MetricType.Gauge, help, c.getCount)
      case m: dropwizard.Meter     => ValueMetric(n, MetricType.Counter, help, m.getCount)
      case h: dropwizard.Histogram => SampledMetric(n, MetricType.Summary, help, MetricSample.fromDropwizard(h.getSnapshot), h.getCount)
      case t: dropwizard.Timer     => SampledMetric(n, MetricType.Summary, help, MetricSample.fromDropwizard(t.getSnapshot), t.getCount)
    }
  }

  def nameToPrometheus(name: String): String = {
    name.split('.').map(NameCase.toSnakeCase).mkString(":")
  }
}

trait Metric {
  def toPrometheus: String
}