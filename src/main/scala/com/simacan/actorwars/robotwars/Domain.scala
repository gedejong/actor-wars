package com.simacan.actorwars.robotwars

import java.awt.Color

import akka.actor.ActorRef

object Domain {

  sealed trait GameObject

  case class Robot(
                  color: Color,
                  x: Double = 0, y: Double = 0, dx: Double = 0, dy: Double = 0,
                  enginePower: Double = 0,
                  heading: Double = 0,
                  hitpoints: Int = 10,
                  steerPos: Double = 0,
                  coolDown: Double = 0,
                  firing: Boolean = false) extends GameObject

  case class Bullet(x: Double = 0, y: Double = 0, dx: Double = 0, dy: Double = 0, color: Color) extends GameObject

  sealed trait GameStateListener {
    def actorRef: ActorRef
  }

  case class Player(robot: Robot, actorRef: ActorRef, name: String) extends GameStateListener

  case class Spectator(actorRef: ActorRef) extends GameStateListener


  sealed trait GameState

  case class WaitingStart(players: Seq[Player], posRemain: Int = 8) extends GameState

  case class Fighting(players: Seq[Player], time: Double) extends GameState

  case class GameOver(winners: Seq[Player]) extends GameState


  sealed trait ServerMessage
  case class GameTick(gameState: GameState) extends ServerMessage

  sealed trait ClientMessage
  case class Register(listener: ActorRef, name: String) extends ClientMessage
  case class RobotCommand(enginePower: Double, steerPosition: Double, fire: Boolean) extends ClientMessage
}
