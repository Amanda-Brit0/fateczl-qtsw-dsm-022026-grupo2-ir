# Cálculo do IR: Teste Funcional e Teste Estrutural

Trabalho da disciplina **Qualidade e Testes de Software**, referente ao **Grupo 2** do trabalho *Mapa Conceitual e Casos de Teste*.

| | |
|---|---|
| **Instituição** | FATEC Zona Leste (FATECZL) |
| **Curso** | Desenvolvimento de Software Multiplataforma (DSM) |
| **Semestre** | 2026/2 |
| **Professor** | Wilson Vendramel |
| **Autora** | Amanda |

## Sobre o projeto

Implementa o cálculo do Imposto de Renda mensal a partir da **Tabela do IR para Ano-base 2026** (Figura 2a do enunciado) e aplica sobre ele técnicas de teste de software:

- **Teste funcional (caixa-preta):** partição em classes de equivalência e análise de valor limite.
- **Teste estrutural (caixa-branca):** Grafo de Fluxo de Controle (GFC), complexidade ciclomática de McCabe e caminhos independentes.

Os testes de unidade foram escritos em **Java + JUnit 5**.

## Arquivos

| Arquivo | Função |
|---|---|
| `ImpostoRenda.java` | Algoritmo de cálculo do IR (unidade sob teste) |
| `ImpostoRendaTest.java` | Testes de unidade com JUnit 5 |
| `SimularDados.java` | Simulador que mostra entrada, saída obtida, saída esperada e veredito de cada caso |
| `ImpostoRendaDefeituoso.java` | Cópia do algoritmo com 4 defeitos injetados, só para demonstração |
| `junit-platform-console-standalone-1.9.1.jar` | JUnit 5, incluído para facilitar a execução |

## Premissas

A Figura 2a traz as faixas e os descontos, mas não a fórmula. O grupo adotou:

- `IR = rendimento × 27,5% × (1 − desconto da faixa)`
- Faixas com limite superior **inclusivo** (por exemplo, "até R$ 5.000" inclui 5.000,00).
- Rendimento negativo ou não numérico é **inválido** e lança `IllegalArgumentException`. Zero é válido.
- Resultado arredondado para 2 casas (`HALF_UP`), calculado com `BigDecimal`.

> **Atenção:** a fórmula é uma simplificação baseada na Figura 2a. A regra oficial (Lei nº 15.270/2025) usa a tabela progressiva mensal e um redutor decrescente entre R$ 5.000,01 e R$ 7.350,00, e isso não foi implementado, porque o enunciado define a Figura 2a como especificação.

## Técnicas aplicadas

**Classes de equivalência**

| Classe | Condição (R = rendimento) | Tipo | Comportamento esperado |
|:--:|---|---|---|
| C1 | R < 0 ou não numérico | Inválida | Lança exceção |
| C2 | 0 ≤ R ≤ 5.000,00 | Válida | Isenção total |
| C3 | 5.000,00 < R ≤ 5.500,00 | Válida | Desconto de 75% |
| C4 | 5.500,00 < R ≤ 6.000,00 | Válida | Desconto de 50% |
| C5 | 6.000,00 < R ≤ 6.500,00 | Válida | Desconto de 25% |
| C6 | 6.500,00 < R ≤ 7.350,00 | Válida | Desconto de 10% |
| C7 | R > 7.350,00 | Válida | Alíquota cheia de 27,5% |

**Valor limite:** para cada classe foram usados os limites inferior e superior, válidos e inválidos (LII, LIV, LSV e LSI), com passo de 0,01. São 21 casos de teste, de CT01 a CT21.

**GFC do método `calcularIR`:** 13 nós, 17 arestas e 5 nós de decisão (2, 4, 6, 8 e 10). A complexidade ciclomática é **V(G) = 17 − 13 + 2 = 6**, com 6 caminhos independentes:

| # | Caminho | Faixa exercitada |
|:--:|---|---|
| 1 | 1-2-3-13 | Isenção |
| 2 | 1-2-4-5-13 | Desconto de 75% |
| 3 | 1-2-4-6-7-13 | Desconto de 50% |
| 4 | 1-2-4-6-8-9-13 | Desconto de 25% |
| 5 | 1-2-4-6-8-10-11-13 | Desconto de 10% |
| 6 | 1-2-4-6-8-10-12-13 | Alíquota cheia |

## Como executar

**Requisito:** JDK 8 ou superior (`javac -version` deve funcionar). Rode os comandos na pasta do projeto.

**1. Compilar**

```
javac -encoding UTF-8 -cp junit-platform-console-standalone-1.9.1.jar ImpostoRenda.java ImpostoRendaDefeituoso.java SimularDados.java ImpostoRendaTest.java
```

**2. Rodar os testes JUnit**

```
java -jar junit-platform-console-standalone-1.9.1.jar execute -cp . --select-class ImpostoRendaTest
```

Resultado esperado: `7 tests successful` e `0 tests failed`.

**3. Rodar o simulador de dados**

```
java SimularDados
```

Resultado esperado no resumo final:

```
Parte 1 - codigo correto x item a ........ 21 PASSOU, 0 FALHOU de 21
Parte 2 - codigo correto x ficticios OK .. 7 PASSOU, 0 FALHOU de 7
Parte 3 - dados nao conformes ............ 6 de 6 divergencias DETECTADAS
Parte 4 - codigo com defeitos ............ 6 FALHOU de 21
```

## O que cada teste cobre

| Método de teste (JUnit) | Casos | Caminho do GFC |
|---|---|:--:|
| `testIsencaoTotal` | CT04, CT06 | 1 |
| `testDesconto75porcento` | CT07, CT09 | 2 |
| `testDesconto50porcento` | CT10, CT12 | 3 |
| `testDesconto25porcento` | CT13, CT15 | 4 |
| `testDesconto10porcento` | CT16, CT18 | 5 |
| `testAliquotaCheia` | CT19, CT20 | 6 |
| `testRendimentoInvalido` | CT01, CT02 | Classe C1 |

O `SimularDados` executa os **21 casos** e mostra, para cada um, a entrada, o cálculo, a saída esperada, a saída obtida e o veredito (`PASSOU` ou `FALHOU`). A saída esperada vem da especificação, e não da execução do código.

## Sobre o `ImpostoRendaDefeituoso`

É uma cópia do algoritmo com **4 defeitos injetados de propósito**, usada para mostrar que os casos de teste detectam erros:

| Defeito | Descrição |
|:--:|---|
| D1 | `validarRendimento` rejeita o zero (usa `>` em vez de `>=`) |
| D2 | Limite da isenção com `<` em vez de `<=` |
| D3 | Fator da faixa C4 igual a 0,60 em vez de 0,50 |
| D4 | Limite da faixa C6 com `<` em vez de `<=` |

Três dos quatro defeitos são de limite, e só os casos de valor limite (CT04, CT06 e CT18) os detectaram. Isso mostra por que a análise de valor limite é importante.

## Licença e uso

Projeto acadêmico, sem fins comerciais. O arquivo `junit-platform-console-standalone-1.9.1.jar` é o JUnit 5 (Eclipse Public License 2.0), incluído apenas para facilitar a execução.
