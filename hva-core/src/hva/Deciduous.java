package hva;

import java.io.Serial;
import java.util.EnumMap;
import java.util.Map;

/** Árvore de folha caduca. */
public class Deciduous extends Tree {

  @Serial
  private static final long serialVersionUID = 202407081733L;

  /** Designação deste tipo de árvore na apresentação e no ficheiro de importação. */
  public static final String TYPE = "CADUCA";

  private static final Map<Season, TreeState> STATES = new EnumMap<>(Map.of(
      Season.SPRING, DeciduousState.SPRING,
      Season.SUMMER, DeciduousState.SUMMER,
      Season.FALL, DeciduousState.FALL,
      Season.WINTER, DeciduousState.WINTER));

  /** @see Tree#Tree(String, String, int, int, Season) */
  public Deciduous(String id, String name, int age, int baseCleaningDifficulty, Season season) {
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
