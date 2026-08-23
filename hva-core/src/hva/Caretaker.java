package hva;

import java.io.Serial;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

/** Funcionário responsável pela limpeza de um conjunto de habitats. */
public class Caretaker extends Employee {

  @Serial
  private static final long serialVersionUID = 202407081733L;

  /** Designação deste tipo de funcionário na apresentação e na importação. */
  public static final String TYPE = "TRT";

  /** Satisfação de um tratador sem habitats à sua responsabilidade. */
  private static final double BASELINE_SATISFACTION = 300;

  private final Set<Habitat> _habitats = new TreeSet<>(KeyOrder.INSTANCE);

  /**
   * @param id   chave única do tratador
   * @param name nome do tratador
   */
  public Caretaker(String id, String name) {
    super(id, name, new DefaultSatisfaction(BASELINE_SATISFACTION));
  }

  @Override
  public String getType() {
    return TYPE;
  }

  @Override
  public double getWork() {
    double work = 0;
    for (Habitat habitat : _habitats)
      work += habitat.getWork() / habitat.getCaretakerCount();
    return work;
  }

  @Override
  public Collection<String> getResponsibilityKeys() {
    return _habitats.stream().map(Habitat::getId).toList();
  }

  @Override
  public boolean addResponsibility(HotelEntity responsibility) {
    if (!(responsibility instanceof Habitat habitat))
      return false;
    if (_habitats.add(habitat))
      habitat.addCaretaker(this);
    return true;
  }

  @Override
  public boolean removeResponsibility(HotelEntity responsibility) {
    if (!(responsibility instanceof Habitat habitat) || !_habitats.remove(habitat))
      return false;
    habitat.removeCaretaker(this);
    return true;
  }
}
