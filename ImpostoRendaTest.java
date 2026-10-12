import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class ImpostoRendaTest {

    @Test
    void testIsencaoTotal() {
        // CT04 e CT06: Limites da faixa de isenção
        assertEquals(0.00, ImpostoRenda.calcularIR(0.00));
        assertEquals(0.00, ImpostoRenda.calcularIR(5000.00));
    }

    @Test
    void testDesconto75porcento() {
        // CT07 e CT09: Faixa até 5.500
        assertEquals(343.75, ImpostoRenda.calcularIR(5000.01));
        assertEquals(378.13, ImpostoRenda.calcularIR(5500.00));
    }

    @Test
    void testDesconto50porcento() {
        // CT10 e CT12: Faixa até 6.000
        assertEquals(756.25, ImpostoRenda.calcularIR(5500.01));
        assertEquals(825.00, ImpostoRenda.calcularIR(6000.00));
    }

    @Test
    void testDesconto25porcento() {
        // CT13 e CT15: Faixa até 6.500
        assertEquals(1237.50, ImpostoRenda.calcularIR(6000.01));
        assertEquals(1340.63, ImpostoRenda.calcularIR(6500.00));
    }

    @Test
    void testDesconto10porcento() {
        // CT16 e CT18: Faixa até 7.350
        assertEquals(1608.75, ImpostoRenda.calcularIR(6500.01));
        assertEquals(1819.13, ImpostoRenda.calcularIR(7350.00));
    }

    @Test
    void testAliquotaCheia() {
        // CT19 e CT20: Acima de 7.350
        assertEquals(2021.25, ImpostoRenda.calcularIR(7350.01));
        assertEquals(2750.00, ImpostoRenda.calcularIR(10000.00));
    }

    @Test
    void testRendimentoInvalido() {
        // CT01 e CT02: Valores negativos ou inválidos lançam exceção
        assertThrows(IllegalArgumentException.class, () -> {
            ImpostoRenda.calcularIR(-1000.00);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            ImpostoRenda.calcularIR(-0.01);
        });
    }
}
