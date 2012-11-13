/**
 * User: mick
 * Date: 29.08.12
 * Time: 17:54
 */
class ControlFunctionFactory {
  def create = ControlFunction.respond _
}

object ControlFunction {
  def respond(input: String): String = {
    val (opcode, paramMap) = CommandParser(input)

    if( opcode == "React" ) {
      val viewString = paramMap("view")
      MainBot.react(View(viewString))
    } else ""
  }
}

object MainBot {
  def weigthElRelative(elxy: ElementXY) = elxy.el match {
    case Snorg => sqrWeight(-400.0, elxy.xy.length, 2, 100)
    case Toxifera => sqrtWeight(-10.0, elxy.xy.length, 0.1, 100)
    case Empty => sqrtWeight(1.0, elxy.xy.length, 0.1, 100)
    case Wall => sqrtWeight(-10.0, elxy.xy.length, 0.1, 10000)
    case Fluppet => sqrtWeight(400.0, elxy.xy.length, 0.1, 100)
    case Zugar => linearWeight(200.0, elxy.xy.length, 0.1, 100)
    case _ => 0.0
  }

  def weigthElRelative2(elxy: ElementXY) = elxy.el match {
    case Snorg => sqrWeight(-200.0, elxy.xy.length, 2, 1000)
    case Toxifera => linearWeight(-10.0, elxy.xy.length, 2, 1000)
    case Empty => sqrtWeight(1.0, elxy.xy.length, 2, 1000)
    case Wall => linearWeight(-10.0, elxy.xy.length, 2, 10000)
    case Fluppet => linearWeight(300.0, elxy.xy.length, 2, 1000)
    case Zugar => sqrtWeight(200.0, elxy.xy.length, 2, 1000)
    case _ => 0.0
  }

  def weightPos(view: View, pos: XY, vector: XY) = view.offsets(pos + vector, _ != Unknown).foldLeft(0.0) {(accu, exy) =>
    val cos = vector.scalar(exy.xy) / (vector.length * exy.xy.length)
    accu +  weigthElRelative(exy) * (if (cos > 0.8) cos else 0.8)
//    accu +  weigthElRelative(exy)
  }

  def weightDirection(sector: List[ElementXY], dir: XY) = sector.foldLeft(0.0) {(accu, exy) =>
    val cos = dir.scalar(exy.xy) / (dir.length * exy.xy.length)
    accu + cos * weigthElRelative2(exy)
  }

  def sqrWeight(weight: Double, length: Double, crit: Double, critMulti: Double) =
    if (length < crit) weight * critMulti
    else weight / (length * length)

  def sqrtWeight(weight: Double, length: Double, crit: Double, critMulti: Double) =
    if (length < crit) weight * critMulti
    else weight / math.sqrt(length)

  def linearWeight(weight: Double, length: Double, crit: Double, critMulti: Double) =
    if (length < crit) weight * critMulti
    else weight / length

  def react(view: View): String = {
    val unitOffsets = (for (
      x <- -1 to 1;
      y <- -1 to 1
      if !(x == 0 && y == 0)
    ) yield (XY(x,y), weightPos(view, view.center, XY(x,y))))

//    val unitOffsets = (for (
//      x <- -1 to 1;
//      y <- -1 to 1
//      if !(x == 0 && y == 0)
//    ) yield (XY(x,y), weightDirection(view.part45(XY(x, y)), XY(x,y)) * (1 + (math.random / 100))))

    val move = unitOffsets.maxBy(_._2)._1
    "Move(direction=" + move + ")"
  }
}
