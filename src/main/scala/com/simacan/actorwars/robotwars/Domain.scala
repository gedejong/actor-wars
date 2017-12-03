package com.simacan.actorwars.robotwars

import java.awt.Color

import akka.actor.ActorRef
import org.jbox2d.dynamics.Body

import scala.concurrent.duration.FiniteDuration

object Domain {
  type Vector2D = (Float, Float)

  sealed trait GameObject

  case class Robot(
                    color: Color = Color.RED,
                    pos: Vector2D = (0, 0), velocity: Vector2D = (0, 0),
                    enginePower: Float = 0,
                    heading: Float = 0,
                    hitpoints: Int = 10,
                    steerPos: Float = 0,
                    coolDown: Float = 0,
                    firing: Boolean = false) extends GameObject

  case class Bullet(pos: Vector2D, velocity: Vector2D, color: Color) extends GameObject

  sealed trait GameStateListener {
    def actorRef: ActorRef
  }

  case class Player(robot: Robot, actorRef: ActorRef, name: String) extends GameStateListener

  case class Spectator(actorRef: ActorRef) extends GameStateListener


  sealed trait GameState

  case class WaitingStart(
                           players: Seq[Player] = Seq(),
                           spectators: Seq[Spectator] = Seq()) extends GameState

  case class Fighting(
                       players: Seq[Player] = Seq(),
                       spectators: Seq[Spectator] = Seq(),
                       bullets: Seq[Bullet] = Seq(),
                       time: Float = 0f) extends GameState

  case class GameOver(winners: Seq[Player], spectators: Seq[Spectator]) extends GameState


  sealed trait ClientMessage

  case class RegisterRobot(listener: ActorRef, name: String) extends ClientMessage

  case class RegisterSpectator(listener: ActorRef) extends ClientMessage

  case class RobotCommand(enginePower: Float, steerPosition: Float, fire: Boolean) extends ClientMessage

  case class RobotSettings(
                            width: Float,
                            height: Float,
                            initialHitpoints: Int,
                            coolDownMillis: Float,
                            maximumSteerPos: Float,
                            maximumEnginePower: Float,
                            bulletSpeed: Float)

  case class LevelSettings(
                            width: Float,
                            height: Float,
                            robotSettings: RobotSettings,
                            maximumRobots: Int)

  sealed trait ServerMessage

  case class GameTick(gameState: GameState) extends ServerMessage

  case class GameSettings(
                           levelSettings: LevelSettings,
                           updateInterval: FiniteDuration) extends ServerMessage

}

