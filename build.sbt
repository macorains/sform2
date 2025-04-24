import sbt.{ExclusionRule, Resolver}

name := """sform2s"""
version := "1.0-SNAPSHOT"
scalaVersion := "2.13.16"
ThisBuild / organization := "net.macolabo"

javaOptions ++= Seq(
  "--add-opens=java.base/jdk.internal.misc=ALL-UNNAMED",
  "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED",
  "--add-exports=java.base/jdk.internal.misc=ALL-UNNAMED"
)

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
  scalaVersion := "2.13.16",
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
    scalaVersion := "2.13.16",
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
  scalaVersion := "2.13.16",
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
      //val akkaVersion = "2.6.21"
      val pac4jVersion = "6.1.1"
      val pekkoVersion = "1.0.3"
      val playPac4jVersion = "12.0.0-PLAY3.0"
      val playVersion = "3.0.7"
      val scalikeJdbcVersion = "3.5.0"

      //val akkaQuartzScheduler = "com.enragedginger" %% "akka-quartz-scheduler" % "1.9.0-akka-2.6.x"
      //val akkaTestkit = "com.typesafe.akka" %% "akka-testkit" % akkaVersion
      val awsJavaSdk = "com.amazonaws" % "aws-java-sdk" % "1.11.1034"
      val awsJavaSdkSes = "com.amazonaws" % "aws-java-sdk-ses" % "1.11.1034"
      val commonsIo = "commons-io" % "commons-io" % "2.11.0"
      val commonsLang3 = "org.apache.commons" % "commons-lang3" % "3.10"
      val ficus = "com.iheart" %% "ficus" % "1.4.7"
      val guice = "com.google.inject" % "guice" % "6.0.0"
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
      val pac4jCore = "org.pac4j" % "pac4j-core" % pac4jVersion excludeAll (ExclusionRule(organization = "com.fasterxml.jackson.core"))
      val pac4jGae = "org.pac4j" % "pac4j-gae" % pac4jVersion excludeAll (ExclusionRule(organization = "com.fasterxml.jackson.core"))
      val pac4jHttp = "org.pac4j" % "pac4j-http" % pac4jVersion excludeAll (ExclusionRule(organization = "com.fasterxml.jackson.core"))
      val pac4jJwt = "org.pac4j" % "pac4j-jwt" % pac4jVersion exclude("commons-io", "commons-io")
      val pac4jKerberos = "org.pac4j" % "pac4j-kerberos" % pac4jVersion exclude("org.springframework", "spring-core")
      val pac4jLdap = "org.pac4j" % "pac4j-ldap" % pac4jVersion excludeAll (ExclusionRule(organization = "com.fasterxml.jackson.core"))
      val pac4jMongo = "org.pac4j" % "pac4j-mongo" % pac4jVersion excludeAll (ExclusionRule(organization = "com.fasterxml.jackson.core"))
      val pac4jOauth = "org.pac4j" % "pac4j-oauth" % pac4jVersion excludeAll (ExclusionRule(organization = "com.fasterxml.jackson.core"))
      val pac4jOidc = "org.pac4j" % "pac4j-oidc" % pac4jVersion excludeAll(ExclusionRule("commons-io", "commons-io"), ExclusionRule(organization = "com.fasterxml.jackson.core"))
      val pac4jSaml = "org.pac4j" % "pac4j-saml" % pac4jVersion excludeAll (ExclusionRule(organization = "com.fasterxml.jackson.core"))
      val pac4jSql = "org.pac4j" % "pac4j-sql" % pac4jVersion exclude("com.fasterxml.jackson.core", "jackson-databind")
      val pekkoActorTyped = "org.apache.pekko" %% "pekko-actor-typed" % pekkoVersion
      val pekkoStream = "org.apache.pekko" %% "pekko-stream" % pekkoVersion
      val pekkoSlf4j = "org.apache.pekko" %% "pekko-slf4j" % pekkoVersion
      val pekkoHttpServer = "org.playframework" %% "play-pekko-http-server" % "3.0.0"
      val pekkoHttp2Support = "org.playframework" %% "play-pekko-http2-support" % "3.0.0"
      val playCache = "org.playframework" %% "play-cache" % playVersion
      val playGuice = "org.playframework" %% "play-guice" % playVersion
      val playJava = "org.playframework" %% "play-java" % playVersion
      val playJson = "org.playframework" %% "play-json" % "3.0.4"
      val playTest = "org.playframework" %% "play-test"% playVersion
      val playLogback = "org.playframework" %% "play-logback" % playVersion
      val playMailer = "org.playframework" %% "play-mailer" % "10.1.0"
      val playMailerGuice = "org.playframework" %% "play-mailer-guice" % "10.1.0"
      val playGuard = "com.digitaltangible" %% "play-guard" % "3.0.0"
      val playWs = "org.playframework" %% "play-ahc-ws" % playVersion
      val scalaGuice = "net.codingwell" %% "scala-guice" % "7.0.0"
      val scalaReflect = "org.scala-lang" % "scala-reflect" % "2.13.15"
      val scalaTest = "org.scalatest" %% "scalatest" % "3.2.19"
      val scalatestplusPlay = "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1"
      val scalikejdbc = "org.scalikejdbc" %% "scalikejdbc" % scalikeJdbcVersion
      val scalikejdbcConfig = "org.scalikejdbc" %% "scalikejdbc-config" % scalikeJdbcVersion
      val scalikejdbcJodaTime = "org.scalikejdbc" %% "scalikejdbc-joda-time" % scalikeJdbcVersion
      val scalikejdbcPlayFixture = "org.scalikejdbc" %% "scalikejdbc-play-fixture" % "2.8.0-scalikejdbc-3.5"
      val scalikejdbcSyntaxSupportMacro = "org.scalikejdbc" %% "scalikejdbc-syntax-support-macro" % scalikeJdbcVersion
      val scalikejdbcTest = "org.scalikejdbc" %% "scalikejdbc-test" % scalikeJdbcVersion
      val slf4jApi = "org.slf4j" % "slf4j-api" % "2.0.16"
      val shiroCore = "org.apache.shiro" % "shiro-core" % "1.7.1"
      val sslConfigCore = "com.typesafe" %% "ssl-config-core" % "0.6.1"
      val webjarsPlay = "org.webjars" %% "webjars-play" % "3.0.2"
    }

lazy val commonDependencies = Seq(
  jdbc,
  ws,
  evolutions,
  ehcache,
  filters,
  dependencies.awsJavaSdk,
  dependencies.awsJavaSdkSes,
  dependencies.commonsIo,
  dependencies.commonsLang3,
  dependencies.ficus,
  dependencies.jacksonAnnotations,
  dependencies.jacksonCore,
  dependencies.jacksonDatabind,
  dependencies.jacksonModuleScala,
  dependencies.logbackClassic,
  dependencies.mockitoScalaScalatest,
  dependencies.mysqlConnector,
  dependencies.pac4j,
  dependencies.pac4jCas,
  dependencies.pac4jCouch,
  dependencies.pac4jCore,
  dependencies.pac4jGae,
  dependencies.pac4jHttp,
  dependencies.pac4jJwt,
  dependencies.pac4jKerberos,
  dependencies.pac4jLdap,
  dependencies.pac4jMongo,
  dependencies.pac4jOauth,
  // dependencies.pac4jSaml,
  dependencies.pac4jOidc,
  dependencies.pac4jSql,
  dependencies.pekkoActorTyped,
  dependencies.pekkoHttpServer,
  dependencies.pekkoHttp2Support,
  dependencies.pekkoStream,
  dependencies.pekkoSlf4j,
  dependencies.playCache,
  dependencies.playGuard,
  dependencies.playGuice,
  dependencies.playJava,
  dependencies.playMailer,
  dependencies.playMailerGuice,
  dependencies.playLogback,
  dependencies.playTest,
  dependencies.playWs,
  dependencies.scalaGuice,
  dependencies.scalaReflect,
  dependencies.scalaTest,
  dependencies.scalatestplusPlay,
  dependencies.scalikejdbc,
  dependencies.scalikejdbcConfig,
  dependencies.scalikejdbcPlayFixture,
  dependencies.scalikejdbcSyntaxSupportMacro,
  dependencies.scalikejdbcJodaTime,
  dependencies.scalikejdbcTest % "test",
  //dependencies.guice,
  dependencies.shiroCore,
  dependencies.slf4jApi,
  dependencies.sslConfigCore,
  dependencies.webjarsPlay,
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
  "com.google.inject" % "guice" % "6.0.0"
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


