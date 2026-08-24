/*
 * O *spike* da fase 1: o `hva-core` compilado para JavaScript pelo TeaVM.
 *
 * A pergunta é uma só — o núcleo sobrevive à travessia e o JavaScript consegue
 * comandá-lo? A resposta é `./gradlew :hva-web:spike`, que corre o mesmo
 * cenário na JVM e no browser e compara as duas saídas.
 */

plugins {
  application
  id("org.teavm") version "0.12.3"
}

sourceSets {
  main {
    java.setSrcDirs(listOf("src"))
    resources.setSrcDirs(emptyList<String>())
  }
}

dependencies {
  implementation(project(":hva-core"))

  // `@JSExport` e `@JSClass`: as anotações que levam a `Bridge` para o módulo
  // gerado. Só existem em tempo de compilação — não vão para a JVM.
  compileOnly(teavm.libs.jso)
}

application {
  mainClass.set("hva.web.Spike")
}

teavm {
  js {
    mainClass.set("hva.web.Spike")
    targetFileName.set("spike.js")

    // Legível de propósito: enquanto isto é um *spike*, um erro no JavaScript
    // gerado tem de poder ser lido.
    obfuscated.set(false)
    sourceMap.set(true)

    // Módulo ES2015: o mesmo ficheiro serve o `node` e o `<script type="module">`.
    moduleType.set(org.teavm.gradle.api.JSModuleType.ES2015)
  }
}

val generatedJs = layout.buildDirectory.file("generated/teavm/js/spike.js")
val jvmReport = layout.buildDirectory.file("spike/jvm.txt")
val jsReport = layout.buildDirectory.file("spike/js.txt")

/** Corre o cenário na JVM e guarda o relatório de um ano. */
val spikeOnJvm = tasks.register<JavaExec>("spikeOnJvm") {
  group = "verification"
  description = "Corre o cenário do spike na JVM."
  classpath = sourceSets.main.get().runtimeClasspath
  mainClass.set("hva.web.Spike")
  outputs.file(jvmReport)
  doFirst {
    val file = jvmReport.get().asFile
    file.parentFile.mkdirs()
    standardOutput = file.outputStream()
  }
}

/**
 * Corre o mesmo cenário em JavaScript, sobre o `node`, e guarda o relatório.
 *
 * Passa pela ponte exportada, e não pelo `main`: assim uma só comparação prova
 * as duas coisas — que o domínio atravessou intacto e que a API que o
 * JavaScript vê devolve o que devia.
 */
val spikeOnNode = tasks.register<Exec>("spikeOnNode") {
  group = "verification"
  description = "Corre o cenário do spike em JavaScript, sobre o node."
  dependsOn(tasks.named("generateJavaScript"))
  inputs.file(generatedJs)
  inputs.file(file("check/report.mjs"))
  outputs.file(jsReport)

  commandLine("node", file("check/report.mjs").absolutePath, generatedJs.get().asFile.toURI().toString())

  doFirst {
    val report = jsReport.get().asFile
    report.parentFile.mkdirs()
    standardOutput = report.outputStream()
  }
}

/** Verifica que o JavaScript consegue ler e alterar o hotel, não só lê-lo. */
val spikeBridge = tasks.register<Exec>("spikeBridge") {
  group = "verification"
  description = "Verifica o ciclo ler-mexer-voltar a ler a partir de JavaScript."
  dependsOn(tasks.named("generateJavaScript"))
  inputs.file(generatedJs)
  inputs.file(file("check/bridge.mjs"))

  commandLine("node", file("check/bridge.mjs").absolutePath, generatedJs.get().asFile.toURI().toString())
}

/** O *gate* da fase 1: as duas saídas têm de ser iguais. */
tasks.register("spike") {
  group = "verification"
  description = "Corre o cenário na JVM e em JavaScript e compara as saídas."
  dependsOn(spikeOnJvm, spikeOnNode, spikeBridge)

  doLast {
    val onJvm = jvmReport.get().asFile.readText().trim().lines()
    val onJs = jsReport.get().asFile.readText().trim().lines()

    val divergence = onJvm.zip(onJs).indexOfFirst { (a, b) -> a != b }
    if (divergence < 0 && onJvm.size == onJs.size) {
      logger.lifecycle("O núcleo atravessou: ${onJvm.size} linhas idênticas na JVM e em JavaScript.")
      return@doLast
    }

    val at = if (divergence >= 0) divergence else minOf(onJvm.size, onJs.size)
    throw GradleException(
      "A saída em JavaScript diverge da saída na JVM, na linha ${at + 1}:\n" +
        "  JVM: ${onJvm.getOrNull(at) ?: "(fim)"}\n" +
        "  JS:  ${onJs.getOrNull(at) ?: "(fim)"}\n" +
        "  relatórios: ${jvmReport.get().asFile} e ${jsReport.get().asFile}"
    )
  }
}

/**
 * Monta a página, com o JavaScript gerado lá dentro.
 *
 * O ficheiro sai auto-contido de propósito: é assim que se percebe o que a
 * arquitectura promete — alojamento estático, um ficheiro, sem servidor.
 *
 * O TeaVM emite um módulo ES2015. A linha de exportação é substituída por uma
 * atribuição a `globalThis`, o que dispensa `type="module"` e o `import()`
 * dinâmico de um `blob:` que algumas políticas de segurança recusam.
 */
tasks.register("webPage") {
  group = "build"
  description = "Monta a página auto-contida em build/web/index.html."
  dependsOn(tasks.named("generateJavaScript"))

  val template = file("web/index.html")
  val output = layout.buildDirectory.file("web/index.html")
  inputs.file(template)
  inputs.file(generatedJs)
  outputs.file(output)

  doLast {
    val exported = Regex("""^export\s*\{([^}]*)}\s*;?\s*$""", RegexOption.MULTILINE)
    val script = generatedJs.get().asFile.readText()

    val exports = exported.find(script)
      ?: throw GradleException("Não encontrei a linha de exportação em ${generatedJs.get().asFile}.")

    // `$rt_export_main as main, $rt_export_class_Hotel_23 as Hotel`
    val bindings = exports.groupValues[1].split(",").joinToString(", ") { pair ->
      val (internal, exposed) = pair.trim().split(Regex("""\s+as\s+"""))
      "$exposed: $internal"
    }

    val asGlobals = script.replaceRange(
      exports.range,
      "globalThis.HVA = { $bindings };"
    )

    val page = template.readText().replace("@TEAVM@", asGlobals)
    val file = output.get().asFile
    file.parentFile.mkdirs()
    file.writeText(page)
    logger.lifecycle("Página montada: $file (${file.length() / 1024} KB)")
  }
}
