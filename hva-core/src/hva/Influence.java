package hva;

/** Adequação de um habitat a uma espécie e o seu efeito na satisfação. */
public enum Influence {

  POS(20),
  NEU(0),
  NEG(-20);

  private final int _satisfactionBonus;

  Influence(int satisfactionBonus) {
    _satisfactionBonus = satisfactionBonus;
  }

  /** @return a parcela que esta adequação acrescenta à satisfação do animal. */
  public int getSatisfactionBonus() {
    return _satisfactionBonus;
  }
}
