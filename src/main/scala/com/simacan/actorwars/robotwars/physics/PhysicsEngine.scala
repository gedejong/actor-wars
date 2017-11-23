package com.simacan.actorwars.robotwars.physics

import com.simacan.actorwars.robotwars.Domain.{Fighting, LevelSettings, Vector2D}
import org.jbox2d.collision.shapes.ChainShape
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.{Body, BodyDef, BodyType, World}

class PhysicsEngine {

  implicit def vector2DToVec2(vector2D: Vector2D): Vec2 = {
    new Vec2(vector2D._1, vector2D._2)
  }

  val world : World = new World((0F,0F)) // No gravity in our top down world


  def setupGameWorld(levelSettings: LevelSettings) = {

    val verticalEdgeDistance = (levelSettings.height / 2)
    val horizontalEdgeDistance = levelSettings.height / 2


    val outerWallsDef: BodyDef = new BodyDef()
    outerWallsDef.`type` = BodyType.STATIC
    outerWallsDef.userData = "Outer walls"

    val outerWalls: Body = world.createBody(outerWallsDef)

    val edges = new ChainShape()
    edges.createLoop(Array[Vec2]((-100F, -100F), (-100F, 100F), (100F, 100F), (100F, -100F)), 4)
    outerWalls.createFixture(edges, 0)

  }


  def calculateGameState(beginState : Fighting): Fighting = {




  }

}
