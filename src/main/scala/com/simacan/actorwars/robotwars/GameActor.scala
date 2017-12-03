package com.simacan.actorwars.robotwars

import java.awt.Color

import akka.actor.{Actor, ActorRef}
import com.simacan.actorwars.robotwars.Domain._
import com.simacan.actorwars.robotwars.InternalGameActorMessages.GameTick

import scala.concurrent.ExecutionContextExecutor

object InternalGameActorMessages {

  sealed trait InternalGameActorMessages

  object GameTick extends InternalGameActorMessages
}

trait GameSettingsProvider {
  def gameSettings: GameSettings
}

trait GameEngine {
  def initialize(waitingStart: WaitingStart): Fighting
  def calculateNextState(fighting: Fighting): Fighting
}

trait GameActor extends Actor with GameEngine with GameSettingsProvider {
  override def receive = inState(WaitingStart())

  override def preStart(): Unit = {
    super.preStart()
    // TODO: later have another actor initialize the scheduler
    import scala.concurrent.duration._
    implicit val executionContext: ExecutionContextExecutor = context.system.dispatcher
    context.system.scheduler.schedule(0.millis, gameSettings.updateInterval, self, GameTick)
  }

  def inState: GameState ⇒ Receive = {

    case state@WaitingStart(players, spectators) ⇒ {

      case RegisterRobot(listener, name) ⇒
        val newPlayers = players :+ createPlayer(listener, name)

        context become inState(
          if (newPlayers.length < gameSettings.levelSettings.maximumRobots)
            state.copy(players = newPlayers)
          else
            Fighting(players = newPlayers, spectators = spectators, time = 0f)
        )

      case RegisterSpectator(listener) ⇒
        context become inState(state.copy(spectators = spectators :+ Spectator(listener)))

      case GameTick ⇒ (players ++ spectators).foreach(_.actorRef ! state)
    }

    case state@Fighting(players, spectators, _, _) ⇒ {
      case RobotCommand(enginePower, steerPosition, fire) ⇒
        state.copy(players = players.map {
          case p@Player(robot, actorRef, _) if actorRef == sender() ⇒
            p.copy(robot.copy(enginePower = enginePower, steerPos = steerPosition, firing = fire))
          case p ⇒ p
        })

      case GameTick ⇒
        spectators.foreach(_.actorRef ! state)
    }
    case state@GameOver(winners, spectators) ⇒ {
      case GameTick ⇒ spectators.foreach(_.actorRef ! state)
    }
  }

  def createPlayer(ref: ActorRef, name: String) =
    Player(createRobot(), ref, name)

  def createRobot() = Robot(
    color = Color.RED,
    hitpoints = gameSettings.levelSettings.robotSettings.initialHitpoints)

//  def placeRobot(robot: Robot, playerNumber: Int, totalPlayers: Int): Robot = {
//    val middleX = gameSettings.levelSettings.width / 2
//    val middleY = gameSettings.levelSettings.height / 2
//    val ringDiameter = Math.min(gameSettings.levelSettings.width, gameSettings.levelSettings.height) / 4
//    val ringPos = ((playerNumber.toDouble / totalPlayers.toDouble) * Math.PI * 2).toFloat
//    val posX = (middleX + ringDiameter * Math.sin(ringPos)).toFloat
//    val posY = (middleY + ringDiameter * Math.cos(ringPos)).toFloat
//    val color = Color.getHSBColor(playerNumber.toFloat / totalPlayers.toFloat, .85f, .6f)
//    robot.copy(color = color, pos = (posX, posY), heading = ringPos)
//  }
}

