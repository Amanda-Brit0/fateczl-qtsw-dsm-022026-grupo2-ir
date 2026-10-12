import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * VERSAO COM DEFEITOS INJETADOS DE PROPOSITO.
 *
 * E uma copia do ImpostoRenda com 4 erros, usada apenas para demonstrar que os
 * casos de teste conseguem detectar um codigo errado (nao usar em producao).
 *
 *   DEFEITO 1: validarRendimento rejeita o zero (usa > 0 em vez de >= 0)
 *   DEFEITO 2: limite da isencao com '<' em vez de '<=' (5.000,00 deixa de ser isento)
 *   DEFEITO 3: fator da faixa C4 igual a 0,60 em vez de 0,50
 *   DEFEITO 4: limite da faixa C6 com '<' em vez de '<=' (7.350,00 cai na aliquota cheia)
 */
public class ImpostoRendaDefeituoso {

    private static final BigDecimal ALIQUOTA = new BigDecimal("0.275");

    public static void validarRendimento(double rendimento) {
        if (!(rendimento > 0)) { // DEFEITO 1: o correto e >= 0
            throw new IllegalArgumentException(
                "Rendimento inválido: informe um valor numérico maior ou igual a zero.");
        }
    }

    public static double calcularIR(double rendimento) {
        validarRendimento(rendimento);
        BigDecimal fator; // fator = 1 - desconto da faixa

        if (rendimento < 5000.00) {                 // DEFEITO 2: o correto e <= 5000.00
            fator = new BigDecimal("0.00");
        } else if (rendimento <= 5500.00) {
            fator = new BigDecimal("0.25");
        } else if (rendimento <= 6000.00) {
            fator = new BigDecimal("0.60");         // DEFEITO 3: o correto e 0.50
        } else if (rendimento <= 6500.00) {
            fator = new BigDecimal("0.75");
        } else if (rendimento < 7350.00) {          // DEFEITO 4: o correto e <= 7350.00
            fator = new BigDecimal("0.90");
        } else {
            fator = BigDecimal.ONE;
        }

        return BigDecimal.valueOf(rendimento)
                .multiply(ALIQUOTA)
                .multiply(fator)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
