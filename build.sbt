import scala.collection.Seq

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.4.2"

lazy val root = project
    .in(file("."))
    .aggregate(toolbox.js, toolbox.jvm, toolbox.native)
    .settings(
      name             := "toolbox",
      version          := "0.1-SNAPSHOT",
      scalaVersion     := "3.4.2",
      idePackagePrefix := Some("com.rlemaitre.toolbox"),
      libraryDependencies ++= Seq(
        "org.scalameta" %% "munit"             % "1.0.0" % Test,
        "org.typelevel" %% "munit-cats-effect" % "2.0.0" % Test,
        "org.scalameta" %% "munit-scalacheck"  % "1.0.0" % Test
      ),
      publish          := {},
      publishLocal     := {}
    )

lazy val toolbox = crossProject(JSPlatform, JVMPlatform, NativePlatform)
    .in(file("."))
    .settings(
    )
    .jvmSettings(
      // Add JVM-specific settings here
    )
    .jsSettings(
      // Add JS-specific settings here
      scalaJSUseMainModuleInitializer := true
    )
    .nativeSettings(
      // Add Native-specific settings here
    )
