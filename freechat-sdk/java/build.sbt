lazy val root = (project in file(".")).
  settings(
    organization := "fun.freechat",
    name := "freechat-sdk",
    version := "2.2.0",
    scalaVersion := "2.13.15",
    scalacOptions ++= Seq("-feature"),
    javacOptions in compile ++= Seq("-Xlint:deprecation"),
    publishArtifact in (Compile, packageDoc) := false,
    resolvers += Resolver.mavenLocal,
    libraryDependencies ++= Seq(
      "io.swagger" % "swagger-annotations" % "1.6.14",
      "com.squareup.okhttp3" % "okhttp" % "4.12.0",
      "com.squareup.okhttp3" % "logging-interceptor" % "4.12.0",
      "com.google.code.gson" % "gson" % "2.11.0",
      "org.apache.commons" % "commons-lang3" % "3.17.0",
      "jakarta.ws.rs" % "jakarta.ws.rs-api" % "4.0.0",
      "org.openapitools" % "jackson-databind-nullable" % "0.2.6",
      "io.gsonfire" % "gson-fire" % "1.9.0" % "compile",
      "jakarta.annotation" % "jakarta.annotation-api" % "3.0.0" % "compile",
      "com.google.code.findbugs" % "jsr305" % "3.0.2" % "compile",
      "jakarta.annotation" % "jakarta.annotation-api" % "3.0.0" % "compile",
      "org.junit.jupiter" % "junit-jupiter-api" % "5.11.4" % "test",
      "com.novocode" % "junit-interface" % "0.11" % "test",
      "org.mockito" % "mockito-core" % "5.14.2" % "test"
    )
  )
