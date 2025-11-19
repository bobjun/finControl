# Guia de Implementação - API de Movimentações

## Resumo das Alterações

Foi criada uma nova API completa de movimentações financeiras no backend, que corresponde exatamente aos endpoints esperados pelo serviço Angular `MovimentacoesService`.

## Arquivos Criados

### 1. DTOs (Data Transfer Objects)
Criados os seguintes DTOs para transferência de dados entre frontend e backend:

- **MovimentacaoDTO.java** - Representa uma movimentação individual
- **MovimentacaoResponseDTO.java** - Resposta paginada com lista de movimentações
- **ResumoFinanceiroDTO.java** - Resumo com totalReceitas, totalDespesas e saldoAtual
- **EvolucaoFinanceiraDTO.java** - Dados de evolução financeira diária
- **DespesaCategoriaDTO.java** - Despesa agrupada por categoria com percentual
- **PrevisaoFinanceiraDTO.java** - Previsão financeira do mês

**Localização:** `/src/main/java/br/com/meuGasto/finControl/dto/`

### 2. Service
- **MovimentacaoService.java** - Lógica de negócios para todas as operações

**Funcionalidades:**
- Listar movimentações com paginação
- Buscar movimentação por ID
- Criar nova movimentação
- Atualizar movimentação existente
- Excluir movimentação
- Obter resumo financeiro
- Obter evolução financeira (últimos N dias)
- Obter despesas por categoria
- Obter previsão financeira mensal
- Exportar movimentações em CSV

**Localização:** `/src/main/java/br/com/meuGasto/finControl/service/MovimentacaoService.java`

### 3. Controller
- **MovimentacaoController.java** - Endpoints REST para a API

**Endpoints implementados:**
```
GET    /api/movimentacoes                   - Listar com paginação
GET    /api/movimentacoes/{id}              - Buscar por ID
POST   /api/movimentacoes                   - Criar novo
PUT    /api/movimentacoes/{id}              - Atualizar
DELETE /api/movimentacoes/{id}              - Excluir
GET    /api/movimentacoes/resumo            - Resumo financeiro
GET    /api/movimentacoes/evolucao          - Evolução financeira
GET    /api/movimentacoes/despesas/categorias - Despesas por categoria
GET    /api/movimentacoes/previsao          - Previsão financeira
GET    /api/movimentacoes/export            - Exportar em CSV
```

**Localização:** `/src/main/java/br/com/meuGasto/finControl/controller/MovimentacaoController.java`

### 4. Documentação
- **API_MOVIMENTACOES.md** - Documentação completa da API com exemplos de uso

## Integração com o Frontend

O serviço Angular já está configurado para consumir estes endpoints. Nenhuma alteração é necessária no frontend.

### URL Base Esperada
```
/api/movimentacoes
```

### Headers Necessários
```
Authorization: Bearer {token}
Content-Type: application/json
Accept: application/json
```

## Segurança

Todos os endpoints estão protegidos pela segurança JWT configurada em `SecurityConfig.java`:

1. **Autenticação obrigatória** - JWT token deve ser fornecido
2. **CORS habilitado** - Para requisições do frontend
3. **Validação de entrada** - Usando Jakarta Validation
4. **Tratamento de erros** - Respostas HTTP apropriadas

## Banco de Dados

A API utiliza a entidade `Gasto` existente que possui os seguintes campos:

```java
- id: Long (PK)
- descricao: String
- valor: BigDecimal
- categoria: String
- dataGasto: LocalDateTime
- observacoes: String
- dataCriacao: LocalDateTime
- dataAtualizacao: LocalDateTime
```

**Nota:** Se você tiver receitas como entidade separada, será necessário ajustar os métodos de `ResumoFinanceiro`, `EvolucaoFinanceira` e `PrevisaoFinanceira` para considerar ambos (receitas e despesas).

## Como Testar

### 1. Compilar o Projeto
```bash
cd /home/robertojr/finControl
mvn clean compile
```

### 2. Executar a Aplicação
```bash
mvn spring-boot:run
```

### 3. Testar via cURL
```bash
# Listar movimentações
curl -X GET "http://localhost:8080/api/movimentacoes?page=0&size=10" \
  -H "Authorization: Bearer {seu_token}" \
  -H "Content-Type: application/json"

# Criar movimentação
curl -X POST "http://localhost:8080/api/movimentacoes" \
  -H "Authorization: Bearer {seu_token}" \
  -H "Content-Type: application/json" \
  -d '{
    "descricao": "Teste",
    "valor": 50.00,
    "categoria": "Teste",
    "data": "2025-11-14T10:00:00"
  }'
```

### 4. Testar via Postman
Importe a coleção disponível em `API_MOVIMENTACOES.md` (exemplos de cURL podem ser facilmente convertidos).

### 5. Testar via Frontend Angular
O serviço `MovimentacoesService` já está pronto para consumir os endpoints.

## Ajustes Importantes

Se você tiver uma estrutura diferente de dados ou requisitos especiais:

### Adicionar Suporte a Receitas
Modifique `MovimentacaoService.java` para considerar um campo `tipo` (receita/despesa) ou crie uma entidade separada.

### Filtros Avançados
O `GastoRepository` já possui vários métodos de busca que podem ser utilizados para adicionar filtros ao controller.

### Paginação Customizada
A paginação padrão é página 0, tamanho 10. Customize em `listarMovimentacoes()` conforme necessário.

### Cache
O `RelatorioService` já utiliza `@Cacheable`. Considere adicionar cache aos métodos de resumo/evolução se houver muitos acessos.

## Estrutura de Resposta

### Resposta com Sucesso (200, 201)
```json
{
  "id": 1,
  "descricao": "...",
  "valor": 50.00,
  ...
}
```

### Resposta com Erro (4xx, 5xx)
```json
{
  "mensagem": "Descrição do erro"
}
```

ou simplesmente uma string com a mensagem de erro.

## Próximos Passos

1. ✅ **Implementado:** Controller com todos os endpoints
2. ✅ **Implementado:** Service com lógica de negócios
3. ✅ **Implementado:** DTOs para transferência de dados
4. ✅ **Implementado:** Documentação da API
5. ⏳ **Próximo:** Testar integração com o frontend
6. ⏳ **Próximo:** Ajustar segurança se necessário (adicionar validação de usuário logado)
7. ⏳ **Próximo:** Implementar filtros avançados (por categoria, data, etc.)

## Suporte a Usuário Logado

Se você quiser filtrar movimentações por usuário logado, modifique `MovimentacaoService.java`:

```java
@Autowired
private UsuarioService usuarioService;

public MovimentacaoResponseDTO listarMovimentacoes(int page, int size) {
    String usuarioEmail = usuarioService.getEmailUsuarioLogado();
    // Adicionar filtro por usuário...
}
```

Você pode verificar em `GastoController` como isso é feito atualmente.

## Dúvidas Frequentes

**P: Como adicionar validação customizada?**
R: Use anotações Jakarta Validation nas classes DTO ou crie um `@ControllerAdvice` para tratamento global de exceções.

**P: Como filtrar por data?**
R: O `GastoRepository` já possui `findByDataGastoBetween()`. Adicione um endpoint `/search` se necessário.

**P: Como adicionar paginação aos outros endpoints?**
R: Use `PagingAndSortingRepository` e `Pageable` conforme feito em `listarMovimentacoes()`.

**P: Por que não há receitas?**
R: A entidade atual `Gasto` representa apenas despesas. Para adicionar receitas, crie uma nova entidade ou adicione um campo `tipo`.

## Contato e Suporte

Para dúvidas sobre a implementação, consulte:
- Documentação Spring Boot: https://spring.io/projects/spring-boot
- Jakarta EE: https://jakarta.ee/
- JPA Repository: https://spring.io/projects/spring-data-jpa

