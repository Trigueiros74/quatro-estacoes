# Quatro Estações — contexto do projecto

Jogo de gestão por turnos construído sobre o núcleo Java de um projecto
académico já concluído. **Cada turno é uma estação do ano.**

Trabalhamos em **português (pt-PT)**.

---

## Onde estão as coisas

| | Caminho | Repositório |
|:---|:---|:---|
| **Este projecto** | `~/trabalhos/quatro-estacoes` | `github.com/Trigueiros74/quatro-estacoes` — privado, branch `main` |
| **Origem (congelada)** | `~/trabalhos/097` | `github.com/Trigueiros74/Projeto---PO` — público, branch `master` |

O repositório de origem é a **entrega avaliada de Programação com Objectos** e
está congelado: não se lhe toca. Tem ainda um remote `origin` para o GitLab da
cadeira — **nunca fazer push para lá**.

Colaborador: `jpborie05` (João Borie), convite pendente nos dois repositórios.

Plano de jogo completo:
<https://claude.ai/code/artifact/7cebe2c5-4490-4f83-95b8-91592a029046>

---

## Estado actual

Um commit. `hva-core` e `hva-app` importados, a compilar sem avisos, **213/213
testes automáticos a passar**.

```console
$ make            # build (herdado; a substituir por Gradle na fase 0)
$ ./run-tests.sh  # 213 testes; aceita padrão, ex.: ./run-tests.sh 'A-19-*'
```

`po-uilib` está no `.gitignore` mas existe localmente em `po-uilib/po-uilib.jar`
— é preciso para compilar. Java 17+ (usa *pattern matching* no `instanceof`,
`Map.of`, `toList()`, `@Serial`).

```text
hva-core/     Domínio: animais, habitats, árvores, funcionários, vacinas
hva-app/      CLI de referência — é ela que mantém os 213 testes vivos
tests/        213 casos de teste e saídas esperadas
hva-game/     (a criar) Turnos, objectivos, eventos, pontuação
hva-web/      (a criar) TeaVM + interface
```

---

## Regra de ouro

**As regras de jogo vão no `hva-game`, nunca no `hva-core`.** A fronteira limpa
entre domínio e jogo é o que dá valor ao projecto; se ela cair, isto passa a ser
mais um CRUD.

Só duas adições ao núcleo estão previstas, porque pertencem mesmo ao domínio:

1. **Remoções** — apagar animal, habitat, árvore, funcionário. O enunciado
   original nunca as pediu; uma interface vai querê-las.
2. **`satisfactionIfMovedTo(animal, habitat)`** — pré-visualizar o ganho ou
   perda *antes* de largar o animal. É o que torna o jogo legível.

Capacidade de habitats, orçamento, turnos, objectivos e eventos são regras de
jogo: `hva-game`.

---

## O que é preciso saber do núcleo

* `Hotel` é a raiz do agregado e possui todas as entidades. Todo o estado é da
  instância — podem coexistir vários hotéis.
* `HotelEntity` é a entidade com chave: `Species`, `Animal`, `Habitat`, `Tree`,
  `Employee`, `Vaccine`. **`VaccinationRecord` não é** — uma vacinação não tem
  chave, é identificada pela ordem de aplicação.
* Chaves **insensíveis a maiúsculas**; colecções ordenadas por chave via
  `KeyOrder`.
* Padrões: **State** (`TreeState` / `DeciduousState` / `EvergreenState`),
  **Strategy** (`SatisfactionStrategy` / `DefaultSatisfaction`), **Command**
  (`hva-app`).
* Excepções: o núcleo lança `*ExceptionCore` que transportam **apenas chaves**;
  cada comando traduz para subclasses de `CommandException`. O núcleo nunca
  constrói mensagens para o utilizador.

### ⚠️ A armadilha da serialização

**Todas as colecções de entidades têm de ser `TreeSet`/`TreeMap`, nunca
`HashSet`/`HashMap`.** Existem ciclos de referências (um animal conhece a sua
espécie e a espécie conhece os seus animais); o `readObject` de um `HashSet`
pede `hashCode()` aos elementos antes de as chaves estarem repostas, e rebenta
com `NullPointerException` ao abrir um ficheiro guardado. Este bug custava 9
testes. Não o reintroduzir.

---

## Design do jogo — decisões já tomadas

O domínio já era um jogo; as fórmulas do enunciado **são** as mecânicas.

| Fórmula | Mecânica |
|:---|:---|
| `+3 × iguais − 2 × diferentes` | Puzzle de colocação: juntar a mesma espécie compensa |
| `+ área / população` | Contrapeso: amontoar reduz o espaço por animal |
| `Σ trabalho(h) / n_tratadores(h)` | Puzzle de pessoal: especializar ou espalhar |
| `dificuldade × esforço_sazonal × log(idade+1)` | Pressão de calendário |
| `dano(vacina, animal)` | Risco com consequência permanente |
| satisfação global | Pontuação |

**Tensão central** — esforço sazonal das árvores:

| | Primavera | Verão | Outono | Inverno |
|:---|:---:|:---:|:---:|:---:|
| Folha caduca | 1 | 2 | **5** | 0 |
| Folha perene | 1 | 1 | 1 | 2 |

Plantar caducas é a jogada gananciosa: zero trabalho no Inverno. Depois chega o
Outono e o esforço quintuplica de uma vez em todos os habitats. A idade agrava e
nunca desce (`log(idade+1)`).

**Ciclo de turno:** observar → agir (acções limitadas) → avançar estação →
pontuar.

**Começar por cenários-puzzle, não por campanha.** «Seis animais, dois habitats,
um turno: maximiza a satisfação.» Pequeno, resolve-se em dois minutos, mostra as
fórmulas a funcionar e é partilhável por link. A campanha longa vem depois — ou
nunca, se os puzzles forem bons.

---

## Arquitectura

**TeaVM** compila *bytecode* Java para JavaScript/WebAssembly: o `hva-core`
corre dentro do browser, sem servidor. Alojamento estático e resposta
instantânea ao arrastar um animal.

*Senão conhecido:* o TeaVM não suporta serialização Java. No *build* web
guarda-se JSON no `localStorage`; a serialização fica só na CLI.

*Alternativa se falhar:* Spring Boot + REST. Convencional, mas precisa de
alojamento e cada acção é uma ida ao servidor — mau para a pré-visualização ao
arrastar.

*Descartados:* reescrever o domínio em TypeScript (perde-se o objectivo do
projecto), submódulo git e pacote publicado (o núcleo vai mudar, e qualquer
alteração passaria a ser uma dança entre dois repositórios).

---

## Roteiro

| | Fase | Estado |
|:---:|:---|:---|
| 00 | Gradle multi-módulo, 213 testes em `./gradlew test` | **a seguir** |
| 01 | *Spike* do TeaVM — chamar `getGlobalSatisfaction()` do browser | *gate*: 1 semana, senão cair para Spring Boot |
| 02 | Um cenário jogável: arrastar animais, satisfação ao vivo, **publicar** | |
| 03 | Turnos e estações | |
| 04 | Funcionários e vacinação | |
| 05 | Campanha, eventos, saves | |
| 06 | Polimento | |

### Fase 0 em concreto

* `settings.gradle.kts` multi-módulo; `hva-app` depende de `hva-core`.
* `po-uilib.jar` como dependência local (`libs/`).
* Teste JUnit que percorre `tests/auto-tests/`, corre a `App` com `-Din`/`-Dout`
  e compara com o esperado. A comparação usada na avaliação é
  `diff -iwub -B` sobre a saída com espaços colapsados — o `run-tests.sh`
  replica-a e serve de referência.

Não avançar para a fase 1 sem os 213 testes verdes no Gradle: são a rede de
segurança para mexer no núcleo.

---

## Convenções de trabalho

* Mensagens de commit curtas, **sem co-autor Claude**.
* Repositórios **sem descrição** no GitHub.
* Nomes de repositório sem espaços (o `Projeto---PO` ficou assim por causa
  disso).
* Correr `./run-tests.sh` a cada refactorização do núcleo.
