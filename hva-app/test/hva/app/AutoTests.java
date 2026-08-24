package hva.app;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

/**
 * A bateria de testes automáticos herdada da cadeira, corrida a partir do
 * Gradle.
 *
 * <p>Cada caso é um processo próprio, tal como no {@code run-tests.sh}: a
 * aplicação lê o guião de {@code -Din} e escreve a resposta em {@code -Dout}. A
 * comparação replica a da avaliação — {@code diff -iwub -B} sobre a saída com
 * os espaços colapsados —, o que na prática é ignorar todo o espaço em branco e
 * a distinção entre maiúsculas e minúsculas.
 *
 * <p>Os casos partilham um directório de trabalho e correm por ordem de nome
 * porque assim o exige o próprio guião: há testes que abrem ficheiros de estado
 * gravados por testes anteriores (por exemplo, {@code A-01-14} abre o
 * {@code ap01.dat} que o {@code A-01-13} gravou).
 */
class AutoTests {

  /** Tempo máximo concedido a um caso antes de ser dado como bloqueado. */
  private static final int TIMEOUT_SECONDS = 60;

  private static final Path TESTS = Path.of(System.getProperty("hva.tests.dir"));
  private static final Path WORK = Path.of(System.getProperty("hva.work.dir"));
  private static final String CLASSPATH = System.getProperty("hva.classpath");
  private static final String JAVA =
      Path.of(System.getProperty("java.home"), "bin", "java").toString();

  /**
   * Prepara um directório de trabalho limpo com os guiões e os ficheiros de
   * importação, deixando de fora as saídas que os casos vierem a produzir.
   */
  @BeforeAll
  static void prepareWorkDirectory() throws IOException {
    deleteRecursively(WORK);
    Files.createDirectories(WORK);
    for (Path source : listFiles(TESTS, "*.{in,import}"))
      Files.copy(source, WORK.resolve(source.getFileName()), StandardCopyOption.REPLACE_EXISTING);
  }

  /** @return um caso de teste por cada guião com saída esperada. */
  @TestFactory
  Stream<DynamicTest> autoTests() throws IOException {
    String pattern = System.getProperty("hva.tests.pattern", "*");
    PathMatcher filter = FileSystems.getDefault().getPathMatcher("glob:" + pattern + ".in");

    List<DynamicTest> cases = new ArrayList<>();
    for (Path script : listFiles(TESTS, "*.in")) {
      if (!filter.matches(script.getFileName()))
        continue;
      String name = stripExtension(script.getFileName().toString());
      Path expected = TESTS.resolve("expected").resolve(name + ".out");
      if (Files.isRegularFile(expected))
        cases.add(dynamicTest(name, () -> runCase(name, expected)));
    }

    if (cases.isEmpty())
      fail("Nenhum caso de teste corresponde ao padrão '" + pattern + "'.");

    return cases.stream();
  }

  /**
   * Corre um caso e confronta a saída com a esperada.
   *
   * @param name     o nome do caso, sem extensão
   * @param expected o ficheiro com a saída esperada
   */
  private void runCase(String name, Path expected) throws Exception {
    Path obtained = WORK.resolve(name + ".outhyp");
    Files.deleteIfExists(obtained);

    List<String> command = new ArrayList<>(List.of(JAVA, "-cp", CLASSPATH));
    if (Files.isRegularFile(WORK.resolve(name + ".import")))
      command.add("-Dimport=" + name + ".import");
    command.add("-Din=" + name + ".in");
    command.add("-Dout=" + name + ".outhyp");
    command.add("hva.app.App");

    Process process = new ProcessBuilder(command)
        .directory(WORK.toFile())
        .redirectErrorStream(true)
        .start();

    String console;
    try (var output = process.getInputStream()) {
      console = new String(output.readAllBytes(), StandardCharsets.UTF_8);
    }

    if (!process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
      process.destroyForcibly();
      fail(name + ": a aplicação não terminou em " + TIMEOUT_SECONDS + "s.");
    }

    if (!Files.isRegularFile(obtained))
      fail(name + ": a aplicação não produziu saída." + consoleReport(console));

    String want = normalize(Files.readString(expected));
    String got = normalize(Files.readString(obtained));
    if (!want.equals(got))
      fail(name + ": a saída difere da esperada.\n"
          + difference(want, got)
          + "\n  esperada: " + expected
          + "\n  obtida:   " + obtained
          + consoleReport(console));
  }

  /**
   * Reduz uma saída à sua parte significativa: sem espaço em branco e sem
   * distinção entre maiúsculas e minúsculas, como faz o {@code diff -iw}.
   *
   * @param text a saída a normalizar
   * @return a saída normalizada
   */
  private static String normalize(String text) {
    return text.replaceAll("\\s+", "").toLowerCase(Locale.ROOT);
  }

  /**
   * @param want a saída esperada, normalizada
   * @param got  a saída obtida, normalizada
   * @return uma descrição do primeiro ponto em que as duas divergem
   */
  private static String difference(String want, String got) {
    int at = 0;
    while (at < want.length() && at < got.length() && want.charAt(at) == got.charAt(at))
      at++;
    return "  primeira divergência ao caractere " + at + ":\n"
        + "    esperado: ..." + excerpt(want, at) + "...\n"
        + "    obtido:   ..." + excerpt(got, at) + "...";
  }

  /**
   * @param text  o texto de onde extrair
   * @param at    a posição em torno da qual extrair
   * @return um excerto do texto em torno da posição indicada
   */
  private static String excerpt(String text, int at) {
    return text.substring(Math.max(0, at - 30), Math.min(text.length(), at + 30));
  }

  /**
   * @param console o que a aplicação escreveu na consola
   * @return um relato do que a aplicação escreveu, ou nada se ela se calou
   */
  private static String consoleReport(String console) {
    return console.isBlank() ? "" : "\n  consola:\n" + console.strip().indent(4);
  }

  /**
   * @param directory o directório a listar
   * @param glob      o padrão dos nomes a incluir
   * @return os ficheiros correspondentes, por ordem de nome
   */
  private static List<Path> listFiles(Path directory, String glob) throws IOException {
    try (var files = Files.newDirectoryStream(directory, glob)) {
      List<Path> found = new ArrayList<>();
      files.forEach(found::add);
      found.sort(Comparator.comparing(path -> path.getFileName().toString()));
      return found;
    }
  }

  /**
   * @param filename um nome de ficheiro
   * @return o nome sem a última extensão
   */
  private static String stripExtension(String filename) {
    int dot = filename.lastIndexOf('.');
    return dot < 0 ? filename : filename.substring(0, dot);
  }

  /** @param directory o directório a apagar, com todo o seu conteúdo */
  private static void deleteRecursively(Path directory) throws IOException {
    if (!Files.exists(directory))
      return;
    try (var paths = Files.walk(directory)) {
      for (Path path : paths.sorted(Comparator.reverseOrder()).toList())
        Files.delete(path);
    }
  }
}
