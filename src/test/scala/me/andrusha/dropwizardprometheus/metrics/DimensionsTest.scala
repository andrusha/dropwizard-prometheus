package me.andrusha.dropwizardprometheus.metrics

import org.scalatest.FunSpec

class DimensionsTest extends FunSpec {
  describe("toPrometheus") {
    it("joins dimensions in prometheus format") {
      val d = Dimensions(Map("cat" -> "dog", "mouse" -> "cat"))
      assert(d.toPrometheus == """{cat="dog",mouse="cat"}""")
    }
  }

  describe("merge") {
    it("joins dimensions together") {
      val d1 = Dimensions(Map("cat" -> "dog", "conflict" -> "old"))
      val d2 = Dimensions(Map("mouse" -> "cat", "conflict" -> "new"))

      assert(d1.merge(d2.d) == Dimensions(Map("cat" -> "dog", "mouse" -> "cat", "conflict" -> "new")))
    }
  }
}
