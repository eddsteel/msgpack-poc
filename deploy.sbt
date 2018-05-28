enablePlugins(JavaAppPackaging)
maintainer in Docker := "Edd Steel <edward.steel@gmail.com>"
packageName in Docker := "eddsteel/msgpackpoc"
packageSummary in Docker := "MsgPack but scala"
packageDescription := "MsgPack but scala"
dockerExposedPorts := List(8080)
dockerBaseImage := "frolvlad/alpine-scala"
dockerUpdateLatest := true
