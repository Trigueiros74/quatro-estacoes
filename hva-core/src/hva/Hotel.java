package hva;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import hva.exceptions.DuplicateAnimalKeyExceptionCore;
import hva.exceptions.DuplicateEmployeeKeyExceptionCore;
import hva.exceptions.DuplicateHabitatKeyExceptionCore;
import hva.exceptions.DuplicateTreeKeyExceptionCore;
import hva.exceptions.DuplicateVaccineKeyExceptionCore;
import hva.exceptions.NoResponsibilityExceptionCore;
import hva.exceptions.UnknownAnimalKeyExceptionCore;
import hva.exceptions.UnknownEmployeeKeyExceptionCore;
import hva.exceptions.UnknownHabitatKeyExceptionCore;
import hva.exceptions.UnknownSpeciesKeyExceptionCore;
import hva.exceptions.UnknownTreeKeyExceptionCore;
import hva.exceptions.UnknownVaccineKeyExceptionCore;
import hva.exceptions.UnknownVeterinarianKeyExceptionCore;
import hva.exceptions.UnrecognizedEntryException;
import hva.exceptions.VeterinarianNotAuthorizedExceptionCore;
import hva.exceptions.WrongVaccineMessage;

/**
 * Um hotel veterinário: as suas espécies, animais, habitats, árvores,
 * funcionários, vacinas e o historial de vacinações.
 *
 * <p>Todo o estado é próprio da instância, pelo que podem coexistir vários
 * hotéis independentes. As chaves das entidades não distinguem maiúsculas de
 * minúsculas e as colecções são mantidas por ordem crescente de chave.
 */
public class Hotel implements Serializable {

  @Serial
  private static final long serialVersionUID = 202407081733L;

  private Season _currentSeason = Season.SPRING;

  private final Map<String, Species> _species = newIndex();
  private final Map<String, Animal> _animals = newIndex();
  private final Map<String, Habitat> _habitats = newIndex();
  private final Map<String, Tree> _trees = newIndex();
  private final Map<String, Employee> _employees = newIndex();
  private final Map<String, Vaccine> _vaccines = newIndex();

  private final List<VaccinationRecord> _records = new ArrayList<>();

  /** Indica se existem alterações por guardar. */
  private boolean _changed;

  /**
   * @param <T> o tipo das entidades indexadas
   * @return um índice de entidades por chave, insensível a maiúsculas e ordenado
   */
  private static <T> Map<String, T> newIndex() {
    return new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
  }

  // ------------------------------------------------------------------ estado

  /** Assinala que o hotel foi modificado desde a última salvaguarda. */
  public void changed() {
    setChanged(true);
  }

  /** @return se existem alterações por guardar. */
  public boolean hasChanged() {
    return _changed;
  }

  /** @param changed o novo estado de modificação */
  public void setChanged(boolean changed) {
    _changed = changed;
  }

  /** @return a estação do ano corrente. */
  public Season getCurrentSeason() {
    return _currentSeason;
  }

  /**
   * Avança para a estação seguinte, fazendo transitar todas as árvores do hotel.
   *
   * @return o código da nova estação
   */
  public int nextSeason() {
    _currentSeason = _currentSeason.next();
    for (Tree tree : _trees.values())
      tree.advanceSeason();
    changed();
    return _currentSeason.ordinal();
  }

  // --------------------------------------------------------------- consultas

  /** @return todas as espécies, por ordem crescente de chave. */
  public Collection<Species> getAllSpecies() {
    return Collections.unmodifiableCollection(_species.values());
  }

  /** @return todos os animais, por ordem crescente de chave. */
  public Collection<Animal> getAnimals() {
    return Collections.unmodifiableCollection(_animals.values());
  }

  /** @return todos os habitats, por ordem crescente de chave. */
  public Collection<Habitat> getHabitats() {
    return Collections.unmodifiableCollection(_habitats.values());
  }

  /** @return todas as árvores, por ordem crescente de chave. */
  public Collection<Tree> getTrees() {
    return Collections.unmodifiableCollection(_trees.values());
  }

  /** @return todos os funcionários, por ordem crescente de chave. */
  public Collection<Employee> getEmployees() {
    return Collections.unmodifiableCollection(_employees.values());
  }

  /** @return todas as vacinas, por ordem crescente de chave. */
  public Collection<Vaccine> getVaccines() {
    return Collections.unmodifiableCollection(_vaccines.values());
  }

  /** @return todas as vacinações, por ordem de aplicação. */
  public Collection<VaccinationRecord> getRecords() {
    return Collections.unmodifiableList(_records);
  }

  /** @return as vacinações que provocaram problemas de saúde, por ordem de aplicação. */
  public Collection<VaccinationRecord> getWrongVaccinations() {
    return _records.stream().filter(VaccinationRecord::isWrong).toList();
  }

  /**
   * @param id a chave da espécie
   * @return a espécie, ou {@code null} se não existir
   */
  public Species getSpecies(String id) {
    return _species.get(id);
  }

  /**
   * @param id a chave do animal
   * @return o animal, ou {@code null} se não existir
   */
  public Animal getAnimal(String id) {
    return _animals.get(id);
  }

  /**
   * @param id a chave do habitat
   * @return o habitat, ou {@code null} se não existir
   */
  public Habitat getHabitat(String id) {
    return _habitats.get(id);
  }

  /**
   * @param id a chave da árvore
   * @return a árvore, ou {@code null} se não existir
   */
  public Tree getTree(String id) {
    return _trees.get(id);
  }

  /**
   * @param id a chave do funcionário
   * @return o funcionário, ou {@code null} se não existir
   */
  public Employee getEmployee(String id) {
    return _employees.get(id);
  }

  /**
   * @param id a chave da vacina
   * @return a vacina, ou {@code null} se não existir
   */
  public Vaccine getVaccine(String id) {
    return _vaccines.get(id);
  }

  /**
   * @param id a chave do veterinário
   * @return o veterinário com essa chave
   * @throws UnknownVeterinarianKeyExceptionCore se a chave não for de um veterinário
   */
  public Veterinarian getVeterinarian(String id) throws UnknownVeterinarianKeyExceptionCore {
    if (_employees.get(id) instanceof Veterinarian veterinarian)
      return veterinarian;
    throw new UnknownVeterinarianKeyExceptionCore(id);
  }

  // -------------------------------------------------------------- satisfação

  /** @return a soma das satisfações de todos os animais e funcionários, arredondada. */
  public long getGlobalSatisfaction() {
    double total = 0;
    for (Animal animal : _animals.values())
      total += animal.getSatisfaction();
    for (Employee employee : _employees.values())
      total += employee.getSatisfaction();
    return Math.round(total);
  }

  // ----------------------------------------------------------------- registo

  /**
   * Regista uma nova espécie.
   *
   * @param id   chave da espécie
   * @param name nome da espécie
   * @return a espécie registada
   */
  public Species registerSpecies(String id, String name) {
    Species species = new Species(id, name);
    _species.put(id, species);
    changed();
    return species;
  }

  /**
   * Regista um novo animal e aloja-o num habitat.
   *
   * @param id        chave do animal
   * @param name      nome do animal
   * @param speciesId chave da espécie do animal
   * @param habitatId chave do habitat onde o animal fica alojado
   * @return o animal registado
   * @throws DuplicateAnimalKeyExceptionCore se já existir um animal com essa chave
   * @throws UnknownSpeciesKeyExceptionCore  se a espécie não existir
   * @throws UnknownHabitatKeyExceptionCore  se o habitat não existir
   */
  public Animal registerAnimal(String id, String name, String speciesId, String habitatId)
      throws DuplicateAnimalKeyExceptionCore, UnknownSpeciesKeyExceptionCore,
      UnknownHabitatKeyExceptionCore {

    if (_animals.containsKey(id))
      throw new DuplicateAnimalKeyExceptionCore(id);

    Habitat habitat = _habitats.get(habitatId);
    if (habitat == null)
      throw new UnknownHabitatKeyExceptionCore(habitatId);

    Species species = _species.get(speciesId);
    if (species == null)
      throw new UnknownSpeciesKeyExceptionCore(speciesId);

    Animal animal = new Animal(id, name, species, habitat);
    _animals.put(id, animal);
    species.addAnimal(animal);
    habitat.addAnimal(animal);
    changed();
    return animal;
  }

  /**
   * Regista um novo habitat.
   *
   * @param id   chave do habitat
   * @param name nome do habitat
   * @param area área do habitat
   * @return o habitat registado
   * @throws DuplicateHabitatKeyExceptionCore se já existir um habitat com essa chave
   */
  public Habitat registerHabitat(String id, String name, int area)
      throws DuplicateHabitatKeyExceptionCore {

    if (_habitats.containsKey(id))
      throw new DuplicateHabitatKeyExceptionCore(id);

    Habitat habitat = new Habitat(id, name, area);
    _habitats.put(id, habitat);
    changed();
    return habitat;
  }

  /**
   * Regista uma nova árvore, ainda sem habitat associado.
   *
   * @param id                     chave da árvore
   * @param name                   nome da árvore
   * @param age                    idade da árvore, em anos
   * @param baseCleaningDifficulty dificuldade base de limpeza
   * @param type                   {@code PERENE} ou {@code CADUCA}
   * @return a árvore registada
   * @throws DuplicateTreeKeyExceptionCore se já existir uma árvore com essa chave
   * @throws UnrecognizedEntryException    se o tipo de árvore for desconhecido
   */
  public Tree registerTree(String id, String name, int age, int baseCleaningDifficulty, String type)
      throws DuplicateTreeKeyExceptionCore, UnrecognizedEntryException {

    if (_trees.containsKey(id))
      throw new DuplicateTreeKeyExceptionCore(id);

    Tree tree;
    if (Evergreen.TYPE.equalsIgnoreCase(type))
      tree = new Evergreen(id, name, age, baseCleaningDifficulty, _currentSeason);
    else if (Deciduous.TYPE.equalsIgnoreCase(type))
      tree = new Deciduous(id, name, age, baseCleaningDifficulty, _currentSeason);
    else
      throw new UnrecognizedEntryException(type);

    _trees.put(id, tree);
    changed();
    return tree;
  }

  /**
   * Regista uma nova árvore e implanta-a num habitat.
   *
   * @param habitatId              chave do habitat onde plantar a árvore
   * @param id                     chave da árvore
   * @param name                   nome da árvore
   * @param age                    idade da árvore, em anos
   * @param baseCleaningDifficulty dificuldade base de limpeza
   * @param type                   {@code PERENE} ou {@code CADUCA}
   * @return a árvore plantada
   * @throws UnknownHabitatKeyExceptionCore se o habitat não existir
   * @throws DuplicateTreeKeyExceptionCore  se já existir uma árvore com essa chave
   * @throws UnrecognizedEntryException     se o tipo de árvore for desconhecido
   */
  public Tree addTreeToHabitat(String habitatId, String id, String name, int age,
      int baseCleaningDifficulty, String type)
      throws UnknownHabitatKeyExceptionCore, DuplicateTreeKeyExceptionCore,
      UnrecognizedEntryException {

    Habitat habitat = _habitats.get(habitatId);
    if (habitat == null)
      throw new UnknownHabitatKeyExceptionCore(habitatId);

    Tree tree = registerTree(id, name, age, baseCleaningDifficulty, type);
    habitat.addTree(tree);
    changed();
    return tree;
  }

  /**
   * Regista um novo funcionário, sem responsabilidades atribuídas.
   *
   * @param id   chave do funcionário
   * @param name nome do funcionário
   * @param type {@code VET} ou {@code TRT}
   * @return o funcionário registado
   * @throws DuplicateEmployeeKeyExceptionCore se já existir um funcionário com essa chave
   * @throws UnrecognizedEntryException        se o tipo de funcionário for desconhecido
   */
  public Employee registerEmployee(String id, String name, String type)
      throws DuplicateEmployeeKeyExceptionCore, UnrecognizedEntryException {

    if (_employees.containsKey(id))
      throw new DuplicateEmployeeKeyExceptionCore(id);

    Employee employee;
    if (Veterinarian.TYPE.equalsIgnoreCase(type))
      employee = new Veterinarian(id, name);
    else if (Caretaker.TYPE.equalsIgnoreCase(type))
      employee = new Caretaker(id, name);
    else
      throw new UnrecognizedEntryException(type);

    _employees.put(id, employee);
    changed();
    return employee;
  }

  /**
   * Regista uma nova vacina.
   *
   * @param id         chave da vacina
   * @param name       nome da vacina
   * @param speciesIds chaves das espécies a que a vacina se destina
   * @return a vacina registada
   * @throws DuplicateVaccineKeyExceptionCore se já existir uma vacina com essa chave
   * @throws UnknownSpeciesKeyExceptionCore   se alguma das espécies não existir
   */
  public Vaccine registerVaccine(String id, String name, Collection<String> speciesIds)
      throws DuplicateVaccineKeyExceptionCore, UnknownSpeciesKeyExceptionCore {

    if (_vaccines.containsKey(id))
      throw new DuplicateVaccineKeyExceptionCore(id);

    List<Species> species = new ArrayList<>();
    for (String speciesId : speciesIds) {
      Species target = _species.get(speciesId);
      if (target == null)
        throw new UnknownSpeciesKeyExceptionCore(speciesId);
      species.add(target);
    }

    Vaccine vaccine = new Vaccine(id, name, species);
    _vaccines.put(id, vaccine);
    changed();
    return vaccine;
  }

  // ---------------------------------------------------------------- operação

  /**
   * Altera a área de um habitat.
   *
   * @param habitatId chave do habitat
   * @param area      a nova área
   * @throws UnknownHabitatKeyExceptionCore se o habitat não existir
   */
  public void changeHabitatArea(String habitatId, int area) throws UnknownHabitatKeyExceptionCore {
    Habitat habitat = _habitats.get(habitatId);
    if (habitat == null)
      throw new UnknownHabitatKeyExceptionCore(habitatId);

    habitat.setArea(area);
    changed();
  }

  /**
   * Altera a adequação de um habitat a uma espécie.
   *
   * @param habitatId chave do habitat
   * @param speciesId chave da espécie
   * @param influence a nova adequação
   * @throws UnknownHabitatKeyExceptionCore se o habitat não existir
   * @throws UnknownSpeciesKeyExceptionCore se a espécie não existir
   */
  public void changeInfluence(String habitatId, String speciesId, Influence influence)
      throws UnknownHabitatKeyExceptionCore, UnknownSpeciesKeyExceptionCore {

    Habitat habitat = _habitats.get(habitatId);
    if (habitat == null)
      throw new UnknownHabitatKeyExceptionCore(habitatId);

    Species species = _species.get(speciesId);
    if (species == null)
      throw new UnknownSpeciesKeyExceptionCore(speciesId);

    habitat.setInfluenceOn(species, influence);
    changed();
  }

  /**
   * Transfere um animal para outro habitat.
   *
   * @param animalId  chave do animal
   * @param habitatId chave do habitat de destino
   * @throws UnknownAnimalKeyExceptionCore  se o animal não existir
   * @throws UnknownHabitatKeyExceptionCore se o habitat não existir
   */
  public void transferAnimal(String animalId, String habitatId)
      throws UnknownAnimalKeyExceptionCore, UnknownHabitatKeyExceptionCore {

    Animal animal = _animals.get(animalId);
    if (animal == null)
      throw new UnknownAnimalKeyExceptionCore(animalId);

    Habitat habitat = _habitats.get(habitatId);
    if (habitat == null)
      throw new UnknownHabitatKeyExceptionCore(habitatId);

    animal.getHabitat().removeAnimal(animal);
    habitat.addAnimal(animal);
    animal.setHabitat(habitat);
    changed();
  }

  /**
   * Atribui uma responsabilidade a um funcionário; nada acontece se ele já a tiver.
   *
   * @param employeeId       chave do funcionário
   * @param responsibilityId chave do habitat ou da espécie
   * @throws UnknownEmployeeKeyExceptionCore se o funcionário não existir
   * @throws NoResponsibilityExceptionCore   se a responsabilidade não existir ou não
   *                                         for adequada a este funcionário
   */
  public void addResponsibility(String employeeId, String responsibilityId)
      throws UnknownEmployeeKeyExceptionCore, NoResponsibilityExceptionCore {

    Employee employee = requireEmployee(employeeId);
    HotelEntity responsibility = findResponsibility(responsibilityId);

    if (responsibility == null || !employee.addResponsibility(responsibility))
      throw new NoResponsibilityExceptionCore(employeeId, responsibilityId);

    changed();
  }

  /**
   * Retira uma responsabilidade a um funcionário.
   *
   * @param employeeId       chave do funcionário
   * @param responsibilityId chave do habitat ou da espécie
   * @throws UnknownEmployeeKeyExceptionCore se o funcionário não existir
   * @throws NoResponsibilityExceptionCore   se a responsabilidade não existir ou não
   *                                         estiver atribuída a este funcionário
   */
  public void removeResponsibility(String employeeId, String responsibilityId)
      throws UnknownEmployeeKeyExceptionCore, NoResponsibilityExceptionCore {

    Employee employee = requireEmployee(employeeId);
    HotelEntity responsibility = findResponsibility(responsibilityId);

    if (responsibility == null || !employee.removeResponsibility(responsibility))
      throw new NoResponsibilityExceptionCore(employeeId, responsibilityId);

    changed();
  }

  /**
   * Vacina um animal, registando a vacinação e os danos dela resultantes.
   *
   * @param vaccineId chave da vacina
   * @param vetId     chave do veterinário
   * @param animalId  chave do animal
   * @throws UnknownVaccineKeyExceptionCore         se a vacina não existir
   * @throws UnknownVeterinarianKeyExceptionCore    se a chave não for de um veterinário
   * @throws UnknownAnimalKeyExceptionCore          se o animal não existir
   * @throws VeterinarianNotAuthorizedExceptionCore se o veterinário não puder tratar
   *                                                da espécie do animal
   * @throws WrongVaccineMessage                    se a vacina não for adequada ao
   *                                                animal; a vacinação acontece na
   *                                                mesma e os danos ficam registados
   */
  public void vaccinateAnimal(String vaccineId, String vetId, String animalId)
      throws UnknownVaccineKeyExceptionCore, UnknownVeterinarianKeyExceptionCore,
      UnknownAnimalKeyExceptionCore, VeterinarianNotAuthorizedExceptionCore,
      WrongVaccineMessage {

    Vaccine vaccine = _vaccines.get(vaccineId);
    if (vaccine == null)
      throw new UnknownVaccineKeyExceptionCore(vaccineId);

    Veterinarian veterinarian = getVeterinarian(vetId);

    Animal animal = _animals.get(animalId);
    if (animal == null)
      throw new UnknownAnimalKeyExceptionCore(animalId);

    Species species = animal.getSpecies();
    if (!veterinarian.isAuthorizedFor(species))
      throw new VeterinarianNotAuthorizedExceptionCore(vetId, species.getId());

    VaccinationRecord record = new VaccinationRecord(vaccine, veterinarian, animal);
    _records.add(record);
    animal.addRecord(record);
    veterinarian.addRecord(record);
    vaccine.addApplication();
    changed();

    if (record.isWrong())
      throw new WrongVaccineMessage(vaccineId, animalId);
  }

  /**
   * @param employeeId chave do funcionário
   * @return o funcionário com essa chave
   * @throws UnknownEmployeeKeyExceptionCore se o funcionário não existir
   */
  private Employee requireEmployee(String employeeId) throws UnknownEmployeeKeyExceptionCore {
    Employee employee = _employees.get(employeeId);
    if (employee == null)
      throw new UnknownEmployeeKeyExceptionCore(employeeId);
    return employee;
  }

  /**
   * @param responsibilityId chave de um habitat ou de uma espécie
   * @return a entidade correspondente, ou {@code null} se nenhuma existir
   */
  private HotelEntity findResponsibility(String responsibilityId) {
    Habitat habitat = _habitats.get(responsibilityId);
    return habitat != null ? habitat : _species.get(responsibilityId);
  }

  // -------------------------------------------------------------- importação

  /**
   * Povoa o hotel a partir de um ficheiro de dados textuais.
   *
   * @param filename nome do ficheiro a importar
   * @throws IOException                se ocorrer um erro de leitura
   * @throws UnrecognizedEntryException se alguma entrada for desconhecida
   */
  public void importFile(String filename) throws IOException, UnrecognizedEntryException {
    try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
      String line;
      while ((line = reader.readLine()) != null)
        importEntry(line.split("\\|"));
    }
  }

  /**
   * @param fields os campos de uma entrada do ficheiro de importação
   * @throws UnrecognizedEntryException se o tipo de entrada for desconhecido ou a
   *                                    entrada não puder ser registada
   */
  private void importEntry(String[] fields) throws UnrecognizedEntryException {
    try {
      switch (fields[0]) {
        case "ESPÉCIE" -> registerSpecies(fields[1], fields[2]);
        case "ÁRVORE" -> registerTree(fields[1], fields[2],
            Integer.parseInt(fields[3]), Integer.parseInt(fields[4]), fields[5]);
        case "HABITAT" -> importHabitat(fields);
        case "ANIMAL" -> registerAnimal(fields[1], fields[2], fields[3], fields[4]);
        case "TRATADOR" -> importEmployee(fields, Caretaker.TYPE);
        case "VETERINÁRIO" -> importEmployee(fields, Veterinarian.TYPE);
        case "VACINA" -> registerVaccine(fields[1], fields[2], optionalList(fields, 3));
        default -> throw new UnrecognizedEntryException(fields[0]);
      }
    } catch (DuplicateAnimalKeyExceptionCore | DuplicateEmployeeKeyExceptionCore
        | DuplicateHabitatKeyExceptionCore | DuplicateTreeKeyExceptionCore
        | DuplicateVaccineKeyExceptionCore | UnknownEmployeeKeyExceptionCore
        | UnknownHabitatKeyExceptionCore | UnknownSpeciesKeyExceptionCore
        | UnknownTreeKeyExceptionCore | NoResponsibilityExceptionCore e) {
      throw new UnrecognizedEntryException(fields[0]);
    }
  }

  /**
   * @param fields os campos de uma entrada {@code HABITAT}
   * @throws DuplicateHabitatKeyExceptionCore se já existir um habitat com essa chave
   * @throws UnknownTreeKeyExceptionCore      se alguma das árvores não existir
   */
  private void importHabitat(String[] fields)
      throws DuplicateHabitatKeyExceptionCore, UnknownTreeKeyExceptionCore {

    Habitat habitat = registerHabitat(fields[1], fields[2], Integer.parseInt(fields[3]));
    for (String treeId : optionalList(fields, 4)) {
      Tree tree = _trees.get(treeId);
      if (tree == null)
        throw new UnknownTreeKeyExceptionCore(treeId);
      habitat.addTree(tree);
    }
  }

  /**
   * @param fields os campos de uma entrada {@code TRATADOR} ou {@code VETERINÁRIO}
   * @param type   o tipo de funcionário a registar
   * @throws DuplicateEmployeeKeyExceptionCore se já existir um funcionário com essa chave
   * @throws UnknownEmployeeKeyExceptionCore   se o funcionário não puder ser consultado
   * @throws NoResponsibilityExceptionCore     se alguma responsabilidade for inválida
   * @throws UnrecognizedEntryException        se o tipo de funcionário for desconhecido
   */
  private void importEmployee(String[] fields, String type)
      throws DuplicateEmployeeKeyExceptionCore, UnknownEmployeeKeyExceptionCore,
      NoResponsibilityExceptionCore, UnrecognizedEntryException {

    String id = fields[1];
    registerEmployee(id, fields[2], type);
    for (String responsibilityId : optionalList(fields, 3))
      addResponsibility(id, responsibilityId);
  }

  /**
   * @param fields os campos de uma entrada do ficheiro de importação
   * @param index  o índice do campo opcional que contém a lista
   * @return os elementos da lista separada por vírgulas, ou uma lista vazia
   */
  private static List<String> optionalList(String[] fields, int index) {
    if (fields.length <= index || fields[index].isBlank())
      return List.of();
    return List.of(fields[index].split(","));
  }
}
