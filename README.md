<div align="center">

# 🦁 Quatro Estações

**Jogo de gestão por turnos, construído sobre um domínio Java existente**

![Java](https://img.shields.io/badge/language-Java%2017-ED8B00?style=flat-square&logo=openjdk)
![Tests](https://img.shields.io/badge/n%C3%BAcleo-213%2F213-brightgreen?style=flat-square)
![Status](https://img.shields.io/badge/estado-fase%200-B4622A?style=flat-square)

</div>

---

## 📖 Sobre

Gerimos um hotel veterinário: animais de várias espécies alojados em habitats
com árvores, tratados por tratadores e veterinários. **Cada turno é uma estação
do ano.** As árvores mudam de ciclo biológico, o esforço de limpeza sobe e
desce, e a satisfação de todos é recalculada.

O jogo não inventa mecânicas — reutiliza as regras de um domínio que já existia:

| Fórmula | Mecânica |
|:---|:---|
| `20 + 3 × iguais − 2 × diferentes + área/população` | Puzzle de colocação: juntar a mesma espécie compensa, mas amontoar reduz o espaço |
| `Σ trabalho(h) / n_tratadores(h)` | Puzzle de pessoal: especializar ou espalhar |
| `dificuldade × esforço_sazonal × log(idade+1)` | Pressão de calendário |
| `dano(vacina, animal)` | Risco com consequência permanente |

A tensão central vem da tabela do esforço sazonal das árvores:

| | 🌱 Primavera | ☀️ Verão | 🍂 Outono | ❄️ Inverno |
|:---|:---:|:---:|:---:|:---:|
| **Folha caduca** | 1 | 2 | **5** | 0 |
| **Folha perene** | 1 | 1 | 1 | 2 |

Plantar caducas é a jogada gananciosa: não dão trabalho nenhum no Inverno.
Depois chega o Outono e o esforço quintuplica de uma vez, em todos os habitats.

---

## 🧬 Origem

O núcleo (`hva-core`) e a aplicação de consola (`hva-app`) vêm do
[Projeto de Programação com Objectos](https://github.com/Trigueiros74/Projeto---PO)
— IST 2024/25, 213/213 testes automáticos. Esse repositório está congelado como
entrega; este é um projecto novo, com história própria.

O `hva-app` fica aqui como **CLI de referência**: ninguém a usa para jogar, mas
é ela que corre os 213 testes e prova que o núcleo continua correcto a cada
alteração.

---

## 📁 Estrutura

```text
.
├── hva-core/     Domínio: animais, habitats, árvores, funcionários, vacinas
├── hva-app/      CLI de referência — mantém os 213 testes vivos
├── hva-game/     (fase 3) Turnos, objectivos, eventos, pontuação
├── hva-web/      (fase 2) TeaVM + interface do jogo
└── tests/        213 casos de teste e saídas esperadas
```

**Regra de ouro:** as regras de jogo vão no `hva-game`, nunca no `hva-core`. A
fronteira limpa entre domínio e jogo é o que dá valor ao projecto — se ela
cair, isto passa a ser mais um CRUD.

As únicas adições previstas ao núcleo, porque pertencem mesmo ao domínio:

* remoções (apagar animal, habitat, árvore, funcionário);
* `satisfactionIfMovedTo(animal, habitat)`, para pré-visualizar o ganho antes
  de largar o animal.

---

## 🏗️ Arquitectura

O núcleo Java corre **dentro do browser**, compilado para JavaScript/WebAssembly
com [TeaVM](https://teavm.org). Sem servidor: alojamento estático e resposta
instantânea ao arrastar um animal.

O senão conhecido é que a serialização Java não é suportada pelo TeaVM — no
*build* web guarda-se JSON no `localStorage`, e a serialização fica só na CLI.
Se o TeaVM resistir mais de uma semana, a alternativa é Spring Boot + REST.

---

## 🗺️ Roteiro

| | Fase | Estado |
|:---:|:---|:---|
| 00 | Migrar para Gradle multi-módulo, 213 testes em `./gradlew test` | 🔨 a decorrer |
| 01 | *Spike* do TeaVM — chamar `getGlobalSatisfaction()` do browser | |
| 02 | Um cenário jogável: arrastar animais, satisfação ao vivo, **publicar** | |
| 03 | Turnos e estações | |
| 04 | Funcionários e vacinação | |
| 05 | Campanha, eventos, saves | |
| 06 | Polimento | |

---

## 🚀 Compilação e testes

Enquanto a fase 0 não estiver feita, o *build* é o herdado do projecto original.
É necessária a biblioteca **po-uilib**, em `po-uilib/po-uilib.jar` ou em
`/usr/share/java`.

```console
$ make
$ ./run-tests.sh
```

---

## 📄 Licença

Código-fonte sob licença [MIT](LICENSE).

Os casos de teste em `tests/auto-tests/` e a biblioteca **po-uilib** são
material didático do Instituto Superior Técnico, incluídos como contexto, e não
são abrangidos por essa licença.

---

## 👥 Autores

**João Ferreira** e **João Borie**
