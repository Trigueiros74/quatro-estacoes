package hva;

import java.io.Serial;
import java.io.Serializable;

/** Entidade do hotel identificada por uma chave única. */
public abstract class HotelEntity implements Serializable {

  @Serial
  private static final long serialVersionUID = 202407081733L;

  private final String _id;
  private final String _name;

  /**
   * @param id   chave única da entidade
   * @param name nome da entidade
   */
  protected HotelEntity(String id, String name) {
    _id = id;
    _name = name;
  }

  /** @return a chave única da entidade. */
  public String getId() {
    return _id;
  }

  /** @return o nome da entidade. */
  public String getName() {
    return _name;
  }

  /** Duas entidades do mesmo tipo são iguais se tiverem a mesma chave. */
  @Override
  public boolean equals(Object other) {
    return other != null && getClass() == other.getClass()
        && _id.equalsIgnoreCase(((HotelEntity) other)._id);
  }

  @Override
  public int hashCode() {
    return _id.toLowerCase().hashCode();
  }
}
