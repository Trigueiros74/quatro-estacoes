sourceSets {
  main {
    java.setSrcDirs(listOf("src"))
    resources.setSrcDirs(emptyList<String>())
  }
  test {
    java.setSrcDirs(listOf("test"))
    resources.setSrcDirs(emptyList<String>())
  }
}

dependencies {
  testImplementation(platform("org.junit:junit-bom:5.11.4"))
  testImplementation("org.junit.jupiter:junit-jupiter")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
  useJUnitPlatform()
  testLogging {
    events("failed")
    exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
  }
}
