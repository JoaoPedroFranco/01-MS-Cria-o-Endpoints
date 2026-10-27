package br.espm.ms.playyourlist.musicas;

import br.espm.ms.playyourlist.comum.ConflitoException;
import br.espm.ms.playyourlist.comum.NaoEncontradoException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MusicaService {

    private final MusicaRepository repository;

    public MusicaService(MusicaRepository repository) {
        this.repository = repository;
    }

    public List<Musica> listar() {
        return repository.findAll();
    }

    public Musica buscar(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new NaoEncontradoException("Musica " + id + " nao encontrada"));
    }

    @Transactional
    public Musica criar(MusicaRequest req) {
        Musica m = new Musica();
        req.copiarPara(m);
        return repository.save(m);
    }

    @Transactional
    public Musica atualizar(Integer id, MusicaRequest req) {
        Musica m = buscar(id);
        req.copiarPara(m);
        return repository.save(m);
    }

    @Transactional
    public void excluir(Integer id) {
        Musica m = buscar(id);
        try {
            repository.delete(m);
            repository.flush();
        } catch (DataIntegrityViolationException e) {
            // chave estrangeira em playlist_musicas
            throw new ConflitoException("Musica " + id
                    + " esta em uma ou mais playlists; remova-a das playlists antes de excluir");
        }
    }
}
