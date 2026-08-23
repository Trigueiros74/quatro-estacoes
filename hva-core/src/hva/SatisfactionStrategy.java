package hva;

import java.io.Serializable;

/**
 * Política de cálculo da satisfação de um funcionário (padrão <i>Strategy</i>).
 *
 * <p>Cada funcionário tem a sua política, que pode ser substituída em tempo de
 * execução através de {@link Employee#setSatisfactionStrategy(SatisfactionStrategy)}.
 * Uma nova política é uma nova implementação desta interface e não obriga a
 * alterar as classes de funcionários.
 */
public interface SatisfactionStrategy extends Serializable {

  /**
   * @param employee o funcionário a avaliar
   * @return o grau de satisfação do funcionário
   */
  double satisfaction(Employee employee);
}
