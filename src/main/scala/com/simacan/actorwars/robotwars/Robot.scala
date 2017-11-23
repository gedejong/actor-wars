package com.simacan.actorwars.robotwars

import akka.actor.Actor

class Robot extends Actor {
  override def receive: Receive = standby()

  def standby(): Receive = {
    case _ => throw new IllegalStateException("Not implemented yet")
  }
}
