package hva.web;

import hva.Animal;
import hva.Employee;
import hva.Habitat;
import hva.Hotel;
import hva.Tree;

/**
 * Descreve o estado de um hotel numa forma determinística, para que a mesma
 * descrição possa ser produzida na JVM e em JavaScript e as duas comparadas.
 *
 * <p>Os valores fraccionários são impressos em centésimos inteiros. Não é
 * economia de espaço: a formatação de vírgula flutuante depende da língua e da
 * implementação, e compará-la mediria o formatador em vez de medir o domínio.
 */
final class Report {

  private Report() {
  }

  /**
   * @param hotel o hotel a descrever, na sua estação corrente
   * @return a descrição do hotel, uma entidade por linha
   */
  static String of(Hotel hotel) {
    StringBuilder report = new StringBuilder();

    line(report, "== " + hotel.getCurrentSeason() + " ==");
    line(report, "satisfação global: " + hotel.getGlobalSatisfaction());

    for (Habitat habitat : hotel.getHabitats())
      line(report, "habitat " + habitat.getId()
          + " área=" + habitat.getArea()
          + " população=" + habitat.getPopulation()
          + " árvores=" + habitat.getTreeCount()
          + " trabalho=" + hundredths(habitat.getWork()));

    for (Tree tree : hotel.getTrees())
      line(report, "árvore " + tree.getId()
          + " " + tree.getTreeType()
          + " idade=" + tree.getAge()
          + " ciclo=" + tree.getBiologicalCycle()
          + " esforço=" + hundredths(tree.getCleaningEffort()));

    for (Animal animal : hotel.getAnimals())
      line(report, "animal " + animal.getId()
          + " espécie=" + animal.getSpecies().getId()
          + " habitat=" + animal.getHabitat().getId()
          + " saúde=" + animal.getHealthHistory()
          + " satisfação=" + hundredths(animal.getSatisfaction()));

    for (Employee employee : hotel.getEmployees())
      line(report, "funcionário " + employee.getId()
          + " " + employee.getType()
          + " responsabilidades=" + String.join(",", employee.getResponsibilityKeys())
          + " trabalho=" + hundredths(employee.getWork())
          + " satisfação=" + hundredths(employee.getSatisfaction()));

    return report.toString();
  }

  /**
   * @param value um valor fraccionário
   * @return o valor em centésimos inteiros, sem formatador pelo meio
   */
  private static long hundredths(double value) {
    return Math.round(value * 100);
  }

  private static void line(StringBuilder report, String text) {
    report.append(text).append('\n');
  }
}
