package br.espm.ms.playyourlist;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Sobe a aplicacao em uma porta real (DEFINED_PORT) para que os clientes
 * Open Feign do servico "api" chamem os outros servicos de verdade.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = "server.port=18080")
@AutoConfigureMockMvc
class PlayYourListTest {

    private static final String HOTEL_CALIFORNIA = """
            {"titulo":"Hotel California","artista":"Eagles","album":"Hotel California","duracao":391,"genero":"Rock"}
            """;

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    private ResultActions postJson(String url, String body) throws Exception {
        return mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(body));
    }

    private ResultActions putJson(String url, String body) throws Exception {
        return mvc.perform(put(url).contentType(MediaType.APPLICATION_JSON).content(body));
    }

    private int criarId(String url, String body) throws Exception {
        String resp = postJson(url, body).andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return json.readTree(resp).get("id").asInt();
    }

    // ------------------------------- musicas -------------------------------

    @Test
    void crudDeMusicas() throws Exception {
        mvc.perform(get("/musicas")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(5)));
        mvc.perform(get("/musicas/3")).andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Bohemian Rhapsody"));

        int id = criarId("/musicas", HOTEL_CALIFORNIA);
        mvc.perform(get("/musicas/" + id)).andExpect(jsonPath("$.artista").value("Eagles"));

        putJson("/musicas/" + id, """
                {"titulo":"Hotel California (Live)","artista":"Eagles","duracao":430}
                """).andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Hotel California (Live)"))
                .andExpect(jsonPath("$.duracao").value(430));

        mvc.perform(delete("/musicas/" + id)).andExpect(status().isNoContent());
        mvc.perform(get("/musicas/" + id)).andExpect(status().isNotFound());
        mvc.perform(delete("/musicas/" + id)).andExpect(status().isNotFound());
        putJson("/musicas/99999", HOTEL_CALIFORNIA).andExpect(status().isNotFound());
    }

    @Test
    void validacoesDeMusica() throws Exception {
        postJson("/musicas", """
                {"titulo":"   ","artista":"","duracao":0,"album":"%s","genero":"%s"}
                """.formatted("a".repeat(151), "g".repeat(51)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.titulo").exists())
                .andExpect(jsonPath("$.erros.artista").exists())
                .andExpect(jsonPath("$.erros.duracao").exists())
                .andExpect(jsonPath("$.erros.album").exists())
                .andExpect(jsonPath("$.erros.genero").exists());
        postJson("/musicas", """
                {"titulo":"X","artista":"Y"}
                """).andExpect(status().isBadRequest()).andExpect(jsonPath("$.erros.duracao").exists());
        // album e genero sao opcionais
        postJson("/musicas", """
                {"titulo":"Sem album","artista":"Alguem","duracao":10}
                """).andExpect(status().isCreated());
    }

    @Test
    void naoExcluiMusicaQueEstaEmPlaylist() throws Exception {
        mvc.perform(delete("/musicas/1")).andExpect(status().isConflict());
    }

    // ------------------------------- playlists -------------------------------

    @Test
    void crudDePlaylists() throws Exception {
        mvc.perform(get("/playlists")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(5)));
        mvc.perform(get("/playlists/2")).andExpect(jsonPath("$.nome").value("Música Brasileira"));

        int id = criarId("/playlists", """
                {"nome":"Foco Total","descricao":"Para trabalhar"}
                """);
        putJson("/playlists/" + id, """
                {"nome":"Foco Total HPWM","descricao":"High Productivity With Music"}
                """).andExpect(status().isOk()).andExpect(jsonPath("$.nome").value("Foco Total HPWM"));

        postJson("/playlists", """
                {"nome":"  ","descricao":"%s"}
                """.formatted("d".repeat(256)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.nome").exists())
                .andExpect(jsonPath("$.erros.descricao").exists());
        mvc.perform(get("/playlists/99999")).andExpect(status().isNotFound());
    }

    @Test
    void musicasDaPlaylist() throws Exception {
        mvc.perform(get("/playlists/1/musicas")).andExpect(status().isOk())
                .andExpect(jsonPath("$", contains(1, 3, 5)));

        int id = criarId("/playlists", "{\"nome\":\"Teste musicas\"}");
        mvc.perform(post("/playlists/" + id + "/musicas/2")).andExpect(status().isCreated());
        mvc.perform(post("/playlists/" + id + "/musicas/4")).andExpect(status().isCreated());
        mvc.perform(post("/playlists/" + id + "/musicas/2")).andExpect(status().isConflict());
        mvc.perform(post("/playlists/" + id + "/musicas/99999")).andExpect(status().isNotFound());
        mvc.perform(post("/playlists/99999/musicas/1")).andExpect(status().isNotFound());
        mvc.perform(get("/playlists/" + id + "/musicas")).andExpect(jsonPath("$", contains(2, 4)));

        mvc.perform(delete("/playlists/" + id + "/musicas/2")).andExpect(status().isNoContent());
        mvc.perform(delete("/playlists/" + id + "/musicas/2")).andExpect(status().isNotFound());
        mvc.perform(get("/playlists/" + id + "/musicas")).andExpect(jsonPath("$", contains(4)));
    }

    @Test
    void excluirPlaylistRemoveAssociacoes() throws Exception {
        int id = criarId("/playlists", "{\"nome\":\"Para excluir\"}");
        mvc.perform(post("/playlists/" + id + "/musicas/1")).andExpect(status().isCreated());
        mvc.perform(delete("/playlists/" + id)).andExpect(status().isNoContent());
        mvc.perform(get("/playlists/" + id)).andExpect(status().isNotFound());
        mvc.perform(get("/playlists")).andExpect(jsonPath("$[*].id", not(hasItem(id))));
    }

    // ------------------------------- reproducoes -------------------------------

    @Test
    void reproducoes() throws Exception {
        mvc.perform(get("/reproducao/3")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(2)));
        long antes = total(3);
        postJson("/reproducao", "{\"playlistId\":3}").andExpect(status().isCreated())
                .andExpect(jsonPath("$.dataHora").exists());
        org.junit.jupiter.api.Assertions.assertEquals(antes + 1, total(3));
        postJson("/reproducao", "{}").andExpect(status().isBadRequest());
    }

    private long total(int playlistId) throws Exception {
        String resp = mvc.perform(get("/reproducao/total/" + playlistId)).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode n = json.readTree(resp);
        return n.get("total").asLong();
    }

    // ------------------------------- api (Open Feign) -------------------------------

    @Test
    void apiAdicionarValidaMusicaEPlaylist() throws Exception {
        int playlist = criarId("/playlists", "{\"nome\":\"Orquestrada\"}");
        mvc.perform(post("/api/adicionar/" + playlist + "/musicas/2")).andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem")
                        .value("Música Billie Jean adicionada com sucesso à playlist Orquestrada"));
        mvc.perform(get("/playlists/" + playlist + "/musicas")).andExpect(jsonPath("$", contains(2)));

        mvc.perform(post("/api/adicionar/" + playlist + "/musicas/2")).andExpect(status().isConflict());
        mvc.perform(post("/api/adicionar/" + playlist + "/musicas/99999")).andExpect(status().isNotFound());
        mvc.perform(post("/api/adicionar/99999/musicas/1")).andExpect(status().isNotFound());
    }

    @Test
    void apiExecutarRegistraReproducao() throws Exception {
        int playlist = criarId("/playlists", "{\"nome\":\"Executada\"}");
        mvc.perform(put("/api/executar/" + playlist)).andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").value("Playlist Executada executada com sucesso"))
                .andExpect(jsonPath("$.totalExecucoes").value(1));
        mvc.perform(put("/api/executar/" + playlist)).andExpect(jsonPath("$.totalExecucoes").value(2));
        mvc.perform(get("/reproducao/" + playlist)).andExpect(jsonPath("$.length()").value(2));
        mvc.perform(put("/api/executar/99999")).andExpect(status().isNotFound());
    }
}
