import sbt._
import sbt.Keys._
import wartremover._

object Lint { // avoid namie conflicts
  def settings: Seq[Setting[_]] =
    Seq(
      wartremoverErrors in (Compile, compile) ++= Warts.allBut(
        Wart.ImplicitParameter // not really an option with scala Futures.
      )
      // Add excludes as needed (e.g. Java stuff).
      // wartremoverExcluded += baseDirectory.value / "src" / "main" / "scala" / ""
    )
}
