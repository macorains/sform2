import sbt.{ExclusionRule, Resolver}

name := """sform2s"""
version := "1.0-SNAPSHOT"
scalaVersion := "2.13.3"

val playPac4jVersion = "11.0.0-PLAY2.8"
val pac4jVersion = "5.1.3"
val playVersion = "2.8.8"

resolvers ++= Seq(
  Resolver.mavenLocal,
  Resolver.jcenterRepo,
  "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
  "Sonatype snapshots repository" at "https://oss.sonatype.org/content/repositories/snapshots/",
  "Shibboleth releases" at "https://build.shibboleth.net/nexus/content/repositories/releases/",
  "Atlassian Releases" at "https://maven.atlassian.com/public/"
)

unmanagedBase := baseDirectory.value / "lib"

libraryDependencies ++= Seq(
  jdbc,
  ws,
  evolutions,
  ehcache,
  guice,
  filters,
  "org.scala-lang" % "scala-reflect" % "2.13.3",
  "mysql" % "mysql-connector-java" % "8.0.20",
  "org.scalikejdbc" %% "scalikejdbc" % "3.5.0",
  "org.scalikejdbc" %% "scalikejdbc-config" % "3.5.0",
  "org.scalikejdbc" %% "scalikejdbc-play-fixture" % "2.8.0-scalikejdbc-3.5",
  "org.scalikejdbc" %% "scalikejdbc-syntax-support-macro" % "3.5.0",
  "org.scalikejdbc" %% "scalikejdbc-joda-time" % "3.5.0",
  "org.scalikejdbc" %% "scalikejdbc-test" % "3.5.0"  % "test",
  "org.webjars" %% "webjars-play" % "2.8.0-1",
  "com.typesafe.play" %% "play-mailer" % "8.0.1",
  "com.typesafe.play" %% "play-mailer-guice" % "8.0.1",
  "net.codingwell" %% "scala-guice" % "4.2.10",
  "com.iheart" %% "ficus" % "1.4.7",
  "com.enragedginger" %% "akka-quartz-scheduler" % "1.9.0-akka-2.6.x",
  "com.adrianhurt" %% "play-bootstrap" % "1.6.1-P28-B4",
  "com.digitaltangible" %% "play-guard" % "2.5.0",
  "org.apache.commons" % "commons-lang3" % "3.10",
  "commons-io" % "commons-io" % "2.7" ,
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0",
  "org.mockito" %% "mockito-scala-scalatest" % "1.13.11",
  "com.typesafe.play" %% "play-test"% "2.8.1",
  "org.scalatest" %% "scalatest" % "3.1.2",

  "org.pac4j" %% "play-pac4j" % playPac4jVersion,
  "org.pac4j" % "pac4j-http" % pac4jVersion excludeAll(ExclusionRule(organization = "com.fasterxml.jackson.core")),
  "org.pac4j" % "pac4j-cas" % pac4jVersion exclude("com.fasterxml.jackson.core", "jackson-databind"),
  "org.pac4j" % "pac4j-oauth" % pac4jVersion excludeAll(ExclusionRule(organization = "com.fasterxml.jackson.core")),
  "org.pac4j" % "pac4j-saml" % pac4jVersion excludeAll(ExclusionRule(organization = "com.fasterxml.jackson.core")),
  "org.pac4j" % "pac4j-oidc" % pac4jVersion  excludeAll(ExclusionRule("commons-io" , "commons-io"), ExclusionRule(organization = "com.fasterxml.jackson.core")),
  "org.pac4j" % "pac4j-gae" % pac4jVersion,
  "org.pac4j" % "pac4j-jwt" % pac4jVersion exclude("commons-io" , "commons-io"),
  "org.pac4j" % "pac4j-ldap" % pac4jVersion excludeAll(ExclusionRule(organization = "com.fasterxml.jackson.core")),
  "org.pac4j" % "pac4j-sql" % pac4jVersion exclude("com.fasterxml.jackson.core", "jackson-databind"),
  "org.pac4j" % "pac4j-mongo" % pac4jVersion excludeAll(ExclusionRule(organization = "com.fasterxml.jackson.core")),
  "org.pac4j" % "pac4j-kerberos" % pac4jVersion exclude("org.springframework", "spring-core"),
  "org.pac4j" % "pac4j-couch" % pac4jVersion excludeAll(ExclusionRule(organization = "com.fasterxml.jackson.core")),
  "org.apache.shiro" % "shiro-core" % "1.7.1",
  "com.typesafe.play" % "play-cache_2.13" % playVersion,
  "commons-io" % "commons-io" % "2.11.0",
  "com.amazonaws" % "aws-java-sdk" % "1.11.1034",
  "com.amazonaws" % "aws-java-sdk-ses" % "1.11.1034"
)

excludeDependencies ++= Seq(
  ExclusionRule("com.google.appengine", "appengine-api-1.0-sdk"),
  ExclusionRule("com.google.appengine", "appengine-jsr107cache")
)

lazy val root: Project = (project in file("."))
  .enablePlugins(PlayScala)

routesGenerator := InjectedRoutesGenerator

routesImport += "net.macolabo.sform2.utils.route.Binders._"

// https://github.com/playframework/twirl/issues/105
TwirlKeys.templateImports := Seq()

scalacOptions ++= Seq(
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-feature", // Emit warning and location for usages of features that should be imported explicitly.
  "-unchecked", // Enable additional warnings where generated code depends on assumptions.
  "-Xfatal-warnings", // Fail the compilation if there are any warnings.
  "-Ywarn-dead-code", // Warn when dead code is identified.
  "-Ywarn-numeric-widen", // Warn when numerics are widened.
  "-Xlint:-unused,_"
)
