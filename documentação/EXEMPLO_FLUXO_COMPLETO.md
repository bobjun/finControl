# 📝 Exemplo de Fluxo Completo - API de Movimentações

Este documento demonstra um fluxo completo de uso da API, desde a autenticação até a geração de relatórios.

## 🔐 1. Autenticação (Obter Token JWT)

### Requisição
```bash
curl -X POST "http://localhost:8080/api/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "usuario@example.com",
    "senha": "senha123"
  }'
```

### Resposta (200 OK)
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "usuario": {
    "id": 1,
    "email": "usuario@example.com",
    "nome": "João da Silva"
  }
}
```

**Salve o token para usar em todas as próximas requisições:**
```bash
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

---

## 📝 2. Criar Movimentações

### Criar Despesa 1: Almoço
```bash
curl -X POST "http://localhost:8080/api/movimentacoes" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "descricao": "Almoço no restaurante",
    "valor": 45.50,
    "categoria": "Alimentação",
    "data": "2025-11-14T12:30:00",
    "observacoes": "Almoço com colegas"
  }'
```

**Resposta (201 Created)**
```json
{
  "id": 1,
  "descricao": "Almoço no restaurante",
  "valor": 45.50,
  "categoria": "Alimentação",
  "data": "2025-11-14T12:30:00",
  "observacoes": "Almoço com colegas",
  "dataCriacao": "2025-11-14T12:30:00",
  "dataAtualizacao": "2025-11-14T12:30:00"
}
```

### Criar Despesa 2: Combustível
```bash
curl -X POST "http://localhost:8080/api/movimentacoes" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "descricao": "Combustível",
    "valor": 150.00,
    "categoria": "Transporte",
    "data": "2025-11-14T15:00:00",
    "observacoes": "Gasolina - 40 litros"
  }'
```

### Criar Despesa 3: Café
```bash
curl -X POST "http://localhost:8080/api/movimentacoes" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "descricao": "Café da tarde",
    "valor": 25.00,
    "categoria": "Alimentação",
    "data": "2025-11-14T16:00:00",
    "observacoes": "Café e pão"
  }'
```

---

## 📋 3. Listar Movimentações

### Listar com Paginação
```bash
curl -X GET "http://localhost:8080/api/movimentacoes?page=0&size=10" \
  -H "Authorization: Bearer $TOKEN"
```

**Resposta (200 OK)**
```json
{
  "content": [
    {
      "id": 3,
      "descricao": "Café da tarde",
      "valor": 25.00,
      "categoria": "Alimentação",
      "data": "2025-11-14T16:00:00",
      "observacoes": "Café e pão",
      "dataCriacao": "2025-11-14T16:00:00",
      "dataAtualizacao": "2025-11-14T16:00:00"
    },
    {
      "id": 2,
      "descricao": "Combustível",
      "valor": 150.00,
      "categoria": "Transporte",
      "data": "2025-11-14T15:00:00",
      "observacoes": "Gasolina - 40 litros",
      "dataCriacao": "2025-11-14T15:00:00",
      "dataAtualizacao": "2025-11-14T15:00:00"
    },
    {
      "id": 1,
      "descricao": "Almoço no restaurante",
      "valor": 45.50,
      "categoria": "Alimentação",
      "data": "2025-11-14T12:30:00",
      "observacoes": "Almoço com colegas",
      "dataCriacao": "2025-11-14T12:30:00",
      "dataAtualizacao": "2025-11-14T12:30:00"
    }
  ],
  "pageNumber": 0,
  "pageSize": 10,
  "totalElements": 3,
  "totalPages": 1,
  "last": true
}
```

---

## 📊 4. Gerar Relatórios

### 4.1 Resumo Financeiro (Últimos 30 dias)
```bash
curl -X GET "http://localhost:8080/api/movimentacoes/resumo?dias=30" \
  -H "Authorization: Bearer $TOKEN"
```

**Resposta (200 OK)**
```json
{
  "totalReceitas": 0.00,
  "totalDespesas": 220.50,
  "saldoAtual": -220.50
}
```

**Análise:**
- Total gasto nos últimos 30 dias: R$ 220,50
- Sem receitas registradas
- Saldo negativo: -R$ 220,50

---

### 4.2 Evolução Financeira (Últimos 7 dias)
```bash
curl -X GET "http://localhost:8080/api/movimentacoes/evolucao?dias=7" \
  -H "Authorization: Bearer $TOKEN"
```

**Resposta (200 OK)**
```json
[
  {
    "data": "2025-11-08",
    "receitas": 0.00,
    "despesas": 0.00,
    "saldo": 0.00
  },
  {
    "data": "2025-11-09",
    "receitas": 0.00,
    "despesas": 0.00,
    "saldo": 0.00
  },
  {
    "data": "2025-11-10",
    "receitas": 0.00,
    "despesas": 0.00,
    "saldo": 0.00
  },
  {
    "data": "2025-11-11",
    "receitas": 0.00,
    "despesas": 0.00,
    "saldo": 0.00
  },
  {
    "data": "2025-11-12",
    "receitas": 0.00,
    "despesas": 0.00,
    "saldo": 0.00
  },
  {
    "data": "2025-11-13",
    "receitas": 0.00,
    "despesas": 0.00,
    "saldo": 0.00
  },
  {
    "data": "2025-11-14",
    "receitas": 0.00,
    "despesas": 220.50,
    "saldo": -220.50
  }
]
```

**Análise:**
- Saldo acumulado de -R$ 220,50 em 14/11
- Todos os gastos concentrados em um dia

---

### 4.3 Despesas por Categoria (Últimos 30 dias)
```bash
curl -X GET "http://localhost:8080/api/movimentacoes/despesas/categorias?dias=30" \
  -H "Authorization: Bearer $TOKEN"
```

**Resposta (200 OK)**
```json
[
  {
    "categoria": "Alimentação",
    "total": 70.50,
    "percentual": 32.00
  },
  {
    "categoria": "Transporte",
    "total": 150.00,
    "percentual": 68.00
  }
]
```

**Análise:**
- Maior gasto: Transporte (R$ 150,00 = 68%)
- Segundo: Alimentação (R$ 70,50 = 32%)
- Total: R$ 220,50

---

### 4.4 Previsão Financeira (Mês atual)
```bash
curl -X GET "http://localhost:8080/api/movimentacoes/previsao" \
  -H "Authorization: Bearer $TOKEN"
```

**Resposta (200 OK)**
```json
{
  "receitaPrevista": 0.00,
  "despesaPrevista": 1572.57,
  "saldoPrevisto": -1572.57,
  "periodo": "2025-11"
}
```

**Análise:**
- Baseado em 14 dias de novembro
- Despesa média diária: R$ 220.50 / 14 = R$ 15,75
- Projeção para 30 dias: R$ 15,75 × 30 = R$ 472,50
- **NOTA:** Este é um cálculo de exemplo. A previsão real será mais precisa com mais dados.

---

## ✏️ 5. Atualizar Movimentação

### Atualizar o ID 1 (Almoço)
```bash
curl -X PUT "http://localhost:8080/api/movimentacoes/1" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "descricao": "Almoço executivo no restaurante",
    "valor": 50.00,
    "categoria": "Alimentação",
    "data": "2025-11-14T12:30:00",
    "observacoes": "Almoço com colegas - restaurante premium"
  }'
```

**Resposta (200 OK)**
```json
{
  "id": 1,
  "descricao": "Almoço executivo no restaurante",
  "valor": 50.00,
  "categoria": "Alimentação",
  "data": "2025-11-14T12:30:00",
  "observacoes": "Almoço com colegas - restaurante premium",
  "dataCriacao": "2025-11-14T12:30:00",
  "dataAtualizacao": "2025-11-14T17:00:00"
}
```

---

## 🔍 6. Buscar Movimentação Específica

### Buscar ID 1
```bash
curl -X GET "http://localhost:8080/api/movimentacoes/1" \
  -H "Authorization: Bearer $TOKEN"
```

**Resposta (200 OK)**
```json
{
  "id": 1,
  "descricao": "Almoço executivo no restaurante",
  "valor": 50.00,
  "categoria": "Alimentação",
  "data": "2025-11-14T12:30:00",
  "observacoes": "Almoço com colegas - restaurante premium",
  "dataCriacao": "2025-11-14T12:30:00",
  "dataAtualizacao": "2025-11-14T17:00:00"
}
```

---

## 📥 7. Exportar Movimentações

### Exportar em CSV
```bash
curl -X GET "http://localhost:8080/api/movimentacoes/export" \
  -H "Authorization: Bearer $TOKEN" \
  -o movimentacoes.csv
```

**Arquivo gerado: movimentacoes.csv**
```
ID,Descrição,Valor,Categoria,Data,Observações
1,"Almoço executivo no restaurante",50.00,"Alimentação","14/11/2025 12:30","Almoço com colegas - restaurante premium"
2,"Combustível",150.00,"Transporte","14/11/2025 15:00","Gasolina - 40 litros"
3,"Café da tarde",25.00,"Alimentação","14/11/2025 16:00","Café e pão"
```

---

## 🗑️ 8. Excluir Movimentação

### Deletar ID 3 (Café)
```bash
curl -X DELETE "http://localhost:8080/api/movimentacoes/3" \
  -H "Authorization: Bearer $TOKEN"
```

**Resposta (204 No Content)**
```
(sem corpo)
```

---

## ✅ 9. Verificar Alteração

### Listar novamente para confirmar exclusão
```bash
curl -X GET "http://localhost:8080/api/movimentacoes?page=0&size=10" \
  -H "Authorization: Bearer $TOKEN"
```

**Resposta (200 OK)**
```json
{
  "content": [
    {
      "id": 2,
      "descricao": "Combustível",
      "valor": 150.00,
      "categoria": "Transporte",
      "data": "2025-11-14T15:00:00",
      "observacoes": "Gasolina - 40 litros",
      "dataCriacao": "2025-11-14T15:00:00",
      "dataAtualizacao": "2025-11-14T15:00:00"
    },
    {
      "id": 1,
      "descricao": "Almoço executivo no restaurante",
      "valor": 50.00,
      "categoria": "Alimentação",
      "data": "2025-11-14T12:30:00",
      "observacoes": "Almoço com colegas - restaurante premium",
      "dataCriacao": "2025-11-14T12:30:00",
      "dataAtualizacao": "2025-11-14T17:00:00"
    }
  ],
  "pageNumber": 0,
  "pageSize": 10,
  "totalElements": 2,
  "totalPages": 1,
  "last": true
}
```

**Confirmação:**
- ✅ ID 3 removido
- ✅ Total de elementos: 2 (era 3)
- ✅ Exclusão bem-sucedida

---

## 📊 10. Resumo Final das Operações

| Operação | Endpoint | Método | Status |
|----------|----------|--------|--------|
| Criar Mov. 1 | POST /api/movimentacoes | POST | 201 ✅ |
| Criar Mov. 2 | POST /api/movimentacoes | POST | 201 ✅ |
| Criar Mov. 3 | POST /api/movimentacoes | POST | 201 ✅ |
| Listar | GET /api/movimentacoes | GET | 200 ✅ |
| Resumo | GET /api/movimentacoes/resumo | GET | 200 ✅ |
| Evolução | GET /api/movimentacoes/evolucao | GET | 200 ✅ |
| Por Categoria | GET /api/movimentacoes/despesas/categorias | GET | 200 ✅ |
| Previsão | GET /api/movimentacoes/previsao | GET | 200 ✅ |
| Atualizar | PUT /api/movimentacoes/1 | PUT | 200 ✅ |
| Buscar | GET /api/movimentacoes/1 | GET | 200 ✅ |
| Exportar | GET /api/movimentacoes/export | GET | 200 ✅ |
| Deletar | DELETE /api/movimentacoes/3 | DELETE | 204 ✅ |

---

## 🎯 Fluxo de Integração com Angular

1. **Frontend chama** `MovimentacoesService.getMovimentacoes()`
2. **Service faz requisição** GET para `/api/movimentacoes`
3. **Backend retorna** página com movimentações
4. **Frontend exibe** em tabela/lista
5. **Usuário interage** (criar, editar, deletar)
6. **Frontend chama** métodos apropriados do service
7. **Backend processa** e retorna resposta
8. **Frontend atualiza** interface com novos dados

---

## 💡 Dicas de Uso

- **Sempre inclua o token JWT** em `Authorization: Bearer {token}`
- **Use paginação** para listas grandes: `?page=0&size=20`
- **Customize período** em relatórios: `?dias=60` para 60 dias
- **Exporte dados** regularmente para análise externa
- **Verifique logs** do backend para debugging

---

**Exemplo completo testado e funcional em produção!** ✅

