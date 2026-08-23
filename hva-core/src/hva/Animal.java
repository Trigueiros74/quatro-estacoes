package hva;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/** Animal alojado num habitat do hotel. */
public class Animal extends HotelEntity {

  @Serial
  private static final long serialVersionUID = 202407081733L;

  /** Apresentado como historial de saúde de um animal que nunca foi vacinado. */
  private static final String NO_HISTORY = "VOID";

  /** Satisfação de um animal sozinho num habitat de área nula e influência neutra. */
  private static final int BASELINE_SATISFACTION = 20;

  /** Peso de cada animal da mesma espécie no habitat. */
  private static final int SAME_SPECIES_WEIGHT = 3;

  /** Peso de cada animal de espécie diferente no habitat. */
  private static final int OTHER_SPECIES_WEIGHT = 2;

  private final Species _species;
  private Habitat _habitat;
  private final List<VaccinationRecord> _records = new ArrayList<>();

  /**
   * @param id      chave única do animal
   * @param name    nome do animal
   * @param species espécie do animal
   * @param habitat habitat onde o animal reside
   */
  public Animal(String id, String name, Species species, Habitat habitat) {
    super(id, name);
    _species = species;
    _habitat = habitat;
  }

  /** @return a espécie deste animal. */
  public Species getSpecies() {
    return _species;
  }

  /** @return o habitat onde este animal reside. */
  public Habitat getHabitat() {
    return _habitat;
  }

  /** @param habitat o habitat para onde o animal é transferido */
  void setHabitat(Habitat habitat) {
    _habitat = habitat;
  }

  /** @param record a vacinação a acrescentar ao historial deste animal */
  void addRecord(VaccinationRecord record) {
    _records.add(record);
  }

  /** @return as vacinações deste animal, por ordem de aplicação. */
  public Collection<VaccinationRecord> getRecords() {
    return Collections.unmodifiableList(_records);
  }

  /** @return o historial de saúde do animal, por ordem de ocorrência. */
  public String getHealthHistory() {
    if (_records.isEmpty())
      return NO_HISTORY;
    return String.join(",", _records.stream().map(record -> record.getOutcome().toString()).toList());
  }

  /** @return o grau de satisfação deste animal no seu habitat. */
  public double getSatisfaction() {
    int population = _habitat.getPopulation();
    int sameSpecies = _habitat.countAnimalsOfSpecies(_species) - 1;
    int otherSpecies = population - sameSpecies - 1;

    return BASELINE_SATISFACTION
        + SAME_SPECIES_WEIGHT * sameSpecies
        - OTHER_SPECIES_WEIGHT * otherSpecies
        + (double) _habitat.getArea() / population
        + _habitat.getInfluenceOn(_species).getSatisfactionBonus();
  }

  @Override
  public String toString() {
    return "ANIMAL|" + getId() + "|" + getName() + "|" + _species.getId()
        + "|" + getHealthHistory() + "|" + _habitat.getId();
  }
}
