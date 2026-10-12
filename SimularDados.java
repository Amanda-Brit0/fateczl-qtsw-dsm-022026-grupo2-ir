import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.DoubleUnaryOperator;

/**
 * Simulador de dados de teste (complementa o JUnit, nao o substitui).
 *
 * Mostra o fluxo completo de cada caso:
 *   ENTRADA -> PROCESSAMENTO (codigo) -> SAIDA OBTIDA -> COMPARACAO com a SAIDA ESPERADA -> VEREDITO
 *
 * A saida esperada vem da ESPECIFICACAO (Figura 2a + premissa da formula), e nao da execucao
 * do codigo. Por isso ela funciona como oraculo de teste.
 *
 * Partes:
 *   1) Codigo CORRETO  x casos do item a (CT01 a CT21)            -> esperado: todos PASSOU
 *   2) Codigo CORRETO  x dados ficticios CONFORMES a especificacao -> esperado: todos PASSOU
 *   3) Codigo CORRETO  x dados ficticios NAO conformes             -> esperado: todos DETECTADO
 *   4) Codigo COM DEFEITOS x casos do item a (CT01 a CT21)        -> esperado: alguns FALHOU
 */
public class SimularDados {

    private static final Locale BR = Locale.forLanguageTag("pt-BR");
    private static final double DELTA = 0.001;
    private static final String FORMATO = "%-5s %-10s %-14s %-31s %-16s %-16s %-14s%n";

    // ------------------------------------------------------------------
    // Estruturas de dados
    // ------------------------------------------------------------------
    /** Um caso de teste: dado de entrada + saida esperada pela especificacao. */
    static class Caso {
        final String id;
        final String classe;
        final double entrada;
        final String descricao;  // memoria de calculo (ou descricao da situacao ficticia)
        final Double esperado;   // null = espera-se erro (excecao)

        Caso(String id, String classe, double entrada, String descricao, Double esperado) {
            this.id = id;
            this.classe = classe;
            this.entrada = entrada;
            this.descricao = descricao;
            this.esperado = esperado;
        }
    }

    /** Resultado da execucao de um caso. */
    static class Resultado {
        final String obtido;
        final boolean conforme; // true = saida obtida igual a esperada

        Resultado(String obtido, boolean conforme) {
            this.obtido = obtido;
            this.conforme = conforme;
        }
    }

    // ------------------------------------------------------------------
    // Formatacao (so ASCII na saida, para nao quebrar acentos no terminal)
    // ------------------------------------------------------------------
    static String moeda(double v) {
        return String.format(BR, "%,.2f", v);
    }

    static String esperadoTexto(Double e) {
        return e == null ? "ERRO (excecao)" : moeda(e);
    }

    static Caso valido(String id, String classe, double entrada, String fator, double esperado) {
        return new Caso(id, classe, entrada, moeda(entrada) + " x 0,275 x " + fator, esperado);
    }

    static Caso invalido(String id, String classe, double entrada) {
        return new Caso(id, classe, entrada, "entrada invalida", null);
    }

    // ------------------------------------------------------------------
    // Execucao e comparacao
    // ------------------------------------------------------------------
    static Resultado executar(DoubleUnaryOperator codigo, Caso c) {
        try {
            double obtido = codigo.applyAsDouble(c.entrada);          // PROCESSAMENTO
            boolean conforme = c.esperado != null && Math.abs(obtido - c.esperado) < DELTA;
            return new Resultado(moeda(obtido), conforme);            // SAIDA OBTIDA
        } catch (IllegalArgumentException e) {
            return new Resultado("ERRO (excecao)", c.esperado == null);
        }
    }

    /** Roda os casos, imprime a tabela e devolve os ids dos casos com divergencia. */
    static List<String> rodar(List<Caso> casos, DoubleUnaryOperator codigo, boolean controleNegativo) {
        List<String> divergencias = new ArrayList<String>();
        System.out.printf(FORMATO, "Caso", "Classe", "Entrada (R)", "Calculo esperado", "Esperado", "Obtido", "Resultado");
        System.out.println(repetir("-", 112));
        for (Caso c : casos) {
            Resultado r = executar(codigo, c);
            String veredito;
            if (controleNegativo) {
                veredito = r.conforme ? "NAO DETECTADO" : "DETECTADO";
            } else {
                veredito = r.conforme ? "PASSOU" : "FALHOU";
            }
            if (!r.conforme) {
                divergencias.add(c.id);
            }
            System.out.printf(FORMATO, c.id, c.classe, moeda(c.entrada), c.descricao,
                    esperadoTexto(c.esperado), r.obtido, veredito);
        }
        System.out.println();
        return divergencias;
    }

    static String repetir(String s, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append(s);
        }
        return sb.toString();
    }

    static void titulo(String texto) {
        System.out.println(repetir("=", 112));
        System.out.println(texto);
        System.out.println(repetir("=", 112));
    }

    // ------------------------------------------------------------------
    // Massas de dados
    // ------------------------------------------------------------------
    /** Casos CT01 a CT21 do item a (iguais a tabela do trabalho). */
    static List<Caso> casosDoItemA() {
        List<Caso> l = new ArrayList<Caso>();
        l.add(invalido("CT01", "C1", -1000.00));
        l.add(invalido("CT02", "C1 (LSI)", -0.01));
        l.add(invalido("CT03", "C1", Double.NaN));
        l.add(valido("CT04", "C2 (LIV)", 0.00, "0,00", 0.00));
        l.add(valido("CT05", "C2", 2500.00, "0,00", 0.00));
        l.add(valido("CT06", "C2 (LSV)", 5000.00, "0,00", 0.00));
        l.add(valido("CT07", "C3 (LIV)", 5000.01, "0,25", 343.75));
        l.add(valido("CT08", "C3", 5400.00, "0,25", 371.25));
        l.add(valido("CT09", "C3 (LSV)", 5500.00, "0,25", 378.13));
        l.add(valido("CT10", "C4 (LIV)", 5500.01, "0,50", 756.25));
        l.add(valido("CT11", "C4", 5900.00, "0,50", 811.25));
        l.add(valido("CT12", "C4 (LSV)", 6000.00, "0,50", 825.00));
        l.add(valido("CT13", "C5 (LIV)", 6000.01, "0,75", 1237.50));
        l.add(valido("CT14", "C5", 6400.00, "0,75", 1320.00));
        l.add(valido("CT15", "C5 (LSV)", 6500.00, "0,75", 1340.63));
        l.add(valido("CT16", "C6 (LIV)", 6500.01, "0,90", 1608.75));
        l.add(valido("CT17", "C6", 7200.00, "0,90", 1782.00));
        l.add(valido("CT18", "C6 (LSV)", 7350.00, "0,90", 1819.13));
        l.add(valido("CT19", "C7 (LIV)", 7350.01, "1,00", 2021.25));
        l.add(valido("CT20", "C7", 10000.00, "1,00", 2750.00));
        l.add(valido("CT21", "C7", 1000000.00, "1,00", 275000.00));
        return l;
    }

    /** Dados ficticios NOVOS, com esperado calculado a mao pela formula (conformes). */
    static List<Caso> dadosFicticiosConformes() {
        List<Caso> l = new ArrayList<Caso>();
        l.add(invalido("F01", "C1", -250.50));
        l.add(valido("F02", "C2", 3200.00, "0,00", 0.00));
        l.add(valido("F03", "C3", 5250.00, "0,25", 360.94));
        l.add(valido("F04", "C4", 5750.00, "0,50", 790.63));
        l.add(valido("F05", "C5", 6250.00, "0,75", 1289.06));
        l.add(valido("F06", "C6", 6900.00, "0,90", 1707.75));
        l.add(valido("F07", "C7", 9000.00, "1,00", 2475.00));
        return l;
    }

    /** Dados ficticios com esperado ERRADO de proposito (nao conformes a especificacao). */
    static List<Caso> dadosFicticiosNaoConformes() {
        List<Caso> l = new ArrayList<Caso>();
        l.add(new Caso("N01", "C3 (LIV)", 5000.01, "5.000,01 tratado como isento", 0.00));
        l.add(new Caso("N02", "C3 (LSV)", 5500.00, "5.500,00 tratado como C4", 756.25));
        l.add(new Caso("N03", "C1 (LSI)", -0.01, "negativo com saida numerica", 0.00));
        l.add(new Caso("N04", "C2 (LIV)", 0.00, "zero tratado como invalido", null));
        l.add(new Caso("N05", "C7", 8000.00, "erro de 1 centavo no IR", 2200.01));
        l.add(new Caso("N06", "C6 (LSV)", 7350.00, "7.350,00 tratado como C7", 2021.25));
        return l;
    }

    // ------------------------------------------------------------------
    // Programa principal
    // ------------------------------------------------------------------
    public static void main(String[] args) {
        System.out.println("SIMULADOR DE DADOS - Calculo do IR (ano-base 2026)");
        System.out.println("Fluxo de cada caso: ENTRADA -> PROCESSAMENTO -> SAIDA OBTIDA -> COMPARACAO COM O ESPERADO -> VEREDITO");
        System.out.println("Formula adotada: IR = R x 27,5% x fator   (fator = 1 - desconto da faixa)");
        System.out.println();

        titulo("PARTE 1 - Codigo CORRETO (ImpostoRenda) x casos do item a (CT01 a CT21)");
        List<Caso> itemA = casosDoItemA();
        List<String> div1 = rodar(itemA, ImpostoRenda::calcularIR, false);

        titulo("PARTE 2 - Codigo CORRETO (ImpostoRenda) x dados ficticios CONFORMES a especificacao");
        List<Caso> conformes = dadosFicticiosConformes();
        List<String> div2 = rodar(conformes, ImpostoRenda::calcularIR, false);

        titulo("PARTE 3 - Codigo CORRETO x dados ficticios NAO conformes (esperado errado de proposito)");
        System.out.println("Objetivo: provar que o teste reconhece quando o dado esperado nao bate com a especificacao.");
        System.out.println();
        List<Caso> naoConformes = dadosFicticiosNaoConformes();
        List<String> div3 = rodar(naoConformes, ImpostoRenda::calcularIR, true);

        titulo("PARTE 4 - Codigo COM DEFEITOS (ImpostoRendaDefeituoso) x casos do item a (CT01 a CT21)");
        System.out.println("Defeitos injetados de proposito:");
        System.out.println("  D1: validarRendimento rejeita o zero (usa > 0 em vez de >= 0)");
        System.out.println("  D2: limite da isencao com '<' em vez de '<=' (5.000,00 deixa de ser isento)");
        System.out.println("  D3: fator da faixa C4 igual a 0,60 em vez de 0,50");
        System.out.println("  D4: limite da faixa C6 com '<' em vez de '<=' (7.350,00 cai na aliquota cheia)");
        System.out.println();
        List<String> div4 = rodar(itemA, ImpostoRendaDefeituoso::calcularIR, false);

        titulo("RESUMO");
        System.out.println("Parte 1 - codigo correto x item a ........ " + (itemA.size() - div1.size()) + " PASSOU, "
                + div1.size() + " FALHOU de " + itemA.size());
        System.out.println("Parte 2 - codigo correto x ficticios OK .. " + (conformes.size() - div2.size()) + " PASSOU, "
                + div2.size() + " FALHOU de " + conformes.size());
        System.out.println("Parte 3 - dados nao conformes ............ " + div3.size() + " de " + naoConformes.size()
                + " divergencias DETECTADAS");
        System.out.println("Parte 4 - codigo com defeitos ............ " + div4.size() + " FALHOU de " + itemA.size()
                + "  -> casos que detectaram os defeitos: " + div4);
        System.out.println();

        boolean tudoEsperado = div1.isEmpty() && div2.isEmpty()
                && div3.size() == naoConformes.size() && !div4.isEmpty();
        System.out.println(tudoEsperado
                ? "CONCLUSAO: o codigo correto passou em tudo, e os testes detectaram os dados errados e os defeitos injetados."
                : "CONCLUSAO: comportamento inesperado - revisar os resultados acima.");
    }
}
