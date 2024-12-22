ThisBuild / scalaVersion := "2.13.14"
ThisBuild / name := (cli / name).value
name := (ThisBuild / name).value

val V = new {
  val betterMonadicFor = "0.3.1"
  val bouncyCastle = "1.78.1"
  val catsEffect = "3.5.4"
  val fs2 = "3.11.0"
  val http4s = "0.23.27"
  val http4sErrors = "0.5.1"
  val log4s = "1.10.0"
  val logbackClassic = "1.5.15"
  val munit = "0.7.29"
  val munitTaglessFinal = "0.2.0"
  val nativeimage = "24.0.2"
}

lazy val commonSettings: Seq[Setting[_]] = Seq(
  organization := "de.bitmarck.bms",
  version := {
    val Tag = "refs/tags/v?([0-9]+(?:\\.[0-9]+)+(?:[+-].*)?)".r
    sys.env.get("CI_VERSION").collect { case Tag(tag) => tag }
      .getOrElse("0.0.1-SNAPSHOT")
  },
  addCompilerPlugin("com.olegpy" %% "better-monadic-for" % V.betterMonadicFor),
  libraryDependencies ++= Seq(
    "org.graalvm.nativeimage" % "svm" % V.nativeimage % Provided,
    "ch.qos.logback" % "logback-classic" % V.logbackClassic % Test,
    "de.lolhens" %% "munit-tagless-final" % V.munitTaglessFinal % Test,
    "org.scalameta" %% "munit" % V.munit % Test,
  ),
  testFrameworks += new TestFramework("munit.Framework"),
  assembly / assemblyJarName := s"${name.value}-${version.value}.sh.bat",
  assembly / assemblyOption := (assembly / assemblyOption).value
    .withPrependShellScript(Some(AssemblyPlugin.defaultUniversalScript(shebang = false))),
  assembly / assemblyMergeStrategy := {
    case PathList(paths@_*) if paths.last == "module-info.class" => MergeStrategy.discard
    case PathList("META-INF", path @ _*) if path.contains("OSGI-INF") => MergeStrategy.discard
    case x =>
      val oldStrategy = (assembly / assemblyMergeStrategy).value
      oldStrategy(x)
  },
)

lazy val root = project.in(file("."))
  .settings(
    publishArtifact := false
  )
  .aggregate(cli, server, core)

lazy val core = project
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "co.fs2" %% "fs2-io" % V.fs2,
      "org.bouncycastle" % "bcpkix-jdk18on" % V.bouncyCastle,
    ),
  )

lazy val cli = project
  .enablePlugins(BuildInfoPlugin)
  .enablePlugins(GraalVMNativeImagePlugin)
  .dependsOn(core)
  .settings(commonSettings)
  .settings(
    name := "cms-content-extractor-cli",

    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % V.logbackClassic,
      "org.log4s" %% "log4s" % V.log4s,
    ),

    GraalVMNativeImage / name := (GraalVMNativeImage / name).value + "-" + (GraalVMNativeImage / version).value,
    graalVMNativeImageOptions ++= Seq(
      "--static",
      "--no-server",
      "--no-fallback",
      "--initialize-at-build-time",
      "--install-exit-handlers"
    )
  )

lazy val server = project
  .enablePlugins(BuildInfoPlugin)
  .enablePlugins(GraalVMNativeImagePlugin)
  .dependsOn(core)
  .settings(commonSettings)
  .settings(
    name := "cms-content-extractor-ws",

    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % V.logbackClassic,
      "de.lhns" %% "http4s-errors" % V.http4sErrors,
      "org.http4s" %% "http4s-ember-server" % V.http4s,
      "org.http4s" %% "http4s-circe" % V.http4s,
      "org.http4s" %% "http4s-dsl" % V.http4s,
      "org.log4s" %% "log4s" % V.log4s,
      "org.typelevel" %% "cats-effect" % V.catsEffect,
    ),
  )
