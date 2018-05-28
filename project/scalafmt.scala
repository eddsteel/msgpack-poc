import sbt._
import sbt.Keys._
import com.lucidchart.sbt.scalafmt.ScalafmtCorePlugin.autoImport._

object Format {
  def settings: Seq[Setting[_]] =
    Seq(
      scalafmtOnCompile in ThisBuild := true
    )
}
