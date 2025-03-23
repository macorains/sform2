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
  commonDependencyOverrides,
  libraryDependencies ++= commonDependencies ++ Seq(
    dependencies.playJson
  )
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
    commonDependencyOverrides,
    libraryDependencies ++= commonDependencies
)
  .dependsOn(domain)
  .aggregate(domain)
  .enablePlugins(PlayScala)

lazy val formApi = Project(
  id = "form",
  base = file("formApi")
).settings(
  name := "formApi",
  version := "1.0-SNAPSHOT",
  scalaVersion := "2.13.3",
  commonResolvers,
  commonScalacOptions,
  commonExcludeDependencies,
  commonDependencyOverrides,
  libraryDependencies ++= commonDependencies
)
  .dependsOn(domain)
  .aggregate(domain)
  .enablePlugins(PlayScala)

lazy val dependencies =
  new
    {
      val akkaVersion = "2.6.21"
      val pac4jVersion = "5.7.2"
      val playPac4jVersion = "12.0.0-PLAY2.8"
      val playVersion = "2.8.20"
      val scalikeJdbcVersion = "3.5.0"

      val akkaQuartzScheduler = "com.enragedginger" %% "akka-quartz-scheduler" % "1.9.0-akka-2.6.x"
      val akkaTestkit = "com.typesafe.akka" %% "akka-testkit" % akkaVersion
      val awsJavaSdk = "com.amazonaws" % "aws-java-sdk" % "1.11.1034"
      val awsJavaSdkSes = "com.amazonaws" % "aws-java-sdk-ses" % "1.11.1034"
      val commonsIo = "commons-io" % "commons-io" % "2.11.0"
      val commonsLang3 = "org.apache.commons" % "commons-lang3" % "3.10"
      val ficus = "com.iheart" %% "ficus" % "1.4.7"
      val jacksonAnnotations = "com.fasterxml.jackson.core" % "jackson-annotations" % "2.14.3"
      val jacksonCore = "com.fasterxml.jackson.core" % "jackson-core" % "2.14.3"
      val jacksonDatabind = "com.fasterxml.jackson.core" % "jackson-databind" % "2.14.3"
      val jacksonModuleScala = "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.14.3"
      val logbackClassic = "ch.qos.logback" % "logback-classic" % "1.4.14"
      val mockitoScalaScalatest = "org.mockito" %% "mockito-scala-scalatest" % "1.13.11"
      val mysqlConnector = "mysql" % "mysql-connector-java" % "8.0.20"
      val pac4j = "org.pac4j" %% "play-pac4j" % playPac4jVersion
      val pac4jCas = "org.pac4j" % "pac4j-cas" % pac4jVersion exclude("com.fasterxml.jackson.core", "jackson-databind")
      val pac4jCouch = "org.pac4j" % "pac4j-couch" % pac4jVersion excludeAll (ExclusionRule(organization = "com.fasterxml.jackson.core"))
      val pac4jGae = "org.pac4j" % "pac4j-gae" % pac4jVersion
      val pac4jHttp = "org.pac4j" % "pac4j-http" % pac4jVersion excludeAll (ExclusionRule(organization = "com.fasterxml.jackson.core"))
      val pac4jJwt = "org.pac4j" % "pac4j-jwt" % pac4jVersion exclude("commons-io", "commons-io")
      val pac4jKerberos = "org.pac4j" % "pac4j-kerberos" % pac4jVersion exclude("org.springframework", "spring-core")
      val pac4jLdap = "org.pac4j" % "pac4j-ldap" % pac4jVersion excludeAll (ExclusionRule(organization = "com.fasterxml.jackson.core"))
      val pac4jMongo = "org.pac4j" % "pac4j-mongo" % pac4jVersion excludeAll (ExclusionRule(organization = "com.fasterxml.jackson.core"))
      val pac4jOauth = "org.pac4j" % "pac4j-oauth" % pac4jVersion excludeAll (ExclusionRule(organization = "com.fasterxml.jackson.core"))
      val pac4jOidc = "org.pac4j" % "pac4j-oidc" % pac4jVersion excludeAll(ExclusionRule("commons-io", "commons-io"), ExclusionRule(organization = "com.fasterxml.jackson.core"))
      val pac4jSaml = "org.pac4j" % "pac4j-saml" % pac4jVersion excludeAll (ExclusionRule(organization = "com.fasterxml.jackson.core"))
      val pac4jSql = "org.pac4j" % "pac4j-sql" % pac4jVersion exclude("com.fasterxml.jackson.core", "jackson-databind")
      val playBootstrap = "com.adrianhurt" %% "play-bootstrap" % "1.6.1-P28-B4"
      val playCache = "com.typesafe.play" % "play-cache_2.13" % playVersion
      val playJson = "com.typesafe.play" %% "play-json" % "2.9.2"
      val playTest = "com.typesafe.play" %% "play-test"% "2.8.1"
      val playLogback = "com.typesafe.play" %% "play-logback" % "2.9.0"
      val playMailer = "com.typesafe.play" %% "play-mailer" % "8.0.1"
      val playMailerGuice = "com.typesafe.play" %% "play-mailer-guice" % "8.0.1"
      val playGuard = "com.digitaltangible" %% "play-guard" % "2.5.0"
      var playWs = "com.typesafe.play" %% "play-ahc-ws" % "2.8.20"
      val scalaGuice = "net.codingwell" %% "scala-guice" % "4.2.10"
      val scalaReflect = "org.scala-lang" % "scala-reflect" % "2.13.3"
      val scalaTest = "org.scalatest" %% "scalatest" % "3.1.2"
      val scalatestplusPlay = "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0"
      val scalikejdbc = "org.scalikejdbc" %% "scalikejdbc" % scalikeJdbcVersion
      val scalikejdbcConfig = "org.scalikejdbc" %% "scalikejdbc-config" % scalikeJdbcVersion
      val scalikejdbcJodaTime = "org.scalikejdbc" %% "scalikejdbc-joda-time" % scalikeJdbcVersion
      val scalikejdbcPlayFixture = "org.scalikejdbc" %% "scalikejdbc-play-fixture" % "2.8.0-scalikejdbc-3.5"
      val scalikejdbcSyntaxSupportMacro = "org.scalikejdbc" %% "scalikejdbc-syntax-support-macro" % scalikeJdbcVersion
      val scalikejdbcTest = "org.scalikejdbc" %% "scalikejdbc-test" % scalikeJdbcVersion
      val slf4jApi = "org.slf4j" % "slf4j-api" % "2.0.12"
      val shiroCore = "org.apache.shiro" % "shiro-core" % "1.7.1"
      val sslConfigCore = "com.typesafe" %% "ssl-config-core" % "0.6.1"
      val webjarsPlay = "org.webjars" %% "webjars-play" % "2.8.0-1"
    }

lazy val commonDependencies = Seq(
  jdbc,
  ws,
  evolutions,
  ehcache,
  guice,
  filters,
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
  dependencies.awsJavaSdkSes,
  dependencies.akkaTestkit,
  dependencies.logbackClassic,
  dependencies.slf4jApi,
  dependencies.playLogback,
  dependencies.playWs,
  dependencies.jacksonCore,
  dependencies.jacksonDatabind,
  dependencies.jacksonAnnotations,
  dependencies.jacksonModuleScala,
  dependencies.sslConfigCore
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

lazy val commonDependencyOverrides = dependencyOverrides ++= Seq(
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.14.3",
  "org.pac4j" %% "pac4j-core" % "5.7.2",
  "org.pac4j" %% "pac4j-http" % "5.7.2",
  "org.pac4j" %% "pac4j-oidc" % "5.7.2",
  "org.pac4j" %% "pac4j-play" % "5.7.2"
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


