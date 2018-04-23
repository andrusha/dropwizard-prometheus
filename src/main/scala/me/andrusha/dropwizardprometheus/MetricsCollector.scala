package me.andrusha.dropwizardprometheus

import com.codahale.metrics.MetricRegistry
import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.NanoHTTPD.{Response, newFixedLengthResponse}
import me.andrusha.dropwizardprometheus.metrics.Metric
import org.slf4j.{Logger, LoggerFactory}

import scala.annotation.tailrec


object MetricsCollector {
  private type MetricTransformer = Metric => Metric
  private var metricRegistries: List[(MetricRegistry, MetricTransformer)] = Nil
  private var server: Option[EmbeddedHttpServer] = None
  private val logger: Logger = LoggerFactory.getLogger("me.andrusha.dropwizardprometheus.MetricsCollector")

  /**
    *
    * @param metricRegistry metrics source
    * @param metricTransformer post process metrics before submitting them to prometheus
    */
  def register(
    metricRegistry: MetricRegistry,
    metricTransformer: MetricTransformer = identity): Unit = {
    metricRegistries.synchronized {
      metricRegistries = (metricRegistry, metricTransformer) :: metricRegistries
    }
    logger.info(s"Metrics registered: ${metricRegistry.getNames}")
  }

  @tailrec
  def start(host: String = "0.0.0.0", port: Int = 9095): Unit = server match {
      case Some(s) =>
        if (s.wasStarted()) {
          logger.warn(s"Server is already listening on ${s.getHostname}:${s.getListeningPort}")
        } else {
          s.start()
          logger.info(s"Started the embedded HTTP server on $host:$port")
        }
      case None =>
        server = Some(new EmbeddedHttpServer(host, port))
        start(host, port)
    }

  def stop(): Unit = server match {
      case Some(s) =>
        s.stop()
        logger.info(s"Stopped the embedded HTTP server on ${s.getHostname}:${s.getListeningPort}")
      case None =>
        logger.warn("Tried to stop not-running server")
    }

  private def metrics(): Seq[Metric] = {
    import scala.collection.JavaConverters._

    metricRegistries.flatMap { case (r, t) =>
      val metrics = r.getGauges().asScala.map((Metric.fromDropwizard _).tupled) ++
        r.getCounters().asScala.map((Metric.fromDropwizard _).tupled) ++
        r.getHistograms().asScala.map((Metric.fromDropwizard _).tupled) ++
        r.getTimers().asScala.map((Metric.fromDropwizard _).tupled) ++
        r.getMeters().asScala.map((Metric.fromDropwizard _).tupled)

      metrics.map(t)
    }
  }

  private class EmbeddedHttpServer(hostname: String, port: Int) extends NanoHTTPD(hostname, port) {
    override def serve(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response = {
      logger.debug(s"Serving metrics to ${session.getRemoteIpAddress} (${session.getRemoteHostName})")
      newFixedLengthResponse(
        Response.Status.OK,
        "text/plain; version=0.0.4; charset=utf-8",
        metrics().map(_.toPrometheus).mkString("\n"))
    }
  }
}
