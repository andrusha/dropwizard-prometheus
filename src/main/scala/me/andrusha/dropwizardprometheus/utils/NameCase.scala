package me.andrusha.dropwizardprometheus.utils

import java.util.regex.Pattern

object NameCase {
  private val regexp1: Pattern = Pattern.compile("([A-Z]+)([A-Z][a-z])")
  private val regexp2: Pattern = Pattern.compile("([a-z\\d])([A-Z])")
  private val cleanup: Pattern = Pattern.compile("[^\\w\\d]+")
  private val replacement: String = "$1_$2"

  def toSnakeCase(s: String): String = {
    val snake = camel2WordArray(s).mkString("_")
    cleanSnake(snake)
  }

  /**
    * Adapted from Lift's StringHelpers#snakify
    * https://github.com/lift/framework/blob/a3075e0676d60861425281427aa5f57c02c3b0bc/core/util/src/main/scala/net/liftweb/util/StringHelpers.scala#L91
    */
  private def camel2WordArray(name: String): Array[String] = {
    val first = regexp1.matcher(name).replaceAll(replacement)
    regexp2.matcher(first).replaceAll(replacement).split("_")
  }

  /**
    * @return non snakes are being replaces by snakes
    */
  private def cleanSnake(s: String): String = {
    cleanup.matcher(s.toLowerCase).replaceAll("_")
  }
}
