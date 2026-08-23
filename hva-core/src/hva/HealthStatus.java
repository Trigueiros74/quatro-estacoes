package hva;

/** Registo do efeito de uma vacinação no estado de saúde de um animal. */
public enum HealthStatus {

  NORMAL("NORMAL"),
  CONFUSION("CONFUSÃO"),
  ACCIDENT("ACIDENTE"),
  ERROR("ERRO");

  /** Dano a partir do qual uma má vacinação é considerada um erro. */
  private static final int ERROR_THRESHOLD = 5;

  private final String _label;

  HealthStatus(String label) {
    _label = label;
  }

  /**
   * @param suitable se a vacina era adequada à espécie do animal
   * @param damage   o dano provocado pela vacinação
   * @return o estado de saúde resultante da vacinação
   */
  public static HealthStatus of(boolean suitable, int damage) {
    if (suitable)
      return NORMAL;
    if (damage == 0)
      return CONFUSION;
    return damage < ERROR_THRESHOLD ? ACCIDENT : ERROR;
  }

  @Override
  public String toString() {
    return _label;
  }
}
