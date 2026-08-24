/*
 * A aplicação de linha de comandos de referência e a bateria de testes
 * automáticos que a mantém honesta.
 */

/**
 * O `po-uilib.jar`: a cópia em `libs/`, a do repositório importado ou a
 * instalação do sistema, que é onde ele está no ambiente de avaliação.
 */
val poUilibJar: File = run {
  val candidates = listOf(
    rootProject.file("libs/po-uilib.jar"),
    rootProject.file("po-uilib/po-uilib.jar"),
    File("/usr/share/java/po-uilib.jar"),
  )
  candidates.firstOrNull { it.isFile }
    ?: throw GradleException(
      "po-uilib.jar não encontrado. Coloque-o em libs/po-uilib.jar " +
        "(procurado em: ${candidates.joinToString(", ")})."
    )
}

sourceSets {
  main {
    // As fontes herdadas estão em `src/`, e não em `src/main/java`.
    java.setSrcDirs(listOf("src"))
    resources.setSrcDirs(emptyList<String>())
  }
  test {
    java.setSrcDirs(listOf("test"))
    resources.setSrcDirs(emptyList<String>())
  }
}

dependencies {
  implementation(project(":hva-core"))
  implementation(files(poUilibJar))

  testImplementation(platform("org.junit:junit-bom:5.11.4"))
  testImplementation("org.junit.jupiter:junit-jupiter")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

val autoTestsDir = rootProject.layout.projectDirectory.dir("tests/auto-tests")

tasks.test {
  useJUnitPlatform()

  // Os casos partilham um directório de trabalho e correm por ordem de nome:
  // há testes que abrem ficheiros de estado gravados por testes anteriores.
  maxParallelForks = 1

  systemProperty("hva.tests.dir", autoTestsDir.asFile.absolutePath)
  systemProperty("hva.work.dir", layout.buildDirectory.dir("auto-tests").get().asFile.absolutePath)
  systemProperty("hva.classpath", sourceSets.main.get().runtimeClasspath.asPath)

  // ./gradlew test -Ptests='A-19-*'
  (project.findProperty("tests") as String?)?.let { systemProperty("hva.tests.pattern", it) }

  inputs.dir(autoTestsDir).withPropertyName("autoTests")

  testLogging {
    events("failed")
    exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
  }
}
