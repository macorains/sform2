import sbt.{ExclusionRule, Resolver}

name := """sform2s"""
version := "1.0-SNAPSHOT"
scalaVersion := "2.13.3"
ThisBuild / organization := "net.macolabo"


/*
lazy val global = (project in file("."))
  .settings(
    commonResolvers,
    commonScalacOptions,
    commonExcludeDependencies,
    libraryDependencies ++= commonDependencies
  )
  .aggregate(
    domain,
    adminApi
  )
  .dependsOn(
    domain,
    adminApi
  )
*/

lazy val domain = Project(
  id = "domain",
  base = file("domain")
).settings(
  name := "domain",
  version := "1.0-SNAPSHOT",
  scalaVersion := "2.13.3",
  commonResolvers,
  commonScalacOptions,
  commonExcludeDependencies,
  libraryDependencies ++= commonDependencies,
)

lazy val adminApi = Project(
  id = "admin",
  base = file("adminApi")
).settings(
    name := "adminApi",
    version := "1.0-SNAPSHOT",
    scalaVersion := "2.13.3",
    commonResolvers,
    commonScalacOptions,
    commonExcludeDependencies,
    libraryDependencies ++= commonDependencies
)
  .dependsOn(domain)
  .aggregate(domain)
  .enablePlugins(PlayScala)

lazy val dependencies =
  new
    {
      val playPac4jVersion = "11.0.0-PLAY2.8"
      val pac4jVersion = "5.1.3"
      val playVersion = "2.8.8"
      val scalikeJdbcVersion = "3.5.0"

      val scalaReflect = "org.scala-lang" % "scala-reflect" % "2.13.3"
      val mysqlConnector = "mysql" % "mysql-connector-java" % "8.0.20"
      val scalikejdbc = "org.scalikejdbc" %% "scalikejdbc" % scalikeJdbcVersion
      val scalikejdbcConfig = "org.scalikejdbc" %% "scalikejdbc-config" % scalikeJdbcVersion
      val scalikejdbcPlayFixture = "org.scalikejdbc" %% "scalikejdbc-play-fixture" % "2.8.0-scalikejdbc-3.5"
      val scalikejdbcSyntaxSupportMacro = "org.scalikejdbc" %% "scalikejdbc-syntax-support-macro" % scalikeJdbcVersion
      val scalikejdbcJodaTime = "org.scalikejdbc" %% "scalikejdbc-joda-time" % scalikeJdbcVersion
      val scalikejdbcTest = "org.scalikejdbc" %% "scalikejdbc-test" % scalikeJdbcVersion
      val webjarsPlay = "org.webjars" %% "webjars-play" % "2.8.0-1"
      val playMailer = "com.typesafe.play" %% "play-mailer" % "8.0.1"
      val playMailerGuice = "com.typesafe.play" %% "play-mailer-guice" % "8.0.1"
      val scalaGuice = "net.codingwell" %% "scala-guice" % "4.2.10"
      val ficus = "com.iheart" %% "ficus" % "1.4.7"
      val akkaQuartzScheduler = "com.enragedginger" %% "akka-quartz-scheduler" % "1.9.0-akka-2.6.x"
      val playBootstrap = "com.adrianhurt" %% "play-bootstrap" % "1.6.1-P28-B4"
      val playGuard = "com.digitaltangible" %% "play-guard" % "2.5.0"
      val commonsLang3 = "org.apache.commons" % "commons-lang3" % "3.10"
      val commonsIo = "commons-io" % "commons-io" % "2.11.0"
      val scalatestplusPlay = "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0"
      val mockitoScalaScalatest = "org.mockito" %% "mockito-scala-scalatest" % "1.13.11"
      val playTest = "com.typesafe.play" %% "play-test"% "2.8.1"
      val scalaTest = "org.scalatest" %% "scalatest" % "3.1.2"

      val pac4j = "org.pac4j" %% "play-pac4j" % playPac4jVersion
      val pac4jHttp = "org.pac4j" % "pac4j-http" % pac4jVersion excludeAll(ExclusionRule(organization = "com.fasterxml.jackson.core"))
      val pac4jCas = "org.pac4j" % "pac4j-cas" % pac4jVersion exclude("com.fasterxml.jackson.core", "jackson-databind")
      val pac4jOauth = "org.pac4j" % "pac4j-oauth" % pac4jVersion excludeAll(ExclusionRule(organization = "com.fasterxml.jackson.core"))
      val pac4jSaml = "org.pac4j" % "pac4j-saml" % pac4jVersion excludeAll(ExclusionRule(organization = "com.fasterxml.jackson.core"))
      val pac4jOidc = "org.pac4j" % "pac4j-oidc" % pac4jVersion  excludeAll(ExclusionRule("commons-io" , "commons-io"), ExclusionRule(organization = "com.fasterxml.jackson.core"))
      val pac4jGae = "org.pac4j" % "pac4j-gae" % pac4jVersion
      val pac4jJwt = "org.pac4j" % "pac4j-jwt" % pac4jVersion exclude("commons-io" , "commons-io")
      val pac4jLdap = "org.pac4j" % "pac4j-ldap" % pac4jVersion excludeAll(ExclusionRule(organization = "com.fasterxml.jackson.core"))
      val pac4jSql = "org.pac4j" % "pac4j-sql" % pac4jVersion exclude("com.fasterxml.jackson.core", "jackson-databind")
      val pac4jMongo = "org.pac4j" % "pac4j-mongo" % pac4jVersion excludeAll(ExclusionRule(organization = "com.fasterxml.jackson.core"))
      val pac4jKerberos = "org.pac4j" % "pac4j-kerberos" % pac4jVersion exclude("org.springframework", "spring-core")
      val pac4jCouch = "org.pac4j" % "pac4j-couch" % pac4jVersion excludeAll(ExclusionRule(organization = "com.fasterxml.jackson.core"))
      val shiroCore = "org.apache.shiro" % "shiro-core" % "1.7.1"
      val playCache = "com.typesafe.play" % "play-cache_2.13" % playVersion
      val awsJavaSdk = "com.amazonaws" % "aws-java-sdk" % "1.11.1034"
      val awsJavaSdkSes = "com.amazonaws" % "aws-java-sdk-ses" % "1.11.1034"
    }

lazy val commonDependencies = Seq(
  dependencies.scalaReflect,
  dependencies.mysqlConnector,
  dependencies.scalikejdbc,
  dependencies.scalikejdbcConfig,
  dependencies.scalikejdbcPlayFixture,
  dependencies.scalikejdbcSyntaxSupportMacro,
  dependencies.scalikejdbcJodaTime,
  dependencies.scalikejdbcTest % "test",
  dependencies.webjarsPlay,
  dependencies.playMailer,
  dependencies.playMailerGuice,
  dependencies.scalaGuice,
  dependencies.ficus,
  dependencies.akkaQuartzScheduler,
  dependencies.playBootstrap,
  dependencies.playGuard,
  dependencies.commonsLang3,
  dependencies.commonsIo,
  dependencies.scalatestplusPlay,
  dependencies.mockitoScalaScalatest,
  dependencies.playTest,
  dependencies.scalaTest,
  dependencies.pac4j,
  dependencies.pac4jHttp,
  dependencies.pac4jCas,
  dependencies.pac4jOauth,
  // dependencies.pac4jSaml,
  dependencies.pac4jOidc,
  dependencies.pac4jGae,
  dependencies.pac4jJwt,
  dependencies.pac4jLdap,
  dependencies.pac4jSql,
  dependencies.pac4jMongo,
  dependencies.pac4jKerberos,
  dependencies.pac4jCouch,
  dependencies.shiroCore,
  dependencies.playCache,
  dependencies.awsJavaSdk,
  dependencies.awsJavaSdkSes
)


lazy val commonResolvers = resolvers ++= Seq(
  Resolver.mavenLocal,
  Resolver.jcenterRepo,
  "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
  "Sonatype snapshots repository" at "https://oss.sonatype.org/content/repositories/snapshots/",
  "Shibboleth releases" at "https://build.shibboleth.net/nexus/content/repositories/releases/",
  "Atlassian Releases" at "https://maven.atlassian.com/public/"
)

lazy val commonExcludeDependencies = excludeDependencies ++= Seq(
  ExclusionRule("com.google.appengine", "appengine-api-1.0-sdk"),
  ExclusionRule("com.google.appengine", "appengine-jsr107cache")
)

lazy val commonScalacOptions = scalacOptions ++= Seq(
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-feature", // Emit warning and location for usages of features that should be imported explicitly.
  "-unchecked", // Enable additional warnings where generated code depends on assumptions.
  "-Xfatal-warnings", // Fail the compilation if there are any warnings.
  "-Ywarn-dead-code", // Warn when dead code is identified.
  "-Ywarn-numeric-widen", // Warn when numerics are widened.
  "-Xlint:-unused,_"
)

// unmanagedBase := baseDirectory.value / "lib"

routesGenerator := InjectedRoutesGenerator
// routesImport += "net.macolabo.sform2.domain.utils.route.Binders._"

// https://github.com/playframework/twirl/issues/105
TwirlKeys.templateImports := Seq()


