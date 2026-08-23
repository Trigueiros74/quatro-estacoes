package hva;

import java.io.Serial;
import java.util.Set;
import java.util.TreeSet;

/** Espécie animal tratada no hotel. */
public class Species extends HotelEntity {

  @Serial
  private static final long serialVersionUID = 202407081733L;

  /*
   * Estas colecções fazem parte de ciclos de referências (um animal conhece a
   * sua espécie e a espécie conhece os seus animais). São ordenadas, e não
   * dispersas, porque a desserialização de um TreeSet não compara os elementos
   * — ao contrário de um HashSet, que lhes pediria o código de dispersão antes
   * de as suas chaves estarem repostas.
   */
  private final Set<Animal> _animals = new TreeSet<>(KeyOrder.INSTANCE);
  private final Set<Veterinarian> _veterinarians = new TreeSet<>(KeyOrder.INSTANCE);

  /**
   * @param id   chave única da espécie
   * @param name nome da espécie
   */
  public Species(String id, String name) {
    super(id, name);
  }

  /** @param animal o animal a registar nesta espécie */
  void addAnimal(Animal animal) {
    _animals.add(animal);
  }

  /** @return o número de animais desta espécie no hotel. */
  public int getPopulation() {
    return _animals.size();
  }

  /** @param veterinarian o veterinário que passa a poder tratar desta espécie */
  void addVeterinarian(Veterinarian veterinarian) {
    _veterinarians.add(veterinarian);
  }

  /** @param veterinarian o veterinário que deixa de poder tratar desta espécie */
  void removeVeterinarian(Veterinarian veterinarian) {
    _veterinarians.remove(veterinarian);
  }

  /** @return o número de veterinários que podem tratar desta espécie. */
  public int getVeterinarianCount() {
    return _veterinarians.size();
  }
}
