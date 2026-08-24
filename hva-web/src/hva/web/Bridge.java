package hva.web;

import org.teavm.jso.JSClass;
import org.teavm.jso.JSExport;

import hva.Animal;
import hva.Habitat;
import hva.Hotel;

/**
 * A fronteira entre o domínio e a interface: o que o JavaScript pode chamar.
 *
 * <p>Existe para responder à pergunta da fase 1 — não «o Java corre no
 * browser?», mas «o JavaScript consegue interrogar e alterar o hotel?». O ciclo
 * que interessa é ler a satisfação, mexer num animal e voltar a ler: é isso, em
 * miniatura, que a fase 2 fará ao arrastar.
 *
 * <p>Deliberadamente pobre. Devolve texto e números porque um <i>spike</i> não é
 * sítio para desenhar uma API; a forma definitiva decide-se quando houver uma
 * interface a consumi-la.
 */
@JSClass(name = "Hotel")
public class Bridge {

  private final Hotel _hotel;

  /**
   * Cria a ponte sobre o cenário do <i>spike</i>.
   *
   * <p>É uma fábrica estática, e não um construtor exportado, por uma razão
   * apurada à força: <b>o TeaVM só arranca o runtime quando se chama um método
   * estático exportado</b>. Um construtor exportado invocado em primeiro lugar
   * corre antes de as classes da biblioteca estarem inicializadas — o
   * {@code String.CASE_INSENSITIVE_ORDER} ainda é {@code null} e todos os
   * {@code TreeMap} do núcleo rebentam com um {@code TypeError}. Entrando por um
   * método estático, o problema não existe.
   *
   * @return a ponte sobre um hotel acabado de construir
   * @throws Exception se o cenário for incoerente
   */
  @JSExport
  public static Bridge create() throws Exception {
    return new Bridge();
  }

  private Bridge() throws Exception {
    _hotel = Scenario.build();
  }

  /** @return a satisfação global do hotel — o valor que a fase 1 tinha de alcançar. */
  @JSExport
  public int getGlobalSatisfaction() {
    return (int) _hotel.getGlobalSatisfaction();
  }

  /** @return a estação do ano corrente. */
  @JSExport
  public String getSeason() {
    return _hotel.getCurrentSeason().toString();
  }

  /** Avança para a estação seguinte, fazendo transitar todas as árvores. */
  @JSExport
  public void nextSeason() {
    _hotel.nextSeason();
  }

  /** @return as chaves dos animais, separadas por vírgulas. */
  @JSExport
  public String getAnimalIds() {
    StringBuilder ids = new StringBuilder();
    for (Animal animal : _hotel.getAnimals())
      append(ids, animal.getId());
    return ids.toString();
  }

  /** @return as chaves dos habitats, separadas por vírgulas. */
  @JSExport
  public String getHabitatIds() {
    StringBuilder ids = new StringBuilder();
    for (Habitat habitat : _hotel.getHabitats())
      append(ids, habitat.getId());
    return ids.toString();
  }

  /**
   * @param animalId a chave do animal
   * @return a chave do habitat onde o animal reside, ou vazio se não existir
   */
  @JSExport
  public String getHabitatOf(String animalId) {
    Animal animal = _hotel.getAnimal(animalId);
    return animal == null ? "" : animal.getHabitat().getId();
  }

  /**
   * @param animalId a chave do animal
   * @return o nome do animal, ou vazio se não existir
   */
  @JSExport
  public String getAnimalName(String animalId) {
    Animal animal = _hotel.getAnimal(animalId);
    return animal == null ? "" : animal.getName();
  }

  /**
   * @param animalId a chave do animal
   * @return a chave da espécie do animal, ou vazio se não existir
   */
  @JSExport
  public String getSpeciesOf(String animalId) {
    Animal animal = _hotel.getAnimal(animalId);
    return animal == null ? "" : animal.getSpecies().getId();
  }

  /**
   * @param animalId a chave do animal
   * @return a satisfação do animal, em centésimos inteiros
   */
  @JSExport
  public int getAnimalSatisfaction(String animalId) {
    Animal animal = _hotel.getAnimal(animalId);
    return animal == null ? 0 : (int) Math.round(animal.getSatisfaction() * 100);
  }

  /**
   * @param habitatId a chave do habitat
   * @return o nome do habitat, ou vazio se não existir
   */
  @JSExport
  public String getHabitatName(String habitatId) {
    Habitat habitat = _hotel.getHabitat(habitatId);
    return habitat == null ? "" : habitat.getName();
  }

  /**
   * A área alimenta a fórmula da satisfação (área a dividir pela população) e é
   * também o que dá a um habitat o seu tamanho no ecrã.
   *
   * @param habitatId a chave do habitat
   * @return a área do habitat, ou zero se não existir
   */
  @JSExport
  public int getHabitatArea(String habitatId) {
    Habitat habitat = _hotel.getHabitat(habitatId);
    return habitat == null ? 0 : habitat.getArea();
  }

  /**
   * A adequação do habitat à espécie do animal, que vale ±20 pontos de
   * satisfação e explica metade das jogadas boas e más.
   *
   * @param habitatId a chave do habitat
   * @param animalId  a chave do animal cuja espécie interessa
   * @return {@code POS}, {@code NEU}, {@code NEG}, ou vazio se algum não existir
   */
  @JSExport
  public String getInfluence(String habitatId, String animalId) {
    Habitat habitat = _hotel.getHabitat(habitatId);
    Animal animal = _hotel.getAnimal(animalId);
    if (habitat == null || animal == null)
      return "";
    return habitat.getInfluenceOn(animal.getSpecies()).toString();
  }

  /**
   * Pré-visualiza uma transferência sem a fazer — o que permite acender o
   * habitat antes de lá se largar o animal.
   *
   * @param animalId  a chave do animal
   * @param habitatId a chave do habitat de destino
   * @return a satisfação global que o hotel teria depois da mudança, ou a actual
   *         se as chaves não existirem
   */
  @JSExport
  public int satisfactionIfMovedTo(String animalId, String habitatId) {
    try {
      return (int) _hotel.satisfactionIfMovedTo(animalId, habitatId);
    } catch (Exception unknown) {
      return getGlobalSatisfaction();
    }
  }

  /**
   * Transfere um animal para outro habitat — a mutação que a fase 2 fará ao
   * largar o animal.
   *
   * @param animalId  a chave do animal
   * @param habitatId a chave do habitat de destino
   * @return se a transferência aconteceu
   */
  @JSExport
  public boolean transferAnimal(String animalId, String habitatId) {
    try {
      _hotel.transferAnimal(animalId, habitatId);
      return true;
    } catch (Exception refused) {
      return false;
    }
  }

  /** @return a descrição completa do hotel na estação corrente. */
  @JSExport
  public String getReport() {
    return Report.of(_hotel);
  }

  /** @return o relatório de um ano completo, a partir de um hotel acabado de construir. */
  @JSExport
  public static String getYearReport() {
    return Spike.report();
  }

  private static void append(StringBuilder ids, String id) {
    if (ids.length() > 0)
      ids.append(',');
    ids.append(id);
  }
}
