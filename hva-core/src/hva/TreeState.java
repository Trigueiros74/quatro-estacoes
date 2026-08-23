package hva;

import java.io.Serializable;

/**
 * Comportamento de uma árvore numa determinada estação do ano (padrão <i>State</i>).
 *
 * <p>Cada tipo de árvore fornece uma implementação por estação. Uma nova
 * funcionalidade dependente da estação (por exemplo, a cor das folhas)
 * acrescenta-se como um novo método desta interface, sem tocar no código que
 * manipula árvores; um novo tipo de árvore acrescenta-se como um novo conjunto
 * de estados, sem tocar nos existentes.
 */
public interface TreeState extends Serializable {

  /** @return o factor sazonal do esforço de limpeza. */
  int seasonalEffort();

  /** @return a designação do ciclo biológico da árvore nesta estação. */
  String biologicalCycle();
}
