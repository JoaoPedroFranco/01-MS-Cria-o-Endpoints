package br.espm.ms.playyourlist.comum;

/** Recurso inexistente -> HTTP 404. */
public class NaoEncontradoException extends RuntimeException {
    public NaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
