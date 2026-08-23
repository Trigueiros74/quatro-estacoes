package hva;

import java.io.Serial;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

/** Vacina aplicável a um conjunto de espécies. */
public class Vaccine extends HotelEntity {

  @Serial
  private static final long serialVersionUID = 202407081733L;

  private final Set<Species> _species = new TreeSet<>(KeyOrder.INSTANCE);

  private int _applications;

  /**
   * @param id      chave única da vacina
   * @param name    nome da vacina
   * @param species espécies a que a vacina se destina
   */
  public Vaccine(String id, String name, Collection<Species> species) {
    super(id, name);
    _species.addAll(species);
  }

  /** @return as espécies a que esta vacina se destina, por ordem crescente de chave. */
  public Collection<Species> getSpecies() {
    return Collections.unmodifiableSet(_species);
  }

  /**
   * @param species a espécie do animal a vacinar
   * @return se a vacina é adequada a animais dessa espécie
   */
  public boolean isSuitableFor(Species species) {
    return _species.contains(species);
  }

  /**
   * Calcula o dano provocado pela aplicação desta vacina a um animal da espécie
   * indicada: zero se a vacina lhe for adequada e, caso contrário, o máximo da
   * diferença entre o nome dessa espécie e os nomes das espécies a que a vacina
   * se destinava.
   *
   * @param species a espécie do animal vacinado
   * @return o dano provocado pela vacinação
   */
  public int damage(Species species) {
    if (isSuitableFor(species))
      return 0;

    int damage = 0;
    for (Species target : _species)
      damage = Math.max(damage, nameDistance(species.getName(), target.getName()));
    return damage;
  }

  /**
   * As diferenças entre maiúsculas e minúsculas são irrelevantes nos nomes, pelo
   * que a contagem de caracteres comuns as ignora.
   *
   * @param first  nome de uma espécie
   * @param second nome de outra espécie
   * @return o comprimento do nome mais longo menos o número de caracteres comuns
   */
  private static int nameDistance(String first, String second) {
    StringBuilder remaining = new StringBuilder(second.toLowerCase());
    int common = 0;

    for (char character : first.toLowerCase().toCharArray()) {
      int position = remaining.indexOf(String.valueOf(character));
      if (position >= 0) {
        remaining.deleteCharAt(position);
        common++;
      }
    }

    return Math.max(first.length(), second.length()) - common;
  }

  /** Regista mais uma aplicação desta vacina. */
  void addApplication() {
    _applications++;
  }

  @Override
  public String toString() {
    StringBuilder description = new StringBuilder("VACINA|")
        .append(getId()).append("|")
        .append(getName()).append("|")
        .append(_applications);
    if (!_species.isEmpty())
      description.append("|").append(
          String.join(",", _species.stream().map(Species::getId).toList()));
    return description.toString();
  }
}
