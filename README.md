# Dropwizard -> Prometheus
[![Build Status](https://travis-ci.org/andrusha/dropwizard-prometheus.svg?branch=master)](https://travis-ci.org/andrusha/dropwizard-prometheus)

Dropwizard to Prometheus exporter.

* Runs http server to serve metrics.
* Converts Dropwizard metric names to Prometheus format.
* Supports multiple metric registries.

## Usage

Include sbt dependency:
```
"me.andrusha" %% "dropwizard-prometheus" % "0.2.0"
```

1. Add registry `MetricsCollector.register(registry)`
2. Start server `MetricsCollector.start("0.0.0.0", 9095)`
3. Gracefully stop server `MetricsCollector.stop()`

### Spark integration

At the moment custom Spark metric sinks are not supported, however it's possible to define Sink as a part of Spark package:

```scala
package org.apache.spark.metrics.sink

private[spark] class PrometheusSink(
    val property: Properties,
    val registry: MetricRegistry,
    securityMgr: SecurityManager) extends Sink {

  override def start(): Unit = {
    MetricsCollector.register(registry)
    MetricsCollector.start("0.0.0.0", 9095)
  }
  
  override def stop(): Unit = {
    MetricsCollector.stop()
  }
  
  override def report(): Unit = ()
}
```

### Spark metric name converter

It's possible to transform metrics after the fact to fit your naming scheme better. In case of spark you would want to change metric names to have common prefix, eg:

```scala
  override def start(): Unit = {
   MetricsCollector.register(registry, sparkMetricsTranformer)
  }

  def sparkMetricsTranformer(m: Metric): Metric = m match {
    case ValueMetric(name, tpe, desc, v, d) =>
      ValueMetric(sparkName(name), tpe, desc, v, d.merge(extractDimensions(name)))
    case SampledMetric(name, tpe, desc, samples, cnt, d) =>
      SampledMetric(sparkName(name), tpe, desc, samples, cnt, d.merge(extractDimensions(name)))
  }

  /**
    * Eg:
    *   spark_application_1523628279755:208:executor:shuffle_total_bytes_read
    *     v v v
    *   spark:executor:shuffle_total_bytes_read
    */
  def sparkName(name: String): String = name.split(':').drop(2).+:("spark").mkString(":")

  /**
    * Two common naming patterns are:
    *   spark_application_1523628279755:driver:dag_scheduler:message_processing_time
    *   spark_application_1523628279755:208:executor:shuffle_total_bytes_read
    */
  def extractDimensions(name: String): Map[String, String] = name.split(':').toList match {
    case appId :: "driver" :: _ =>
      Map("app_id" -> appId, "app_type" -> "driver")
    case appId :: executorId :: _ =>
      Map("app_id" -> appId, "app_type" -> "executor", "executor_id" -> executorId)
    case _ => Map.empty
  }
}

```