# 🚀 REFERÊNCIA RÁPIDA - API de Movimentações

## 📍 Links Importantes

| Documento | Link | Propósito |
|-----------|------|----------|
| **Começar Aqui** | [QUICKSTART.md](QUICKSTART.md) | Guia de 5 minutos |
| **Referência API** | [API_MOVIMENTACOES.md](API_MOVIMENTACOES.md) | Documentação técnica |
| **Exemplo Real** | [EXEMPLO_FLUXO_COMPLETO.md](EXEMPLO_FLUXO_COMPLETO.md) | Fluxo passo a passo |
| **Implementação** | [IMPLEMENTACAO_MOVIMENTACOES.md](IMPLEMENTACAO_MOVIMENTACOES.md) | Detalhes técnicos |
| **Ajustes** | [AJUSTES_FUTUROS.md](AJUSTES_FUTUROS.md) | Melhorias futuras |
| **Índice** | [INDEX.md](INDEX.md) | Índice completo |

---

## 📝 Endpoints (Quick Reference)

### CRUD
```bash
GET    /api/movimentacoes              # Listar com paginação
GET    /api/movimentacoes/{id}         # Buscar por ID
POST   /api/movimentacoes              # Criar novo
PUT    /api/movimentacoes/{id}         # Atualizar
DELETE /api/movimentacoes/{id}         # Excluir
```

### Relatórios
```bash
GET    /api/movimentacoes/resumo              # Resumo financeiro
GET    /api/movimentacoes/evolucao            # Evolução diária
GET    /api/movimentacoes/despesas/categorias # Despesas/categoria
GET    /api/movimentacoes/previsao            # Previsão mensal
GET    /api/movimentacoes/export              # Exportar CSV
```

---

## 🧪 Testes Rápidos

### Script Bash
```bash
./test-movimentacoes.sh "seu_token_jwt"
```

### Postman
1. Importe: `postman-movimentacoes.json`
2. Configure: variável `token`
3. Execute: requisições

### cURL
```bash
curl -X GET "http://localhost:8080/api/movimentacoes" \
  -H "Authorization: Bearer {token}"
```

---

## 💻 Comandos Essenciais

### Compilar
```bash
mvn clean compile
```

### Executar
```bash
mvn spring-boot:run
```

### Testar
```bash
./test-movimentacoes.sh "seu_token"
```

---

## 📋 DTOs

### MovimentacaoDTO
```json
{
  "id": 1,
  "descricao": "string",
  "valor": 0.00,
  "categoria": "string",
  "data": "2025-11-14T12:30:00",
  "observacoes": "string",
  "dataCriacao": "2025-11-14T12:30:00",
  "dataAtualizacao": "2025-11-14T12:30:00"
}
```

### ResumoFinanceiroDTO
```json
{
  "totalReceitas": 0.00,
  "totalDespesas": 0.00,
  "saldoAtual": 0.00
}
```

### EvolucaoFinanceiraDTO (Array)
```json
[
  {
    "data": "2025-11-14",
    "receitas": 0.00,
    "despesas": 0.00,
    "saldo": 0.00
  }
]
```

### DespesaCategoriaDTO (Array)
```json
[
  {
    "categoria": "string",
    "total": 0.00,
    "percentual": 0.00
  }
]
```

### PrevisaoFinanceiraDTO
```json
{
  "receitaPrevista": 0.00,
  "despesaPrevista": 0.00,
  "saldoPrevisto": 0.00,
  "periodo": "2025-11"
}
```

---

## 🔑 Headers Necessários

```
Authorization: Bearer {token}
Content-Type: application/json
Accept: application/json
```

---

## 🎯 Exemplo GET (Listar)

### Request
```bash
GET /api/movimentacoes?page=0&size=10
Authorization: Bearer {token}
```

### Response (200 OK)
```json
{
  "content": [
    {
      "id": 1,
      "descricao": "Almoço",
      "valor": 45.50,
      "categoria": "Alimentação",
      "data": "2025-11-14T12:30:00",
      "observacoes": "Restaurante",
      "dataCriacao": "2025-11-14T12:30:00",
      "dataAtualizacao": "2025-11-14T12:30:00"
    }
  ],
  "pageNumber": 0,
  "pageSize": 10,
  "totalElements": 150,
  "totalPages": 15,
  "last": false
}
```

---

## 🎯 Exemplo POST (Criar)

### Request
```bash
POST /api/movimentacoes
Authorization: Bearer {token}
Content-Type: application/json

{
  "descricao": "Almoço",
  "valor": 45.50,
  "categoria": "Alimentação",
  "data": "2025-11-14T12:30:00",
  "observacoes": "Restaurante XYZ"
}
```

### Response (201 Created)
```json
{
  "id": 1,
  "descricao": "Almoço",
  "valor": 45.50,
  "categoria": "Alimentação",
  "data": "2025-11-14T12:30:00",
  "observacoes": "Restaurante XYZ",
  "dataCriacao": "2025-11-14T12:30:00",
  "dataAtualizacao": "2025-11-14T12:30:00"
}
```

---

## 🎯 Exemplo GET Resumo

### Request
```bash
GET /api/movimentacoes/resumo?dias=30
Authorization: Bearer {token}
```

### Response (200 OK)
```json
{
  "totalReceitas": 0.00,
  "totalDespesas": 1250.75,
  "saldoAtual": -1250.75
}
```

---

## 📊 Códigos HTTP

| Código | Significado | Quando |
|--------|-------------|---------|
| 200 | OK | Sucesso em GET/PUT |
| 201 | Created | Sucesso em POST |
| 204 | No Content | Sucesso em DELETE |
| 400 | Bad Request | Dados inválidos |
| 401 | Unauthorized | Token ausente/inválido |
| 403 | Forbidden | Sem permissão |
| 404 | Not Found | Recurso inexistente |
| 500 | Server Error | Erro do servidor |

---

## ⚙️ Parâmetros Comuns

### Paginação
```
?page=0&size=10  # Página 0, tamanho 10
?page=1&size=20  # Página 1, tamanho 20
```

### Relatórios
```
?dias=30   # Últimos 30 dias
?dias=60   # Últimos 60 dias
?dias=90   # Últimos 90 dias
```

---

## 🔍 Troubleshooting

| Erro | Causa | Solução |
|------|-------|---------|
| 401 Unauthorized | Token inválido | Obtenha novo token |
| 404 Not Found | Endpoint errado | Verifique URL |
| 500 Server Error | Erro no backend | Verifique logs |
| CORS Error | Origem bloqueada | Já habilitado |

---

## 📚 Documentação Completa

**Leia primeiro:** [QUICKSTART.md](QUICKSTART.md)

**Depois consulte:**
1. [API_MOVIMENTACOES.md](API_MOVIMENTACOES.md) - Referência técnica
2. [EXEMPLO_FLUXO_COMPLETO.md](EXEMPLO_FLUXO_COMPLETO.md) - Exemplos
3. [IMPLEMENTACAO_MOVIMENTACOES.md](IMPLEMENTACAO_MOVIMENTACOES.md) - Customização

---

## 📁 Arquivos Principais

```
/home/robertojr/finControl/
├── src/main/java/.../controller/MovimentacaoController.java
├── src/main/java/.../service/MovimentacaoService.java
├── src/main/java/.../dto/
│   ├── MovimentacaoDTO.java
│   ├── MovimentacaoResponseDTO.java
│   ├── ResumoFinanceiroDTO.java
│   ├── EvolucaoFinanceiraDTO.java
│   ├── DespesaCategoriaDTO.java
│   └── PrevisaoFinanceiraDTO.java
├── QUICKSTART.md
├── API_MOVIMENTACOES.md
├── test-movimentacoes.sh
└── postman-movimentacoes.json
```

---

## 🚀 Começar em 3 Passos

1. **Compile:**
   ```bash
   mvn clean compile
   ```

2. **Execute:**
   ```bash
   mvn spring-boot:run
   ```

3. **Teste:**
   ```bash
   ./test-movimentacoes.sh "seu_token"
   ```

**Pronto!** 🎉

---

**Referência Rápida - v1.0**  
**Última atualização:** 14 de Novembro de 2025

