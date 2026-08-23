package hva;

import java.io.Serial;

/**
 * Uma árvore plantada num habitat.
 *
 * <p>Tudo o que depende da estação do ano é delegado no {@link TreeState}
 * correspondente, pelo que esta classe não conhece as estações nem os ciclos
 * biológicos concretos.
 */
public abstract class Tree extends HotelEntity {

  @Serial
  private static final long serialVersionUID = 202407081733L;

  private int _age;
  private final int _baseCleaningDifficulty;

  /** Estação em que a árvore foi plantada; marca o fecho de cada ano de vida. */
  private final Season _plantingSeason;

  /** Estação corrente da árvore. */
  private Season _season;

  /**
   * @param id                     chave única da árvore
   * @param name                   nome da árvore
   * @param age                    idade em anos no momento da plantação
   * @param baseCleaningDifficulty dificuldade base de limpeza
   * @param season                 estação em que a árvore é plantada
   */
  protected Tree(String id, String name, int age, int baseCleaningDifficulty, Season season) {
    super(id, name);
    _age = age;
    _baseCleaningDifficulty = baseCleaningDifficulty;
    _plantingSeason = season;
    _season = season;
  }

  /** @return a idade da árvore, em anos. */
  public int getAge() {
    return _age;
  }

  /** @return a dificuldade base de limpeza da árvore. */
  public int getBaseCleaningDifficulty() {
    return _baseCleaningDifficulty;
  }

  /** @return a estação corrente da árvore. */
  public Season getSeason() {
    return _season;
  }

  /**
   * Faz a árvore transitar para a estação seguinte, envelhecendo-a quando
   * completa mais um ano de vida.
   */
  void advanceSeason() {
    _season = _season.next();
    if (_season == _plantingSeason)
      _age++;
  }

  /**
   * @param season a estação pretendida
   * @return o estado desta árvore na estação indicada
   */
  protected abstract TreeState state(Season season);

  /** @return a designação do tipo desta árvore ({@code PERENE} ou {@code CADUCA}). */
  public abstract String getTreeType();

  /** @return o ciclo biológico da árvore na estação corrente. */
  public String getBiologicalCycle() {
    return state(_season).biologicalCycle();
  }

  /** @return o esforço de limpeza que a árvore induz na estação corrente. */
  public double getCleaningEffort() {
    return _baseCleaningDifficulty * state(_season).seasonalEffort() * Math.log(_age + 1);
  }

  @Override
  public String toString() {
    return "ÁRVORE|" + getId() + "|" + getName() + "|" + _age + "|"
        + _baseCleaningDifficulty + "|" + getTreeType() + "|" + getBiologicalCycle();
  }
}
