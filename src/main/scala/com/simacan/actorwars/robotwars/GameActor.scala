package com.simacan.actorwars.robotwars

import java.awt.Color

import akka.actor.{Actor, ActorRef}
import com.simacan.actorwars.robotwars.Domain._
import com.simacan.actorwars.robotwars.InternalGameActorMessages.SendGameTick

import scala.concurrent.duration.FiniteDuration

object InternalGameActorMessages {
  sealed trait InternalGameActorMessages
  object SendGameTick extends InternalGameActorMessages
}


class GameActor(gameSettings: GameSettings) extends Actor {

  override def receive = inState(WaitingStart())

  import scala.concurrent.duration._

  override def preStart(): Unit = {
    super.preStart()
    //context.system.scheduler.schedule(0.millis, gameSettings.updateInterval, SendGameTick)
  }

  def inState: GameState ⇒ Receive = {

    case WaitingStart(players, spectators) ⇒ {

      case RegisterRobot(listener, name) ⇒
        val newPlayers = players :+ createPlayer(listener, name)

        if (newPlayers.length < gameSettings.levelSettings.maximumRobots)
          context become inState(WaitingStart( players = newPlayers, spectators = spectators))
        else
          context become inState(Fighting(players = newPlayers, spectators = spectators, time = 0d))

      case RegisterSpectator(listener) ⇒
        context become inState(WaitingStart(
          players = players,
          spectators = spectators :+ Spectator(listener)))


    }

    case Fighting(players, spectators, time) ⇒ ???
    case GameOver(winners, spectators) ⇒ ???
  }

  def createPlayer(ref: ActorRef, name: String) =
    Player(createRobot(), ref, name)

  def createRobot() = Robot(
    color = Color.RED,
    hitpoints = gameSettings.levelSettings.robotSettings.initialHitpoints)

  def placeRobot(robot: Robot, playerNumber: Int, totalPlayers: Int): Robot = {
    val middleX = gameSettings.levelSettings.width / 2
    val middleY = gameSettings.levelSettings.height / 2
    val ringDiameter = Math.min(gameSettings.levelSettings.width, gameSettings.levelSettings.height) / 4
    val ringPos = (playerNumber.toDouble / totalPlayers.toDouble) * Math.PI
    val posX = middleX + ringDiameter * Math.sin(ringPos)
    val posY = middleY + ringDiameter * Math.cos(ringPos)
    val color = Color.getHSBColor(playerNumber.toFloat / totalPlayers.toFloat, .85f, .6f)
    robot.copy(color = color, x = posX, y = posY, heading = ringPos)
  }
}

