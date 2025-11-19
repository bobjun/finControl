# FinControl

Sistema de Controle Financeiro Pessoal

## Descrição
FinControl é uma aplicação web para gerenciamento de finanças pessoais, permitindo o controle de gastos, geração de relatórios e acompanhamento financeiro.

## Tecnologias
- Java
- Spring Boot
- Spring Security
- Spring Data JPA
- Thymeleaf
- Maven

## Funcionalidades
- Cadastro e autenticação de usuários
- Registro e categorização de gastos
- Dashboard com visão geral das finanças
- Geração de relatórios
- Exportação de dados
- Sistema de notificações

## Como executar
1. Clone o repositório
2. Escolha o perfil:
   - Desenvolvimento (padrão): configurações em `src/main/resources/application-dev.properties`
   - Produção: configure variáveis de ambiente (`DATABASE_URL`, `DATABASE_USERNAME`, etc.) ou ajuste `application-prod.properties`
3. Execute com Maven:
   - Dev: `mvn spring-boot:run`
   - Outro perfil: `mvn spring-boot:run -Dspring-boot.run.profiles=prod`
4. Acesse `http://localhost:8080`

## Observabilidade e segurança
- Ativamos Prometheus via Actuator (`/actuator/prometheus`); proteja o endpoint com autenticação/autorização.
- O Actuator só expõe `health`, `info` e `prometheus` em produção; personalize via variáveis ou arquivos de perfil.
- Credenciais nunca devem ficar em `application.properties`; utilize variáveis de ambiente ou Secret Manager.

## Cache
- O projeto utiliza Caffeine (`CacheManager` em `CacheConfig`). Ajuste TTL e tamanho máximo conforme a carga da aplicação.

## API de Movimentações Financeiras ✨ NOVO

Uma API REST completa para gerenciar movimentações financeiras foi implementada!

### 📚 Documentação
- **[INDEX.md](INDEX.md)** - Índice completo de documentação
- **[QUICKSTART.md](QUICKSTART.md)** - Guia rápido (5 minutos)
- **[API_MOVIMENTACOES.md](API_MOVIMENTACOES.md)** - Referência técnica
- **[IMPLEMENTACAO_MOVIMENTACOES.md](IMPLEMENTACAO_MOVIMENTACOES.md)** - Detalhes de implementação
- **[AJUSTES_FUTUROS.md](AJUSTES_FUTUROS.md)** - Melhorias opcionais

### 🔌 Endpoints

#### CRUD Básico
```
GET    /api/movimentacoes              → Listar com paginação
GET    /api/movimentacoes/{id}         → Buscar por ID
POST   /api/movimentacoes              → Criar novo
PUT    /api/movimentacoes/{id}         → Atualizar
DELETE /api/movimentacoes/{id}         → Excluir
```

#### Relatórios e Análises
```
GET    /api/movimentacoes/resumo                → Resumo financeiro
GET    /api/movimentacoes/evolucao              → Evolução diária
GET    /api/movimentacoes/despesas/categorias   → Despesas por categoria
GET    /api/movimentacoes/previsao              → Previsão mensal
GET    /api/movimentacoes/export                → Exportar em CSV
```

### 🧪 Testes
- **Script Bash:** `./test-movimentacoes.sh "seu_token_jwt"`
- **Postman:** Importe `postman-movimentacoes.json`
- **cURL:** Veja exemplos em API_MOVIMENTACOES.md

### 📝 Exemplo de Requisição
```bash
curl -X GET "http://localhost:8080/api/movimentacoes?page=0&size=10" \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json"
```

### 📊 Exemplo de Resposta
```json
{
  "content": [
    {
      "id": 1,
      "descricao": "Almoço",
      "valor": 45.50,
      "categoria": "Alimentação",
      "data": "2025-11-14T12:30:00",
      "observacoes": "Restaurante XYZ"
    }
  ],
  "pageNumber": 0,
  "pageSize": 10,
  "totalElements": 150,
  "totalPages": 15,
  "last": false
}
```

### 🎯 Próximos Passos
1. Leia [QUICKSTART.md](QUICKSTART.md)
2. Compile o projeto: `mvn clean compile`
3. Execute: `mvn spring-boot:run`
4. Teste os endpoints com Postman ou cURL
5. Integre com seu frontend Angular

