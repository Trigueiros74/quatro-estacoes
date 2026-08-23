package hva;

/** Os estados sazonais de uma árvore de folha caduca. */
public enum DeciduousState implements TreeState {

  SPRING(1, "GERARFOLHAS"),
  SUMMER(2, "COMFOLHAS"),
  FALL(5, "LARGARFOLHAS"),
  WINTER(0, "SEMFOLHAS");

  private final int _seasonalEffort;
  private final String _biologicalCycle;

  DeciduousState(int seasonalEffort, String biologicalCycle) {
    _seasonalEffort = seasonalEffort;
    _biologicalCycle = biologicalCycle;
  }

  @Override
  public int seasonalEffort() {
    return _seasonalEffort;
  }

  @Override
  public String biologicalCycle() {
    return _biologicalCycle;
  }
}
