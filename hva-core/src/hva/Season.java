package hva;

/**
 * As estações do ano, por ordem cíclica.
 *
 * <p>A estação é um dado do hotel e não do processo: não existe aqui qualquer
 * estado global, pelo que podem coexistir vários hotéis em estações distintas.
 */
public enum Season {

  SPRING,
  SUMMER,
  FALL,
  WINTER;

  /** @return a estação seguinte no ciclo anual. */
  public Season next() {
    return values()[(ordinal() + 1) % values().length];
  }
}
