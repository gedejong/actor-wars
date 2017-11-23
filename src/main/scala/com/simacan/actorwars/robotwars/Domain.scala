package com.simacan.actorwars.robotwars

import java.awt.Color

import akka.actor.ActorRef

import scala.concurrent.duration.FiniteDuration

object Domain {

  sealed trait GameObject

  case class Robot(
                    color: Color = Color.RED,
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

  case class WaitingStart(players: Seq[Player] = Seq(), spectators: Seq[Spectator] = Seq()) extends GameState

  case class Fighting(players: Seq[Player], spectators: Seq[Spectator], time: Double) extends GameState

  case class GameOver(winners: Seq[Player], spectators: Seq[Spectator]) extends GameState


  sealed trait ClientMessage

  case class RegisterRobot(listener: ActorRef, name: String) extends ClientMessage

  case class RegisterSpectator(listener: ActorRef) extends ClientMessage

  case class RobotCommand(enginePower: Double, steerPosition: Double, fire: Boolean) extends ClientMessage

  case class RobotSettings(
                            width: Double,
                            height: Double,
                            initialHitpoints: Int,
                            coolDownMillis: Double,
                            maximumSteerPos: Double,
                            maximumEnginePower: Double,
                            bulletSpeed: Double)

  case class LevelSettings(
                            width: Double,
                            height: Double,
                            robotSettings: RobotSettings,
                            maximumRobots: Int)

  sealed trait ServerMessage

  case class GameTick(gameState: GameState) extends ServerMessage

  case class GameSettings(
                           levelSettings: LevelSettings,
                           updateInterval: FiniteDuration) extends ServerMessage

}

