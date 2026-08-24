pluginManagement {
  repositories {
    // O plugin do TeaVM é publicado no Maven Central, não no portal do Gradle.
    mavenCentral()
    gradlePluginPortal()
  }
}

rootProject.name = "quatro-estacoes"

include("hva-core")
include("hva-app")
include("hva-web")
