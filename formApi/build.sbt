import sbt.Resolver

name := """sform-api"""
organization := "net.macolabo"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)
routesGenerator := InjectedRoutesGenerator
routesImport += "net.macolabo.sform.api.utils.route.Binders._"

scalaVersion := "2.13.3"

val playPac4jVersion = "11.0.0-PLAY2.8"
val pac4jVersion = "5.1.3"
val playVersion = "2.8.8"
val AkkaVersion = "2.6.14"

resolvers ++= Seq(
  Resolver.mavenLocal,
  Resolver.jcenterRepo,
  "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
  "Sonatype snapshots repository" at "https://oss.sonatype.org/content/repositories/snapshots/",
  "Shibboleth releases" at "https://build.shibboleth.net/nexus/content/repositories/releases/",
  "Atlassian Releases" at "https://maven.atlassian.com/public/"
)

libraryDependencies ++= Seq(
  filters,
  jdbc,
  ws,
  evolutions,
  ehcache,
  "org.scala-lang" % "scala-reflect" % "2.13.3",
  "mysql" % "mysql-connector-java" % "8.0.20",
  "org.scalikejdbc" %% "scalikejdbc" % "3.5.0",
  "org.scalikejdbc" %% "scalikejdbc-config" % "3.5.0",
  "org.scalikejdbc" %% "scalikejdbc-syntax-support-macro" % "3.5.0",
  "org.scalikejdbc" %% "scalikejdbc-joda-time" % "3.5.0",
  "org.scalikejdbc" %% "scalikejdbc-test" % "3.5.0",
  "net.codingwell" %% "scala-guice" % "4.2.10",
  "com.iheart" %% "ficus" % "1.4.7",
  "org.webjars" %% "webjars-play" % "2.8.0-1",
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
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0",
  "org.mockito" %% "mockito-scala-scalatest" % "1.13.11",
  "com.typesafe.play" %% "play-test"% "2.8.1",
  "com.typesafe.akka" %% "akka-testkit" % AkkaVersion % Test,
  "org.scalatest" %% "scalatest" % "3.1.2",
  "com.amazonaws" % "aws-java-sdk" % "1.11.1034",
  "com.amazonaws" % "aws-java-sdk-ses" % "1.11.1034",
  guice,
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "net.macolabo.controllers._"
TwirlKeys.templateImports := Seq()

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "net.macolabo.binders._"
