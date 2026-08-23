package hva;

/** Os estados sazonais de uma árvore de folha perene. */
public enum EvergreenState implements TreeState {

  SPRING(1, "GERARFOLHAS"),
  SUMMER(1, "COMFOLHAS"),
  FALL(1, "COMFOLHAS"),
  WINTER(2, "LARGARFOLHAS");

  private final int _seasonalEffort;
  private final String _biologicalCycle;

  EvergreenState(int seasonalEffort, String biologicalCycle) {
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
