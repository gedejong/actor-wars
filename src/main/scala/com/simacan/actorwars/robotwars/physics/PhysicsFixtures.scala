package com.simacan.actorwars.robotwars.physics

import org.jbox2d.collision.shapes.CircleShape
import org.jbox2d.dynamics.{Filter, FixtureDef}

object PhysicsFixtures {
  val robotFixtureDef: FixtureDef = {
    val fixtureDef = new FixtureDef

    fixtureDef.shape = CircleShape
    fixtureDef.shape.m_radius = 10F // Todo: Tweak this until it feels right
    fixtureDef.density = 100F
    fixtureDef.friction = 0.5F
    fixtureDef.filter = CollisionFilters.robotFilter
    fixtureDef
  }

  val bulletFixtureDef: FixtureDef = {
    val fixtureDef = new FixtureDef

    fixtureDef.shape = CircleShape
    fixtureDef.shape.m_radius = 1F // Todo: Tweak this until it feels right
    fixtureDef.density = 1000F
    fixtureDef.friction = 0F
    fixtureDef.filter = CollisionFilters.bulletFilter
    fixtureDef
  }

}

// This whole setup might not be necessary, the defaults might work perfectly fine - but doesn't hurt to add
object CollisionFilters {

  private sealed abstract class CollisionCategory(
      val bitFlag: Int
  )

  private case object OuterWall extends CollisionCategory(0x0001)
  private case object Robot extends CollisionCategory(0x0002)
  private case object Bullet extends CollisionCategory(0x0004)

  val robotFilter: Filter = {
    val filter = new Filter()
    filter.categoryBits = Robot.bitFlag // I am a robot
    filter.maskBits = OuterWall.bitFlag | Robot.bitFlag | Bullet.bitFlag // and I collide with the outer wall, other robots, and bullets

    filter
  }

  val bulletFilter: Filter = {
    val filter = new Filter()
    filter.categoryBits = Bullet.bitFlag // I am a bullet
    filter.maskBits = OuterWall.bitFlag | Robot.bitFlag // and I collide with the outer wall and other robots

    filter
  }

  // No need to set outerWallFilter, those settings equal the default.

}

