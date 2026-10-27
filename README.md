# MS-AV-01 – Endpoints (Play Your List)

App de montagem de playlists para empresas que adotam o **HPWM (High Productivity With Music)**.
A solução é dividida em 4 microsserviços que, para facilitar, estão no **mesmo projeto Spring Boot**, cada um em seu próprio pacote:

| Microsserviço | Pacote | Responsabilidade |
|---|---|---|
| `musicas` | `br.espm.ms.playyourlist.musicas` | CRUD das músicas disponíveis |
| `playlists` | `br.espm.ms.playyourlist.playlists` | manutenção das playlists e de suas músicas |
| `reproducoes` | `br.espm.ms.playyourlist.reproducoes` | quantas vezes cada playlist foi executada |
| `api` | `br.espm.ms.playyourlist.api` | orquestra os outros serviços via **Open Feign** |

**Tecnologias:** Java 17, Spring Boot 3.4, Spring Data JPA, Bean Validation, Spring Cloud OpenFeign, H2 e springdoc (Swagger).

## Como executar

```bash
./mvnw spring-boot:run          # Windows: mvnw.cmd spring-boot:run
```
Ou abra o projeto na IDE e rode `PlayYourListApplication`.

- API: http://localhost:8080
- Swagger UI (para testar todos os endpoints): http://localhost:8080/swagger-ui.html
- Console do H2: http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:playyourlist`, usuário `sa`, sem senha)

O banco é criado e populado pelo `src/main/resources/data.sql` com os scripts do enunciado.

### Testes
```bash
./mvnw test
```
São 9 testes de integração que sobem a aplicação numa porta real, então as chamadas Open Feign do serviço `api` acontecem de verdade.

## Endpoints

### musicas
| Método | Endpoint | Descrição | Respostas |
|---|---|---|---|
| `POST` | `/musicas` | cadastrar uma música | `201` · `400` |
| `GET` | `/musicas` | listar todas | `200` |
| `GET` | `/musicas/{id}` | buscar pelo id | `200` · `404` |
| `PUT` | `/musicas/{id}` | atualizar | `200` · `400` · `404` |
| `DELETE` | `/musicas/{id}` | excluir | `204` · `404` · `409` se estiver em alguma playlist |

### playlists
| Método | Endpoint | Descrição | Respostas |
|---|---|---|---|
| `POST` | `/playlists` | criar | `201` · `400` |
| `GET` | `/playlists` | listar todas | `200` |
| `GET` | `/playlists/{playlistid}` | buscar | `200` · `404` |
| `PUT` | `/playlists/{playlistid}` | atualizar nome e descrição | `200` · `400` · `404` |
| `DELETE` | `/playlists/{playlistid}` | excluir a playlist e suas músicas associadas | `204` · `404` |
| `POST` | `/playlists/{playlistid}/musicas/{musicaId}` | adicionar música | `201` · `404` · `409` já está na playlist |
| `DELETE` | `/playlists/{playlistid}/musicas/{musicaId}` | remover música | `204` · `404` |
| `GET` | `/playlists/{playlistid}/musicas` | ids das músicas, ex.: `[1,3,5]` | `200` · `404` |

### reproducoes
| Método | Endpoint | Descrição | Respostas |
|---|---|---|---|
| `POST` | `/reproducao` | registra uma execução; corpo `{"playlistId": 1}`, `datahora` = `LocalDateTime.now()` | `201` · `400` |
| `GET` | `/reproducao/{playlistid}` | lista as reproduções da playlist | `200` |
| `GET` | `/reproducao/total/{playlistid}` | total de execuções, ex.: `{"playlistId":1,"total":5}` | `200` |

### api (orquestrador com Open Feign)
| Método | Endpoint | O que faz |
|---|---|---|
| `POST` | `/api/adicionar/{playlistId}/musicas/{musicaId}` | valida a música (`GET /musicas/{id}`) e a playlist (`GET /playlists/{id}`) via Feign; se ambas existirem, chama `POST /playlists/{id}/musicas/{musicaId}` e retorna `Música <título> adicionada com sucesso à playlist <nome>`. Retorna `404` se uma das duas não existir e `409` se a música já estiver na playlist |
| `PUT` | `/api/executar/{playlistId}` | valida a playlist e registra a execução em `POST /reproducao` via Feign; devolve a mensagem, a data/hora e o total de execuções |

Os clientes Feign ficam em `api/client` e apontam para a propriedade `app.servicos.url` do `application.yml`. Como tudo está no mesmo projeto, ela aponta para a própria aplicação; com os serviços separados, bastaria trocar a URL.

## Regras de validação

| Recurso | Campo | Regra |
|---|---|---|
| Música | `titulo` | obrigatório, não pode ser vazio nem só espaços |
| Música | `artista` | obrigatório, não pode ser vazio nem só espaços |
| Música | `album` | opcional, máximo 150 caracteres |
| Música | `duracao` | obrigatória, maior que zero |
| Música | `genero` | opcional, máximo 50 caracteres |
| Playlist | `nome` | obrigatório, não pode ser vazio nem só espaços |
| Playlist | `descricao` | opcional, máximo 255 caracteres |

Erros de validação retornam `400` indicando cada campo:
```json
{"mensagem":"Dados invalidos","erros":{"titulo":"titulo e obrigatorio e nao pode ser vazio","duracao":"duracao deve ser maior que zero"}}
```

## Exemplos (respostas reais)

```bash
curl -X POST localhost:8080/musicas -H "Content-Type: application/json" \
  -d '{"titulo":"Hotel California","artista":"Eagles","album":"Hotel California","duracao":391,"genero":"Rock"}'
# 201 {"id":6,"titulo":"Hotel California","artista":"Eagles","album":"Hotel California","duracao":391,"genero":"Rock"}

curl localhost:8080/playlists/1/musicas
# 200 [1,3,5]

curl -X POST localhost:8080/api/adicionar/2/musicas/6
# 200 {"mensagem":"Música Hotel California adicionada com sucesso à playlist Música Brasileira"}

curl -X POST localhost:8080/api/adicionar/2/musicas/999
# 404 {"mensagem":"Musica 999 nao encontrada"}

curl -X PUT localhost:8080/api/executar/1
# 200 {"mensagem":"Playlist Clássicos do Rock executada com sucesso","playlistId":1,"dataHora":"2026-10-27T13:30:19.74","totalExecucoes":6}
```

## Observações
- O enunciado cita `POST /statistic` no `PUT /api/executar`; como esse endpoint não existe na especificação, a execução é registrada no endpoint equivalente do serviço de reproduções, `POST /reproducao`.
- Os scripts do enunciado foram reunidos em um único `data.sql`. Os `DROP TABLE` foram agrupados no início, na ordem correta (primeiro `playlist_musicas`, que tem as chaves estrangeiras), para o script não falhar se as tabelas já existirem.
- Excluir uma música que está em alguma playlist retorna `409`: o serviço de músicas não mexe nas tabelas do serviço de playlists. Remova a música das playlists antes.
- O banco H2 é em memória: a cada reinício os dados voltam à carga inicial do `data.sql`.
