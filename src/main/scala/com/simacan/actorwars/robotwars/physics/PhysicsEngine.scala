package com.simacan.actorwars.robotwars.physics

import java.awt.Color
import java.util.UUID

import com.simacan.actorwars.robotwars.Domain.{Fighting, GameSettings, Player, Robot, Vector2D, WaitingStart}
import com.simacan.actorwars.robotwars.{Domain, GameEngine}
import org.jbox2d.collision.shapes.ChainShape
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.{Body, BodyDef, BodyType, World}

class PhysicsEngine(gameSettings: GameSettings) extends GameEngine {

  val world: World = setupGameWorld() // Todo: I don't think we want state in here. Can we pass the World object around in the messages?

  implicit def vector2DToVec2(vector2D: Vector2D): Vec2 = {
    new Vec2(vector2D._1, vector2D._2)
  }

  override def initialize(waitingStart: WaitingStart): Fighting = {
    val players: Seq[Player] = setRobotStartingLocations(waitingStart.players)

    players.foreach(player => createRobotEntity(world, player))

    Fighting(players = players, spectators = waitingStart.spectators, bullets = Seq())
  }

  override def calculateNextState(fighting: Fighting): Fighting = {
    var body = world.getBodyList

    while( body != null) {
      body.getUserData match {
        case EntityData(Boundary, _) => _

        case EntityData(LiveRobot, _) => updateLiveRobot(body, fighting)
        case EntityData(DeadRobot, _) => _ // todo
        case EntityData(Bullet, _) => _ // todo
        case _ => throw new RuntimeException(s"Invalid entity: $body")
      }

      body = body.getNext

    }

  }


  private def updateLiveRobot(body: Body, fighting: Domain.Fighting): Unit = {
    body.getUserData match {
      case entityData: EntityData => {
        val robot: Robot = fighting.players.find(player => entityData.id == s"PLAYER ${player.name}")
            .getOrElse({throw new RuntimeException(s"Someone made player ${entityData.id} disappear")}).robot
        body.setLinearVelocity(robot.velocity)
        body.setAngularVelocity(body.getAngularVelocity + robot.steerPos)

        if (robot.firing) spawnBullet(body)

      }
    }
  }

  private def spawnBullet(source: Body): Unit = {
    val bulletBodyDef : BodyDef = new BodyDef()
    bulletBodyDef.`type` = BodyType.DYNAMIC
    bulletBodyDef.userData = EntityData(Bullet, s"BULLET ${UUID.randomUUID}")
    bulletBodyDef.position = source.getPosition.add(angleToVector(source.getAngle))  // Todo Do some geometry magic here to make the bullet spawn just outside the robot. This is clearly incorrect
    bulletBodyDef.linearVelocity = (100F, 100F) // Todo: sensible values
    bulletBodyDef.bullet = true


  }





  private def setRobotStartingLocations(players: Seq[Domain.Player]) : Seq[Player] = {

    for {
      i <- players.indices
      placedRobot <- placeRobot(players(i).robot, i, players.length)
      updatedPlayer <- players(i).copy(robot = placedRobot)
    } yield updatedPlayer


  }




  private def setupGameWorld(): World = {
    val world : World = new World((0F,0F)) // No gravity in our top down world

    val hEdgeDistance = gameSettings.levelSettings.width / 2
    val vEdgeDistance = gameSettings.levelSettings.height / 2

    val outerWallsDef: BodyDef = new BodyDef()
    outerWallsDef.`type` = BodyType.STATIC
    outerWallsDef.userData = EntityData(Boundary, "BOUNDARY")

    val outerWalls: Body = world.createBody(outerWallsDef)

    val edges = new ChainShape()
    edges.createLoop(Array[Vec2]((-hEdgeDistance, -vEdgeDistance), (-hEdgeDistance, vEdgeDistance), (hEdgeDistance, vEdgeDistance), (hEdgeDistance, -vEdgeDistance)), 4)
    val wallFixture = outerWalls.createFixture(edges, 0)

  }

  private def createRobotEntity(world: World, player: Player): Body = {

    val robotBodyDef : BodyDef = new BodyDef()
    robotBodyDef.`type` = BodyType.DYNAMIC
    robotBodyDef.userData = EntityData(LiveRobot, s"PLAYER ${player.name}")
    robotBodyDef.position = player.robot.pos
    robotBodyDef.angle = player.robot.heading // todo: is this the right way round for the game engine?

    val body = world.createBody(robotBodyDef)

    body.createFixture(PhysicsFixtures.robotFixtureDef)

    body

  }


  private def placeRobot(robot: Robot, playerNumber: Int, totalPlayers: Int): Robot = { // Todo: I copied this from the GameActor to have something to work with, clean up
    val middleX = gameSettings.levelSettings.width / 2
    val middleY = gameSettings.levelSettings.height / 2
    val ringDiameter = Math.min(gameSettings.levelSettings.width, gameSettings.levelSettings.height) / 4
    val ringPos = ((playerNumber.toDouble / totalPlayers.toDouble) * Math.PI * 2).toFloat
    val posX = (middleX + ringDiameter * Math.sin(ringPos)).toFloat
    val posY = (middleY + ringDiameter * Math.cos(ringPos)).toFloat
    val color = Color.getHSBColor(playerNumber.toFloat / totalPlayers.toFloat, .85f, .6f)
    robot.copy(color = color, pos = (posX, posY), heading = ringPos)
  }

  private def angleToVector(radian: Float): Vector2D = {
    new Vector2D(Math.cos(radian.toDouble).toFloat, Math.sin(radian.toDouble).toFloat)
  }

}
