import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Cálculo do IR anual conforme a Tabela do IR - Ano-base 2026 (Figura 2a).
 */
public class ImpostoRenda {

    private static final BigDecimal ALIQUOTA = new BigDecimal("0.275");

    /** Rejeita rendimento negativo ou não numérico (NaN). */
    public static void validarRendimento(double rendimento) {
        if (!(rendimento >= 0)) { // falso para negativos e para NaN
            throw new IllegalArgumentException(
                "Rendimento inválido: informe um valor numérico maior ou igual a zero.");
        }
    }

    public static double calcularIR(double rendimento) {
        validarRendimento(rendimento);
        BigDecimal fator; // fator = 1 - desconto da faixa

        if (rendimento <= 5000.00) {
            fator = new BigDecimal("0.00"); // isenção total
        } else if (rendimento <= 5500.00) {
            fator = new BigDecimal("0.25"); // desconto de 75%
        } else if (rendimento <= 6000.00) {
            fator = new BigDecimal("0.50"); // desconto de 50%
        } else if (rendimento <= 6500.00) {
            fator = new BigDecimal("0.75"); // desconto de 25%
        } else if (rendimento <= 7350.00) {
            fator = new BigDecimal("0.90"); // desconto de 10%
        } else {
            fator = BigDecimal.ONE;         // alíquota cheia
        }

        return BigDecimal.valueOf(rendimento)
                .multiply(ALIQUOTA)
                .multiply(fator)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
