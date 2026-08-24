package hva;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import hva.exceptions.UnknownAnimalKeyExceptionCore;
import hva.exceptions.UnknownHabitatKeyExceptionCore;

/**
 * A pré-visualização de uma transferência tem de responder o mesmo que a
 * transferência a sério, e não deixar rasto nenhum.
 */
class SatisfactionIfMovedToTest {

  private Hotel _hotel;

  @BeforeEach
  void buildHotel() throws Exception {
    _hotel = new Hotel();

    _hotel.registerHabitat("SAVANA", "Savana", 800);
    _hotel.registerHabitat("FLORESTA", "Floresta", 300);
    _hotel.registerSpecies("LEAO", "Leão");
    _hotel.registerSpecies("PANTERA", "Pantera");

    _hotel.registerAnimal("SIMBA", "Simba", "LEAO", "SAVANA");
    _hotel.registerAnimal("NALA", "Nala", "LEAO", "SAVANA");
    _hotel.registerAnimal("SHERE", "Shere Khan", "PANTERA", "FLORESTA");

    _hotel.changeInfluence("SAVANA", "PANTERA", Influence.NEG);

    _hotel.registerEmployee("TRT1", "Rafiki", "TRT");
    _hotel.addResponsibility("TRT1", "SAVANA");
    _hotel.addResponsibility("TRT1", "FLORESTA");

    _hotel.setChanged(false);
  }

  @Test
  @DisplayName("prevê exactamente o que a transferência viria a dar")
  void agreesWithTheRealTransfer() throws Exception {
    long predicted = _hotel.satisfactionIfMovedTo("SHERE", "SAVANA");

    _hotel.transferAnimal("SHERE", "SAVANA");

    assertEquals(predicted, _hotel.getGlobalSatisfaction());
  }

  @Test
  @DisplayName("não mexe no hotel: nem no habitat do animal, nem na satisfação")
  void leavesNoTrace() throws Exception {
    long before = _hotel.getGlobalSatisfaction();

    _hotel.satisfactionIfMovedTo("SHERE", "SAVANA");

    assertEquals("FLORESTA", _hotel.getAnimal("SHERE").getHabitat().getId());
    assertEquals(1, _hotel.getHabitat("FLORESTA").getPopulation());
    assertEquals(2, _hotel.getHabitat("SAVANA").getPopulation());
    assertEquals(before, _hotel.getGlobalSatisfaction());
  }

  @Test
  @DisplayName("não marca o hotel como alterado: pré-visualizar não é jogar")
  void doesNotMarkTheHotelAsChanged() throws Exception {
    _hotel.satisfactionIfMovedTo("SHERE", "SAVANA");

    assertFalse(_hotel.hasChanged());
  }

  @Test
  @DisplayName("mudar para o habitat onde já se está não muda nada")
  void movingNowhereChangesNothing() throws Exception {
    assertEquals(_hotel.getGlobalSatisfaction(), _hotel.satisfactionIfMovedTo("SHERE", "FLORESTA"));
  }

  @Test
  @DisplayName("conta os vizinhos e os tratadores, não só o animal que se mexe")
  void accountsForEveryoneAffected() throws Exception {
    // A pantera sozinha na floresta está bem. Na savana apanha influência
    // negativa e dois leões de espécie diferente; os leões perdem por a terem
    // ao lado; e o tratador vê o trabalho passar de um habitat para o outro.
    long staying = _hotel.getGlobalSatisfaction();
    long moving = _hotel.satisfactionIfMovedTo("SHERE", "SAVANA");

    assertEquals(true, moving < staying,
        "mudar a pantera para junto dos leões tem de piorar o hotel");
  }

  @Test
  @DisplayName("recusa chaves que não existem")
  void refusesUnknownKeys() {
    assertThrows(UnknownAnimalKeyExceptionCore.class,
        () -> _hotel.satisfactionIfMovedTo("DUMBO", "SAVANA"));
    assertThrows(UnknownHabitatKeyExceptionCore.class,
        () -> _hotel.satisfactionIfMovedTo("SHERE", "DESERTO"));
  }
}
