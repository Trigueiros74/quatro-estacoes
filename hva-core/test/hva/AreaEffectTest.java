package hva;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * O efeito da área de um habitat na satisfação global.
 *
 * <p>A resposta é surpreendente e vale a pena estar escrita: <b>alargar um
 * habitat que alguém limpa não muda absolutamente nada</b>. O espaço que a área
 * dá aos animais é, ao cêntimo, o trabalho que dá a quem limpa.
 *
 * <ul>
 *   <li>cada residente ganha {@code área / população}, e são {@code população}
 *       residentes, pelo que os animais ganham ao todo exactamente
 *       {@code Δárea};</li>
 *   <li>o trabalho do habitat é {@code área + 3 × população + limpeza}, e cresce
 *       também {@code Δárea};</li>
 *   <li>esse trabalho é repartido pelos tratadores do habitat, mas <b>a soma do
 *       que lhes é cobrado é sempre o trabalho inteiro</b> — mais tratadores
 *       repartem-no, não o reduzem.</li>
 * </ul>
 *
 * <p>Sobra uma brecha: um habitat que <b>ninguém</b> limpa não cobra o seu
 * trabalho a ninguém, e aí a área é ganho puro. Não é defeito destes testes; é o
 * que as fórmulas do enunciado dizem. Corrigi-lo é matéria de regra de jogo, e
 * portanto do {@code hva-game}.
 */
class AreaEffectTest {

  private Hotel _hotel;

  @BeforeEach
  void buildHotel() throws Exception {
    _hotel = new Hotel();

    _hotel.registerHabitat("SAVANA", "Savana", 800);
    _hotel.registerHabitat("FLORESTA", "Floresta", 300);
    _hotel.registerSpecies("LEAO", "Leão");

    _hotel.registerAnimal("SIMBA", "Simba", "LEAO", "SAVANA");
    _hotel.registerAnimal("NALA", "Nala", "LEAO", "SAVANA");
    _hotel.registerAnimal("SHERE", "Shere Khan", "LEAO", "FLORESTA");

    // Só a savana é limpa; a floresta não tem quem trate dela.
    _hotel.registerEmployee("TRT1", "Rafiki", "TRT");
    _hotel.addResponsibility("TRT1", "SAVANA");
  }

  @Test
  @DisplayName("num habitat limpo por alguém, a área não mexe na satisfação global")
  void areaIsNeutralWhenSomeoneCleansIt() throws Exception {
    long antes = _hotel.getGlobalSatisfaction();

    _hotel.changeHabitatArea("SAVANA", 2400);

    assertEquals(antes, _hotel.getGlobalSatisfaction(),
        "o espaço dado aos animais é o trabalho dado ao tratador");
  }

  @Test
  @DisplayName("mais tratadores repartem o trabalho, não o reduzem")
  void moreCaretakersSplitTheWorkButDoNotShrinkIt() throws Exception {
    _hotel.registerEmployee("TRT2", "Zazu", "TRT");
    _hotel.addResponsibility("TRT2", "SAVANA");

    long antes = _hotel.getGlobalSatisfaction();

    _hotel.changeHabitatArea("SAVANA", 2400);

    assertEquals(antes, _hotel.getGlobalSatisfaction(),
        "com dois tratadores continua a cancelar-se, porque a soma cobrada é a mesma");
  }

  @Test
  @DisplayName("num habitat que ninguém limpa, a área é ganho puro")
  void areaIsFreeGainWhenNobodyCleansIt() throws Exception {
    long antes = _hotel.getGlobalSatisfaction();

    // A floresta não está atribuída a tratador nenhum, pelo que o trabalho que
    // a área lhe acrescenta não é cobrado a ninguém.
    _hotel.changeHabitatArea("FLORESTA", 1300);

    assertEquals(antes + 1000, _hotel.getGlobalSatisfaction(),
        "o único residente fica com toda a área acrescentada e ninguém paga o trabalho");
    assertTrue(_hotel.getGlobalSatisfaction() > antes);
  }

  @Test
  @DisplayName("contratar um tratador para um habitat esquecido custa o trabalho todo de uma vez")
  void hiringForAForgottenHabitatChargesItsWholeWork() throws Exception {
    long antes = _hotel.getGlobalSatisfaction();

    _hotel.registerEmployee("TRT2", "Zazu", "TRT");
    _hotel.addResponsibility("TRT2", "FLORESTA");

    // O tratador traz 300 de base e leva com o trabalho da floresta: 300 de
    // área mais 3 por cada um dos seus residentes.
    assertEquals(antes + 300 - 303, _hotel.getGlobalSatisfaction());
  }
}
