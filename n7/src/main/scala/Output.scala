/**
 * User: mick
 * Date: 13.11.12
 * Time: 18:39
 */
case class Output(stateParams: Map[String, Any], commands: List[Command], history: List[(XY, XY)]) {
  val historySize = 10

  def this() = this(Map.empty, List.empty, List.empty)
  def this(params: Map[String, String]) = this(params, List.empty, List.empty)
  def this(params: Map[String, String], h: List[(XY, XY)]) = this(params, List.empty, h)

  def append(command: Command) = new Output(stateParams, command :: commands, history)

  def set(params: (String,Any)*) = new Output(stateParams ++ params, commands, history)
  def set(keyPrefix: String, xy: XY) =
    new Output(stateParams ++ List(keyPrefix+"x" -> xy.x, keyPrefix+"y" -> xy.y), commands, history)

  /*
    adding move to history. should automatically compute new coords
   */
  def addHistory(move: XY) = {
    val prevH = history.headOption.getOrElse((XY(0,0),XY(0,0)))
    val newh = (move, prevH._2 + move)
    new Output(stateParams, commands, (newh :: history).take(10))
  }

  override def toString = {
    var result = commands.map(_.toString).mkString("|")
    if(!stateParams.isEmpty) {
      if(!result.isEmpty) result += "|"
      result += stateParams.map(e => e._1 + "=" + e._2).mkString("Set(",",",")")
    }

    //write moves
    if(!history.isEmpty) {
      if(!result.isEmpty) result += "|"
      result += "Set(_history=" + history.map(el => el._1.toString + ";" + el._2.toString).mkString(";") + ")"
    }

    result
  }

  def move(direction: XY) = append(Move(direction))
  def say(text: String) = append(Say(text))
  def status(text: String) = append(Status(text))
  def explode(blastRadius: Int) = append(Explode(blastRadius))
  def spawn(offset: XY, name: String, energy: Int, mood: String) = append(Spawn(offset, name, energy, mood))
}

sealed case class Command()
case class Move(offset: XY) extends Command
case class Spawn(offset: XY, name: String, energy: Int, mood: String) extends Command
case class Explode(size: Int) extends Command
case class Say(text: String) extends Command
case class Status(text: String) extends Command
