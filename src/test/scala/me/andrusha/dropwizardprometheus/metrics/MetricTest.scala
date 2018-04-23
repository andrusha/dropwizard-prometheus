package me.andrusha.dropwizardprometheus.metrics

class MetricTest extends org.scalatest.FunSpec {
  describe("nameToPrometheus") {
    it("converts to snake_case terms split on dot") {
      assert(Metric.nameToPrometheus("camelCase.kebab-case.snake_case") == "camel_case:kebab_case:snake_case")
    }
  }
}
