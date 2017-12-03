package com.simacan.actorwars.robotwars.physics

case class EntityData(
    entityType: EntityType,
    id: String,
)
sealed trait EntityType

case object LiveRobot extends EntityType
case object DeadRobot extends EntityType
case object Bullet extends EntityType
case object Boundary extends EntityType
