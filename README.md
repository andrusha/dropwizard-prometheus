# Dropwizard -> Prometheus

Dropwizard to Prometheus exporter.

* Runs http server to serve metrics.
* Converts Dropwizard metric names to Prometheus format.
* Supports multiple metric registries.

## Usage

Include sbt dependency:
```
"me.andrusha" %% "dropwizard-prometheus" % "0.1.1"
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
