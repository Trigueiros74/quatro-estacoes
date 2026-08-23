package hva;

import java.io.Serial;
import java.util.Collection;

/**
 * Funcionário do hotel.
 *
 * <p>Um novo tipo de funcionário estende esta classe, define o seu trabalho e o
 * conjunto de responsabilidades que aceita; nada mais no domínio precisa de
 * saber que ele existe.
 */
public abstract class Employee extends HotelEntity {

  @Serial
  private static final long serialVersionUID = 202407081733L;

  private SatisfactionStrategy _satisfactionStrategy;

  /**
   * @param id       chave única do funcionário
   * @param name     nome do funcionário
   * @param strategy política inicial de cálculo da satisfação
   */
  protected Employee(String id, String name, SatisfactionStrategy strategy) {
    super(id, name);
    _satisfactionStrategy = strategy;
  }

  /** @return a designação do tipo deste funcionário ({@code VET} ou {@code TRT}). */
  public abstract String getType();

  /** @return o trabalho actualmente atribuído a este funcionário. */
  public abstract double getWork();

  /** @return as chaves das responsabilidades atribuídas, por ordem crescente. */
  public abstract Collection<String> getResponsibilityKeys();

  /**
   * Atribui uma responsabilidade ao funcionário; não faz nada se ela já lhe
   * estiver atribuída.
   *
   * @param responsibility a entidade pela qual o funcionário passa a responder
   * @return {@code true} se a entidade é uma responsabilidade aceitável para
   *         este tipo de funcionário
   */
  public abstract boolean addResponsibility(HotelEntity responsibility);

  /**
   * Retira uma responsabilidade ao funcionário.
   *
   * @param responsibility a entidade pela qual o funcionário deixa de responder
   * @return {@code true} se a responsabilidade estava atribuída e foi retirada
   */
  public abstract boolean removeResponsibility(HotelEntity responsibility);

  /** @param strategy a nova política de cálculo da satisfação */
  public void setSatisfactionStrategy(SatisfactionStrategy strategy) {
    _satisfactionStrategy = strategy;
  }

  /** @return o grau de satisfação do funcionário, segundo a política corrente. */
  public double getSatisfaction() {
    return _satisfactionStrategy.satisfaction(this);
  }

  @Override
  public String toString() {
    StringBuilder description = new StringBuilder(getType())
        .append("|").append(getId())
        .append("|").append(getName());
    Collection<String> responsibilities = getResponsibilityKeys();
    if (!responsibilities.isEmpty())
      description.append("|").append(String.join(",", responsibilities));
    return description.toString();
  }
}
