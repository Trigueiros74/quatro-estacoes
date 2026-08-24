/*
 * Configuração comum a todos os módulos.
 *
 * A raiz não tem código: os módulos herdados do projecto de Programação com
 * Objectos têm as fontes directamente em `src/`, e não em `src/main/java`, pelo
 * que a disposição habitual do Gradle é redefinida em cada um deles.
 */

subprojects {
  apply(plugin = "java")

  repositories {
    mavenCentral()
  }

  tasks.withType<JavaCompile>().configureEach {
    // `release` em vez de uma toolchain: qualquer JDK 17 ou posterior serve
    // para construir, e o código fica preso à API do Java 17.
    options.release.set(17)
    options.encoding = "UTF-8"

    // Dois avisos ficam de fora; os restantes são erros.
    //
    //   serial — acusa todos os campos declarados com o tipo da interface
    //     (`Set`, `Map`, `List`) sem reparar que as implementações usadas são
    //     serializáveis;
    //   try — acusa o `try (var ui = Dialog.UI)` do `App`, onde o recurso só
    //     existe para ser fechado no fim. O `Dialog.UI` não é final, pelo que
    //     nem sequer pode ser usado na forma sem variável.
    options.compilerArgs.addAll(listOf("-Xlint:all,-serial,-try", "-Werror"))
  }

  tasks.withType<Javadoc>().configureEach {
    options.encoding = "UTF-8"
  }
}
