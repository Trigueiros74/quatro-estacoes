<div align="center">

# 🦁 Quatro Estações

**Um hotel veterinário Java, traduzido para uma página web onde se joga**

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

Faz-se tudo o que a aplicação de consola original fazia — criar espécies, alojar
animais, plantar árvores, contratar pessoal, vacinar — mas a ver e a mexer: os
animais estão desenhados, arrastam-se entre habitats, e cada acção devolve uma
animação positiva ou negativa.

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
├── hva-web/      (fase 1) TeaVM + interface: desenho, arrastar, animações
├── hva-game/     (fase 5) Restrições: orçamento, capacidade, acções por turno
└── tests/        213 casos de teste e saídas esperadas
```

**Regra de ouro:** as regras de jogo vão no `hva-game`, nunca no `hva-core`. A
fronteira limpa entre domínio e jogo é o que dá valor ao projecto — se ela
cair, isto passa a ser mais um CRUD.

As únicas adições previstas ao núcleo, porque pertencem mesmo ao domínio:

* remoções (apagar animal, habitat, árvore, funcionário);
* `satisfactionIfMovedTo(animal, habitat)`, para pré-visualizar o ganho antes
  de largar o animal;
* `importFrom(Reader)`, porque no browser não há sistema de ficheiros.

As **posições** dos animais e dos habitats também não vão para o núcleo: nada no
domínio sabe onde as coisas estão, e desenhar é matéria do `hva-web`.

---

## 🏗️ Arquitectura

O núcleo Java corre **dentro do browser**, compilado para JavaScript/WebAssembly
com [TeaVM](https://teavm.org). Sem servidor: alojamento estático e resposta
instantânea ao arrastar um animal.

O senão conhecido é que a serialização Java não é suportada pelo TeaVM — no
*build* web guarda-se JSON no `localStorage`, e a serialização fica só na CLI.
Se o TeaVM resistir mais de uma semana, a alternativa é Spring Boot + REST.

---

## 🗺️ Guia

| | Fase | Estado |
|:---:|:---|:---|
| 00 | Migrar para Gradle multi-módulo, 213 testes em `./gradlew test` | ✅ feita |
| 01 | *Spike* do TeaVM — `getGlobalSatisfaction()` a correr no browser | ✅ feita |
| 02 | Prova de que o domínio se comanda do browser, **publicado** | ✅ feita |
| 03 | O jogo a parecer um jogo — mundo em azulejos, *sprites*, as acções todas | 🔨 a decorrer |
| 04 | Cenários carregados de texto, em vez de fixos no código | |
| 05 | Restrições: saúde a valer pontos, orçamento, capacidade, acções por turno | |
| 06 | Guardar em `localStorage`, eventos, polimento | |

---

## 🚀 Compilação e testes

Basta um JDK 17 ou posterior — o *wrapper* trata do resto. É necessária a
biblioteca **po-uilib**, em `libs/po-uilib.jar`, em `po-uilib/po-uilib.jar` ou
em `/usr/share/java`.

```console
$ ./gradlew build              # compilar tudo e correr os 213 testes
$ ./gradlew test               # só os testes
$ ./gradlew test -Ptests='A-19-*'   # só os casos que interessam
```

O núcleo compilado para o browser precisa também de `node`, para as verificações:

```console
$ ./gradlew :hva-web:spike     # corre o cenário na JVM e em JavaScript e compara
$ ./gradlew :hva-web:webPage   # monta build/web/index.html, auto-contido
```

Os casos partilham um directório de trabalho e correm por ordem de nome, tal
como no `run-tests.sh`: há testes que abrem ficheiros de estado gravados por
testes anteriores. A comparação replica a da avaliação — `diff -iwub -B` sobre a
saída com os espaços colapsados. Quando um caso falha, o relatório fica em
`hva-app/build/reports/tests/test/index.html` e a saída obtida em
`hva-app/build/auto-tests/`.

O *build* herdado continua a funcionar e serve de segunda opinião independente
sobre o mesmo corpo de testes:

```console
$ make && ./run-tests.sh
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
