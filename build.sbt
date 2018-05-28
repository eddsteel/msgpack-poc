// scalafmt: {align.tokens = ["%", "%%", ":=", "+="]}
name         := "msgpack-poc"
description  := "MsgPack but scala"
scalaVersion := "2.12.4"
organization := "com.eddsteel"
version      := "slice1"

licenses := List(
  ("GPL version 3", url("https://www.gnu.org/licenses/gpl-3.0.en.html")))
homepage := Some(url("https://github.com/eddsteel/msgpack-poc"))
developers := List(
  Developer("eddsteel",
            "Edd Steel",
            "edward.steel@gmail.com",
            url("https://github.com/eddsteel/msgpack-poc")))
scmInfo := Some(
  ScmInfo(url("https://github.com/eddsteel/msgpack-poc"),
          "scm:git:https://github.com/eddsteel/msgpack-poc.git"))

libraryDependencies += "org.typelevel"  %% "cats-core"   % "1.1.0"
libraryDependencies += "org.msgpack"    % "msgpack-core" % "0.8.16"
libraryDependencies += "com.chuusai"    %% "shapeless"   % "2.3.3"
libraryDependencies += "org.scalatest"  %% "scalatest"   % "3.0.4" % "test"
libraryDependencies += "org.scalacheck" %% "scalacheck"  % "1.13.5" % "test"

Lint.settings
Flags.settings
Format.settings

initialCommands in console := Seq(
  "import com.eddsteel.msgpackpoc._",
  "import implicits._",
  "import _root_.org.msgpack.core").mkString("\n")
