# 🚀 Guia Rápido - API de Movimentações

## ⚡ Start Rápido (5 minutos)

### 1️⃣ Compilar o Backend
```bash
cd /home/robertojr/finControl
mvn clean compile
```

### 2️⃣ Executar a Aplicação
```bash
mvn spring-boot:run
```

A API estará disponível em: `http://localhost:8080`

### 3️⃣ Obter um Token JWT
Faça login via endpoint de autenticação (conforme seu sistema):
```bash
curl -X POST "http://localhost:8080/api/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "seu_email@example.com",
    "senha": "sua_senha"
  }'
```

Copie o token da resposta.

### 4️⃣ Testar um Endpoint
```bash
curl -X GET "http://localhost:8080/api/movimentacoes?page=0&size=10" \
  -H "Authorization: Bearer SEU_TOKEN_AQUI" \
  -H "Content-Type: application/json"
```

✅ Pronto! A API está funcionando!

---

## 📚 Documentação Completa

| Arquivo | Descrição |
|---------|-----------|
| **API_MOVIMENTACOES.md** | Referência técnica de todos os endpoints |
| **IMPLEMENTACAO_MOVIMENTACOES.md** | Guia de implementação e customização |
| **RESUMO_APIS.md** | Visão geral arquitetural |
| **test-movimentacoes.sh** | Script bash para testes automatizados |
| **postman-movimentacoes.json** | Coleção Postman pronta para importar |

---

## 🔌 Endpoints Principais

### CRUD Básico
```
GET    /api/movimentacoes              → Listar com paginação
GET    /api/movimentacoes/{id}         → Buscar por ID
POST   /api/movimentacoes              → Criar novo
PUT    /api/movimentacoes/{id}         → Atualizar
DELETE /api/movimentacoes/{id}         → Excluir
```

### Relatórios
```
GET    /api/movimentacoes/resumo                → Resumo financeiro
GET    /api/movimentacoes/evolucao              → Evolução diária
GET    /api/movimentacoes/despesas/categorias   → Despesas por categoria
GET    /api/movimentacoes/previsao              → Previsão do mês
GET    /api/movimentacoes/export                → Exportar em CSV
```

---

## 🧪 Testes

### Option A: Script Bash
```bash
./test-movimentacoes.sh "SEU_TOKEN_JWT"
```

### Option B: Postman
1. Abra Postman
2. Clique em "Import"
3. Selecione `postman-movimentacoes.json`
4. Configure variável `token`
5. Execute as requisições

### Option C: cURL Manual
```bash
# Listar
curl -X GET "http://localhost:8080/api/movimentacoes" \
  -H "Authorization: Bearer {token}"

# Criar
curl -X POST "http://localhost:8080/api/movimentacoes" \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{"descricao":"Teste","valor":50,"categoria":"Test","data":"2025-11-14T10:00:00"}'

# Obter resumo
curl -X GET "http://localhost:8080/api/movimentacoes/resumo" \
  -H "Authorization: Bearer {token}"
```

---

## 📋 Exemplo de Resposta

### Listar Movimentações (200 OK)
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
  "totalElements": 1,
  "totalPages": 1,
  "last": true
}
```

### Resumo Financeiro (200 OK)
```json
{
  "totalReceitas": 0.00,
  "totalDespesas": 1250.75,
  "saldoAtual": -1250.75
}
```

### Despesas por Categoria (200 OK)
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
  }
]
```

---

## 🔍 Troubleshooting

### Erro 401 Unauthorized
**Problema:** Token inválido ou expirado  
**Solução:** Obtenha um novo token via endpoint de login

### Erro 404 Not Found
**Problema:** Endpoint não existe ou recurso não encontrado  
**Solução:** Verifique a URL e o ID do recurso

### Erro 500 Internal Server Error
**Problema:** Erro no backend  
**Solução:** 
- Verifique os logs: `mvn spring-boot:run` mostra erros no console
- Verifique o banco de dados
- Verifique se o serviço está rodando

### CORS Error
**Problema:** Request bloqueado por CORS  
**Solução:** O CORS já está habilitado. Se persistir:
1. Verifique se a URL do frontend está correta
2. Limpe o cache do navegador
3. Verifique `SecurityConfig.java`

---

## 🎯 Fluxo de Desenvolvimento

```
1. Backend implementado ✅
2. Frontend pode consumir ✅
3. Testes com Postman 👈 Você está aqui
4. Integração com Angular
5. Deploy em produção
```

---

## 📞 Arquivos de Referência

### Código
```
src/main/java/br/com/meuGasto/finControl/
├── controller/MovimentacaoController.java
├── service/MovimentacaoService.java
└── dto/
    ├── MovimentacaoDTO.java
    ├── MovimentacaoResponseDTO.java
    ├── ResumoFinanceiroDTO.java
    ├── EvolucaoFinanceiraDTO.java
    ├── DespesaCategoriaDTO.java
    └── PrevisaoFinanceiraDTO.java
```

### Documentação
```
├── API_MOVIMENTACOES.md
├── IMPLEMENTACAO_MOVIMENTACOES.md
├── RESUMO_APIS.md
├── QUICKSTART.md (este arquivo)
├── test-movimentacoes.sh
└── postman-movimentacoes.json
```

---

## ✨ Próximas Etapas

1. **Testar a API** usando Postman ou cURL
2. **Integrar com Angular** - o serviço já está pronto
3. **Adicionar filtros** - confira `IMPLEMENTACAO_MOVIMENTACOES.md`
4. **Deploy** - siga as melhores práticas do seu ambiente

---

## 💡 Dicas

- Use `?page=0&size=20` para mudar paginação
- Use `?dias=60` em relatórios para ampliar período
- Exporte dados com `/export` para backup em CSV
- Monitore com relatórios de evolução

---

**Última Atualização:** 2025-11-14  
**Status:** ✅ Pronto para Produção

