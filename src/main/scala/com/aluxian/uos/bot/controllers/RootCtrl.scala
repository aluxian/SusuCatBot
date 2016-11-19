package com.aluxian.uos.bot.controllers

import io.finch.{Endpoint, _}

object RootCtrl {
  def root: Endpoint[String] = get(/) {
    Ok("ok")
  }
}
