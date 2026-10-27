package br.espm.ms.playyourlist.comum;

/** Operacao que conflita com o estado atual -> HTTP 409. */
public class ConflitoException extends RuntimeException {
    public ConflitoException(String mensagem) {
        super(mensagem);
    }
}
