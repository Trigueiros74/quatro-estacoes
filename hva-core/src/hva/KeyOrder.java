package hva;

import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;

/**
 * Ordem lexicográfica das chaves das entidades do hotel, sem distinção entre
 * maiúsculas e minúsculas.
 *
 * <p>É uma classe e não uma expressão lambda porque as colecções ordenadas que
 * a usam fazem parte do estado serializado da aplicação.
 */
public final class KeyOrder implements Comparator<HotelEntity>, Serializable {

  @Serial
  private static final long serialVersionUID = 202407081733L;

  /** Instância partilhada: o comparador não tem estado. */
  public static final KeyOrder INSTANCE = new KeyOrder();

  private KeyOrder() {
  }

  @Override
  public int compare(HotelEntity first, HotelEntity second) {
    return String.CASE_INSENSITIVE_ORDER.compare(first.getId(), second.getId());
  }

  /** @return a instância partilhada, preservando o singleton após desserialização. */
  @Serial
  private Object readResolve() {
    return INSTANCE;
  }
}
