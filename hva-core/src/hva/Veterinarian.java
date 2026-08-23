package hva;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/** Funcionário responsável por vacinar animais de um conjunto de espécies. */
public class Veterinarian extends Employee {

  @Serial
  private static final long serialVersionUID = 202407081733L;

  /** Designação deste tipo de funcionário na apresentação e na importação. */
  public static final String TYPE = "VET";

  /** Satisfação de um veterinário sem espécies à sua responsabilidade. */
  private static final double BASELINE_SATISFACTION = 20;

  private final Set<Species> _species = new TreeSet<>(KeyOrder.INSTANCE);

  private final List<VaccinationRecord> _records = new ArrayList<>();

  /**
   * @param id   chave única do veterinário
   * @param name nome do veterinário
   */
  public Veterinarian(String id, String name) {
    super(id, name, new DefaultSatisfaction(BASELINE_SATISFACTION));
  }

  @Override
  public String getType() {
    return TYPE;
  }

  @Override
  public double getWork() {
    double work = 0;
    for (Species species : _species)
      work += (double) species.getPopulation() / species.getVeterinarianCount();
    return work;
  }

  @Override
  public Collection<String> getResponsibilityKeys() {
    return _species.stream().map(Species::getId).toList();
  }

  @Override
  public boolean addResponsibility(HotelEntity responsibility) {
    if (!(responsibility instanceof Species species))
      return false;
    if (_species.add(species))
      species.addVeterinarian(this);
    return true;
  }

  @Override
  public boolean removeResponsibility(HotelEntity responsibility) {
    if (!(responsibility instanceof Species species) || !_species.remove(species))
      return false;
    species.removeVeterinarian(this);
    return true;
  }

  /**
   * @param species a espécie a vacinar
   * @return se este veterinário está autorizado a vacinar animais da espécie
   */
  public boolean isAuthorizedFor(Species species) {
    return _species.contains(species);
  }

  /** @param record a vacinação a acrescentar ao historial deste veterinário */
  void addRecord(VaccinationRecord record) {
    _records.add(record);
  }

  /** @return as vacinações realizadas por este veterinário, por ordem de aplicação. */
  public Collection<VaccinationRecord> getRecords() {
    return Collections.unmodifiableList(_records);
  }
}
