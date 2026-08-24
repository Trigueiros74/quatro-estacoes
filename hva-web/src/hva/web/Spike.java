package hva.web;

import org.teavm.jso.JSExportClasses;

import hva.Hotel;

/**
 * O <i>spike</i> da fase 1: prova que o {@code hva-core} corre dentro do
 * browser.
 *
 * <p>Percorre um ano inteiro do cenário e escreve o estado do hotel em cada
 * estação. O relatório é o teste: corre-se este mesmo código na JVM e em
 * JavaScript e comparam-se as duas saídas. Se forem idênticas, as colecções
 * ordenadas, os {@code enum}, os {@code stream}, o {@code Math.log} e a
 * aritmética de vírgula flutuante atravessaram intactos.
 *
 * <p>A anotação leva a {@link Bridge} para o módulo gerado — é ela que o
 * JavaScript instancia e interroga.
 */
@JSExportClasses(Bridge.class)
public final class Spike {

  /** As quatro estações, voltando à de partida para se ver as árvores envelhecer. */
  private static final int SEASONS = 5;

  private Spike() {
  }

  /** @param args ignorados */
  public static void main(String[] args) {
    System.out.println(report());
  }

  /** @return o relatório de um ano completo do cenário. */
  static String report() {
    StringBuilder report = new StringBuilder();
    try {
      Hotel hotel = Scenario.build();
      for (int season = 0; season < SEASONS; season++) {
        report.append(Report.of(hotel)).append('\n');
        if (season < SEASONS - 1)
          hotel.nextSeason();
      }
    } catch (Exception problem) {
      report.append("FALHOU: ").append(problem).append('\n');
    }
    return report.toString();
  }
}
