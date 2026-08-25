// Verifica o ciclo que a fase 2 vai fazer ao arrastar um animal: ler a
// satisfação, mexer, voltar a ler.
//
// Corre sobre o módulo gerado pelo TeaVM, a partir de JavaScript — é o que
// distingue «o Java corre no browser» de «o JavaScript comanda o domínio».

import assert from "node:assert/strict";

const [, , modulePath] = process.argv;
const { Hotel } = await import(modulePath);

// A fábrica é estática de propósito: um construtor exportado correria antes de
// o runtime do TeaVM estar inicializado. Ver o comentário na Bridge.
const hotel = Hotel.create();

assert.equal(hotel.getHabitatIds(), "FLORESTA,SAVANA", "habitats por ordem de chave");
assert.equal(hotel.getAnimalIds(), "BAGHEERA,NALA,SHERE,SIMBA", "animais por ordem de chave");
assert.equal(hotel.getSeason(), "SPRING", "estação inicial");
assert.equal(hotel.getGlobalSatisfaction(), 361, "satisfação inicial");

// Os dados que a página precisa para desenhar o tabuleiro.
assert.equal(hotel.getHabitatName("SAVANA"), "Savana");
assert.equal(hotel.getHabitatArea("SAVANA"), 800);
assert.equal(hotel.getAnimalName("SHERE"), "Shere Khan");
assert.equal(hotel.getSpeciesOf("SHERE"), "PANTERA");
assert.equal(hotel.getInfluence("SAVANA", "SHERE"), "NEG", "a savana não é sítio para panteras");
assert.equal(hotel.getInfluence("FLORESTA", "SHERE"), "NEU", "a floresta é indiferente");

// As árvores, e o ciclo biológico de onde a interface tira o aspecto da copa.
assert.equal(hotel.getTreeIdsIn("SAVANA"), "ACACIA,BAOBA", "árvores por ordem de chave");
assert.equal(hotel.getTreeType("BAOBA"), "CADUCA");
assert.equal(hotel.getTreeType("ACACIA"), "PERENE");
assert.equal(hotel.getBiologicalCycle("BAOBA"), "GERARFOLHAS", "na primavera todas geram folhas");
assert.equal(hotel.getTreeAge("BAOBA"), 20);

// A pantera está na floresta, onde ninguém a incomoda. A savana tem influência
// negativa para panteras e dois leões lá dentro: mudá-la para lá é uma péssima
// jogada, e o número tem de o dizer.
assert.equal(hotel.getHabitatOf("SHERE"), "FLORESTA");

// A pré-visualização tem de prometer exactamente o que a transferência cumpre.
const previsto = hotel.satisfactionIfMovedTo("SHERE", "SAVANA");
assert.ok(previsto < hotel.getGlobalSatisfaction(), "a má jogada tem de aparecer como má");
assert.equal(hotel.getHabitatOf("SHERE"), "FLORESTA", "pré-visualizar não mexe em nada");
assert.equal(hotel.satisfactionIfMovedTo("SHERE", "DESERTO"), hotel.getGlobalSatisfaction(),
  "destino inexistente não promete nada");
assert.equal(hotel.transferAnimal("SHERE", "SAVANA"), true, "transferência válida");
assert.equal(hotel.getHabitatOf("SHERE"), "SAVANA");
assert.equal(hotel.getGlobalSatisfaction(), 39, "satisfação depois da má jogada");
assert.equal(previsto, 39, "a pré-visualização cumpriu o que prometeu");

assert.equal(hotel.transferAnimal("SHERE", "DESERTO"), false, "destino inexistente");
assert.equal(hotel.transferAnimal("DUMBO", "SAVANA"), false, "animal inexistente");
assert.equal(hotel.getHabitatOf("SHERE"), "SAVANA", "recusa não mexe em nada");

// Um ano completo. O Outono é o fundo do poço: o esforço das caducas quintuplica.
const year = [];
for (let i = 0; i < 4; i++) {
  hotel.nextSeason();
  year.push([hotel.getSeason(), hotel.getGlobalSatisfaction()]);
}
assert.deepEqual(year, [["SUMMER", 7], ["FALL", -89], ["WINTER", 61], ["SPRING", 38]],
  "o ano inteiro, com o Outono no fundo");

// A caduca despe-se no inverno e não dá trabalho nenhum; a perene é que o dá.
hotel.nextSeason();
hotel.nextSeason();
hotel.nextSeason();
assert.equal(hotel.getSeason(), "WINTER");
assert.equal(hotel.getBiologicalCycle("BAOBA"), "SEMFOLHAS");
assert.equal(hotel.getCleaningEffort("BAOBA"), 0, "uma caduca despida não dá trabalho");
assert.equal(hotel.getBiologicalCycle("ACACIA"), "LARGARFOLHAS");
assert.ok(hotel.getCleaningEffort("ACACIA") > 0, "a perene é que trabalha no inverno");

console.log("a ponte responde: " + year.length + " estações, leitura e mutação a partir de JavaScript.");
