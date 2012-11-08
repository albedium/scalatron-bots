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
  def weigthPos(elxy: ElementXY) = elxy.el match {
    case Snorg => linearWeight(-100.0, elxy.xy.length, 0.1, 1000)
    case Toxifera => linearWeight(-10.0, elxy.xy.length, 0.1, 1000)
    case Empty => sqrtWeight(1.0, elxy.xy.length, 0.1, 1000)
    case Wall => linearWeight(-10.0, elxy.xy.length, 0.1, 1000)
    case Fluppet => linearWeight(300.0, elxy.xy.length, 0.1, 1000)
    case Zugar => sqrtWeight(200.0, elxy.xy.length, 0.1, 1000)
    case _ => 0.0
  }

  def weightPos(view: View, pos: XY) = {
    view.offsets(pos, _ != Unknown).foldLeft(0.0) {(weight, exy) =>
      weight +  weigthPos(exy)
    }
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
    ) yield (XY(x,y), weightPos(view, view.center + XY(x,y))))

    val move = unitOffsets.maxBy(_._2)._1
    "Move(direction=" + move + ")"
  }
}
