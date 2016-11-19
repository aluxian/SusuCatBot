package com.aluxian.uos.bot

import scala.util.Random

trait ResponseGenerator {
  /**
    * @param options the options to choose from
    * @return one of the given options, chosen randomly
    */
  def random(options: String*): String = {
    options(Random.nextInt(options.length))
  }
}
