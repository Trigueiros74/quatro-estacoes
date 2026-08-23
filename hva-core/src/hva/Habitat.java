package hva;

import java.io.Serial;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/** Recinto do hotel onde residem animais e estão implantadas árvores. */
public class Habitat extends HotelEntity {

  @Serial
  private static final long serialVersionUID = 202407081733L;

  /** Peso de cada animal no trabalho de limpeza do habitat. */
  private static final int POPULATION_WEIGHT = 3;

  private int _area;

  private final Set<Tree> _trees = new TreeSet<>(KeyOrder.INSTANCE);

  private final Set<Animal> _animals = new TreeSet<>(KeyOrder.INSTANCE);

  /*
   * Tal como as restantes colecções de entidades, estas são ordenadas por
   * chave: a desserialização de um TreeSet ou TreeMap não compara os elementos,
   * ao passo que uma colecção dispersa lhes pediria o código de dispersão antes
   * de as suas chaves estarem repostas.
   */
  private final Set<Caretaker> _caretakers = new TreeSet<>(KeyOrder.INSTANCE);

  /** Adequação do habitat a cada espécie; as omissas são neutras. */
  private final Map<Species, Influence> _influences = new TreeMap<>(KeyOrder.INSTANCE);

  /**
   * @param id   chave única do habitat
   * @param name nome do habitat
   * @param area área do habitat
   */
  public Habitat(String id, String name, int area) {
    super(id, name);
    _area = area;
  }

  /** @return a área do habitat. */
  public int getArea() {
    return _area;
  }

  /** @param area a nova área do habitat */
  void setArea(int area) {
    _area = area;
  }

  /** @return o número de animais que residem neste habitat. */
  public int getPopulation() {
    return _animals.size();
  }

  /** @return o número de árvores implantadas neste habitat. */
  public int getTreeCount() {
    return _trees.size();
  }

  /** @return as árvores deste habitat, por ordem crescente de chave. */
  public Collection<Tree> getTrees() {
    return Collections.unmodifiableSet(_trees);
  }

  /** @return os animais deste habitat, por ordem crescente de chave. */
  public Collection<Animal> getAnimals() {
    return Collections.unmodifiableSet(_animals);
  }

  /** @param tree a árvore a implantar neste habitat */
  void addTree(Tree tree) {
    _trees.add(tree);
  }

  /** @param animal o animal que passa a residir neste habitat */
  void addAnimal(Animal animal) {
    _animals.add(animal);
  }

  /** @param animal o animal que deixa de residir neste habitat */
  void removeAnimal(Animal animal) {
    _animals.remove(animal);
  }

  /**
   * @param species a espécie a contar
   * @return o número de animais dessa espécie neste habitat
   */
  public int countAnimalsOfSpecies(Species species) {
    return (int) _animals.stream().filter(animal -> animal.getSpecies().equals(species)).count();
  }

  /**
   * @param species a espécie em causa
   * @return a adequação deste habitat a animais dessa espécie
   */
  public Influence getInfluenceOn(Species species) {
    return _influences.getOrDefault(species, Influence.NEU);
  }

  /**
   * @param species   a espécie em causa
   * @param influence a nova adequação deste habitat a animais dessa espécie
   */
  void setInfluenceOn(Species species, Influence influence) {
    if (influence == Influence.NEU)
      _influences.remove(species);
    else
      _influences.put(species, influence);
  }

  /** @param caretaker o tratador que passa a poder limpar este habitat */
  void addCaretaker(Caretaker caretaker) {
    _caretakers.add(caretaker);
  }

  /** @param caretaker o tratador que deixa de poder limpar este habitat */
  void removeCaretaker(Caretaker caretaker) {
    _caretakers.remove(caretaker);
  }

  /** @return o número de tratadores que podem limpar este habitat. */
  public int getCaretakerCount() {
    return _caretakers.size();
  }

  /** @return o trabalho que este habitat dá a quem o limpa. */
  public double getWork() {
    double cleaningEffort = 0;
    for (Tree tree : _trees)
      cleaningEffort += tree.getCleaningEffort();
    return _area + POPULATION_WEIGHT * getPopulation() + cleaningEffort;
  }

  @Override
  public String toString() {
    return "HABITAT|" + getId() + "|" + getName() + "|" + _area + "|" + getTreeCount();
  }
}
