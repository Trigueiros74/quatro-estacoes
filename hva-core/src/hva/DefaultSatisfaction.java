package hva;

import java.io.Serial;

/**
 * Política de satisfação por omissão: um valor de referência descontado do
 * trabalho atribuído ao funcionário.
 */
public class DefaultSatisfaction implements SatisfactionStrategy {

  @Serial
  private static final long serialVersionUID = 202407081733L;

  private final double _baseline;

  /** @param baseline a satisfação de um funcionário sem trabalho atribuído */
  public DefaultSatisfaction(double baseline) {
    _baseline = baseline;
  }

  @Override
  public double satisfaction(Employee employee) {
    return _baseline - employee.getWork();
  }
}
