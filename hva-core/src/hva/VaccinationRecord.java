package hva;

import java.io.Serial;
import java.io.Serializable;

/** Registo de uma vacinação: que vacina, aplicada por quem, a que animal. */
public class VaccinationRecord implements Serializable {

  @Serial
  private static final long serialVersionUID = 202407081733L;

  private final Vaccine _vaccine;
  private final Veterinarian _veterinarian;
  private final Animal _animal;
  private final HealthStatus _outcome;

  /**
   * @param vaccine      a vacina aplicada
   * @param veterinarian o veterinário que a aplicou
   * @param animal       o animal vacinado
   */
  VaccinationRecord(Vaccine vaccine, Veterinarian veterinarian, Animal animal) {
    _vaccine = vaccine;
    _veterinarian = veterinarian;
    _animal = animal;

    Species species = animal.getSpecies();
    _outcome = HealthStatus.of(vaccine.isSuitableFor(species), vaccine.damage(species));
  }

  /** @return o efeito desta vacinação no estado de saúde do animal. */
  public HealthStatus getOutcome() {
    return _outcome;
  }

  /** @return se esta vacinação provocou problemas de saúde ao animal. */
  public boolean isWrong() {
    return _outcome != HealthStatus.NORMAL;
  }

  @Override
  public String toString() {
    return "REGISTO-VACINA|" + _vaccine.getId() + "|" + _veterinarian.getId()
        + "|" + _animal.getSpecies().getId();
  }
}
