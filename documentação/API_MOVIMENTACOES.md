# API de Movimentações - Documentação

Este documento descreve todos os endpoints da API de movimentações financeiras disponíveis no backend.

## Base URL
```
/api/movimentacoes
```

## Autenticação
Todos os endpoints requerem autenticação via JWT Token. O token deve ser enviado no header:
```
Authorization: Bearer {token}
```

---

## Endpoints

### 1. Listar Movimentações (com paginação)
**GET** `/api/movimentacoes`

**Parâmetros Query:**
- `page` (optional, default: 0) - Número da página
- `size` (optional, default: 10) - Tamanho da página

**Response (200 OK):**
```json
{
  "content": [
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
  ],
  "pageNumber": 0,
  "pageSize": 10,
  "totalElements": 150,
  "totalPages": 15,
  "last": false
}
```

---

### 2. Buscar Movimentação por ID
**GET** `/api/movimentacoes/{id}`

**Parâmetros Path:**
- `id` (required) - ID da movimentação

**Response (200 OK):**
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

**Response (404 Not Found):**
```
Movimentação não encontrada
```

---

### 3. Criar Nova Movimentação
**POST** `/api/movimentacoes`

**Request Body:**
```json
{
  "descricao": "Almoço",
  "valor": 45.50,
  "categoria": "Alimentação",
  "data": "2025-11-14T12:30:00",
  "observacoes": "Restaurante XYZ"
}
```

**Response (201 Created):**
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

### 4. Atualizar Movimentação
**PUT** `/api/movimentacoes/{id}`

**Parâmetros Path:**
- `id` (required) - ID da movimentação

**Request Body:**
```json
{
  "descricao": "Almoço (atualizado)",
  "valor": 50.00,
  "categoria": "Alimentação",
  "data": "2025-11-14T12:30:00",
  "observacoes": "Restaurante XYZ - melhor que esperado"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "descricao": "Almoço (atualizado)",
  "valor": 50.00,
  "categoria": "Alimentação",
  "data": "2025-11-14T12:30:00",
  "observacoes": "Restaurante XYZ - melhor que esperado",
  "dataCriacao": "2025-11-14T12:30:00",
  "dataAtualizacao": "2025-11-14T13:00:00"
}
```

---

### 5. Excluir Movimentação
**DELETE** `/api/movimentacoes/{id}`

**Parâmetros Path:**
- `id` (required) - ID da movimentação

**Response (204 No Content):**
```
(sem corpo)
```

---

### 6. Obter Resumo Financeiro
**GET** `/api/movimentacoes/resumo`

**Parâmetros Query:**
- `dias` (optional, default: 30) - Número de dias a considerar

**Response (200 OK):**
```json
{
  "totalReceitas": 0.00,
  "totalDespesas": 1250.75,
  "saldoAtual": -1250.75
}
```

---

### 7. Obter Evolução Financeira
**GET** `/api/movimentacoes/evolucao`

**Parâmetros Query:**
- `dias` (optional, default: 30) - Número de dias a considerar

**Response (200 OK):**
```json
[
  {
    "data": "2025-10-15",
    "receitas": 0.00,
    "despesas": 150.50,
    "saldo": -150.50
  },
  {
    "data": "2025-10-16",
    "receitas": 0.00,
    "despesas": 75.25,
    "saldo": -225.75
  }
]
```

---

### 8. Obter Despesas por Categoria
**GET** `/api/movimentacoes/despesas/categorias`

**Parâmetros Query:**
- `dias` (optional, default: 30) - Número de dias a considerar

**Response (200 OK):**
```json
[
  {
    "categoria": "Alimentação",
    "total": 450.75,
    "percentual": 35.50
  },
  {
    "categoria": "Transporte",
    "total": 320.25,
    "percentual": 25.25
  },
  {
    "categoria": "Educação",
    "total": 480.00,
    "percentual": 37.75
  }
]
```

---

### 9. Obter Previsão Financeira
**GET** `/api/movimentacoes/previsao`

**Response (200 OK):**
```json
{
  "receitaPrevista": 0.00,
  "despesaPrevista": 2850.50,
  "saldoPrevisto": -2850.50,
  "periodo": "2025-11"
}
```

**Nota:** A previsão é calculada com base na média de despesas do mês até o dia atual.

---

### 10. Exportar Movimentações (CSV)
**GET** `/api/movimentacoes/export`

**Response (200 OK):**
```
Content-Type: text/csv
Content-Disposition: attachment; filename=movimentacoes.csv

ID,Descrição,Valor,Categoria,Data,Observações
1,"Almoço",45.50,"Alimentação","14/11/2025 12:30","Restaurante XYZ"
2,"Táxi",25.00,"Transporte","14/11/2025 14:00","Ida ao trabalho"
```

---

## Códigos de Resposta

- **200 OK** - Requisição bem-sucedida
- **201 Created** - Recurso criado com sucesso
- **204 No Content** - Recurso deletado com sucesso
- **400 Bad Request** - Dados inválidos
- **401 Unauthorized** - Não autenticado
- **403 Forbidden** - Sem permissão
- **404 Not Found** - Recurso não encontrado
- **500 Internal Server Error** - Erro do servidor

---

## Tratamento de Erros

Todas as respostas de erro seguem este formato:

```json
{
  "message": "Descrição do erro",
  "status": 400,
  "timestamp": "2025-11-14T12:30:00"
}
```

---

## CORS

A API está configurada com CORS habilitado para todas as origens. Os seguintes métodos são permitidos:
- GET
- POST
- PUT
- DELETE
- OPTIONS

---

## Exemplos de Uso (cURL)

### Listar movimentações
```bash
curl -X GET "http://localhost:8080/api/movimentacoes?page=0&size=10" \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json"
```

### Criar movimentação
```bash
curl -X POST "http://localhost:8080/api/movimentacoes" \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "descricao": "Almoço",
    "valor": 45.50,
    "categoria": "Alimentação",
    "data": "2025-11-14T12:30:00",
    "observacoes": "Restaurante XYZ"
  }'
```

### Obter resumo financeiro
```bash
curl -X GET "http://localhost:8080/api/movimentacoes/resumo?dias=30" \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json"
```

### Exportar em CSV
```bash
curl -X GET "http://localhost:8080/api/movimentacoes/export" \
  -H "Authorization: Bearer {token}" \
  -o movimentacoes.csv
```

---

## Notas Importantes

1. **Autenticação**: Todos os endpoints, exceto `/auth/**`, requerem um JWT token válido.
2. **Paginação**: O padrão é página 0 com tamanho 10. Ajuste conforme necessário.
3. **Datas**: Use o formato ISO 8601 (YYYY-MM-DDTHH:mm:ss).
4. **Valores**: Use BigDecimal para evitar problemas de arredondamento.
5. **Categorias**: Não há restrição de valores, use conforme sua necessidade.

---

## Integração com o Frontend Angular

O serviço Angular `MovimentacoesService` já está configurado para consumir estes endpoints. Apenas certifique-se de que:

1. O backend está rodando em `http://localhost:8080` (ou configure a URL em `environment.ts`)
2. O usuário está autenticado e o JWT token está sendo enviado
3. As credenciais estão habilitadas (CORS com `withCredentials: true`)

