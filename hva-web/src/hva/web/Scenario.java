package hva.web;

import java.util.List;

import hva.Hotel;
import hva.Influence;

/**
 * O cenário do <i>spike</i>, construído em código.
 *
 * <p>Não passa pelo {@code Hotel.importFile}: o TeaVM não tem sistema de
 * ficheiros, e ler o formato de importação a partir de texto é trabalho da fase
 * 2 ({@code importFrom(Reader)}).
 *
 * <p>É escolhido para exercitar o que mais provavelmente não sobrevive à
 * travessia para JavaScript: colecções ordenadas por comparador, {@code enum}
 * com {@code toString} próprio, {@code Math.log} sobre idades diferentes, e o
 * cálculo do dano de uma vacina desadequada, que compara nomes caractere a
 * caractere com um {@code StringBuilder}.
 */
final class Scenario {

  private Scenario() {
  }

  /**
   * @return um hotel com dois habitats, quatro animais de duas espécies,
   *         árvores dos dois tipos, um tratador, um veterinário e uma vacinação
   *         desadequada
   * @throws Exception se a construção falhar, o que só acontece se o cenário
   *                   aqui escrito for incoerente
   */
  static Hotel build() throws Exception {
    Hotel hotel = new Hotel();

    hotel.registerSpecies("LEAO", "Leão");
    hotel.registerSpecies("PANTERA", "Pantera");

    hotel.registerHabitat("SAVANA", "Savana", 800);
    hotel.registerHabitat("FLORESTA", "Floresta", 300);

    // Uma caduca e uma perene em cada habitat, com idades muito diferentes: é o
    // `log(idade+1)` que as separa.
    hotel.addTreeToHabitat("SAVANA", "BAOBA", "Baobá", 20, 4, "CADUCA");
    hotel.addTreeToHabitat("SAVANA", "ACACIA", "Acácia", 3, 2, "PERENE");
    hotel.addTreeToHabitat("FLORESTA", "CARVALHO", "Carvalho", 50, 5, "CADUCA");
    hotel.addTreeToHabitat("FLORESTA", "PINHEIRO", "Pinheiro", 8, 3, "PERENE");

    hotel.registerAnimal("SIMBA", "Simba", "LEAO", "SAVANA");
    hotel.registerAnimal("NALA", "Nala", "LEAO", "SAVANA");
    hotel.registerAnimal("BAGHEERA", "Bagheera", "PANTERA", "SAVANA");
    hotel.registerAnimal("SHERE", "Shere Khan", "PANTERA", "FLORESTA");

    hotel.changeInfluence("SAVANA", "LEAO", Influence.POS);
    hotel.changeInfluence("SAVANA", "PANTERA", Influence.NEG);

    hotel.registerEmployee("TRT1", "Rafiki", "TRT");
    hotel.addResponsibility("TRT1", "SAVANA");
    hotel.addResponsibility("TRT1", "FLORESTA");

    hotel.registerEmployee("VET1", "Doolittle", "VET");
    hotel.addResponsibility("VET1", "LEAO");
    hotel.addResponsibility("VET1", "PANTERA");

    hotel.registerVaccine("VAC1", "Antirrábica", List.of("LEAO"));

    // Uma boa e uma má: a má passa pelo cálculo do dano.
    vaccinate(hotel, "SIMBA");
    vaccinate(hotel, "BAGHEERA");

    return hotel;
  }

  /**
   * Vacina um animal com a vacina do cenário, tratando a vacinação desadequada
   * como resultado esperado e não como erro.
   *
   * @param hotel  o hotel do cenário
   * @param animal a chave do animal a vacinar
   */
  private static void vaccinate(Hotel hotel, String animal) throws Exception {
    try {
      hotel.vaccinateAnimal("VAC1", "VET1", animal);
    } catch (hva.exceptions.WrongVaccineMessage expected) {
      // A vacinação aconteceu à mesma; o dano fica no historial do animal.
    }
  }
}
