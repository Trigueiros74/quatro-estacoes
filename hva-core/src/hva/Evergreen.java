package hva;

import java.io.Serial;
import java.util.EnumMap;
import java.util.Map;

/** Árvore de folha perene. */
public class Evergreen extends Tree {

  @Serial
  private static final long serialVersionUID = 202407081733L;

  /** Designação deste tipo de árvore na apresentação e no ficheiro de importação. */
  public static final String TYPE = "PERENE";

  private static final Map<Season, TreeState> STATES = new EnumMap<>(Map.of(
      Season.SPRING, EvergreenState.SPRING,
      Season.SUMMER, EvergreenState.SUMMER,
      Season.FALL, EvergreenState.FALL,
      Season.WINTER, EvergreenState.WINTER));

  /** @see Tree#Tree(String, String, int, int, Season) */
  public Evergreen(String id, String name, int age, int baseCleaningDifficulty, Season season) {
    super(id, name, age, baseCleaningDifficulty, season);
  }

  @Override
  protected TreeState state(Season season) {
    return STATES.get(season);
  }

  @Override
  public String getTreeType() {
    return TYPE;
  }
}
