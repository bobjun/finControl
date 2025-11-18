#!/bin/bash

# Script de teste para a API de Movimentações
# Use: ./test-movimentacoes.sh <token_jwt>

TOKEN=${1:-"seu_token_aqui"}
BASE_URL="http://localhost:8080"
API_URL="$BASE_URL/api/movimentacoes"

# Cores para output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}=== Testes da API de Movimentações ===${NC}\n"

# Função para fazer requisições com erro handling
test_endpoint() {
    local method=$1
    local endpoint=$2
    local data=$3
    local description=$4

    echo -e "${YELLOW}Testando: $description${NC}"
    echo -e "  Método: $method"
    echo -e "  Endpoint: $endpoint"

    if [ -n "$data" ]; then
        echo -e "  Dados: $data"
        curl -s -X $method "$API_URL$endpoint" \
            -H "Authorization: Bearer $TOKEN" \
            -H "Content-Type: application/json" \
            -d "$data" | jq . || echo "Erro na requisição"
    else
        curl -s -X $method "$API_URL$endpoint" \
            -H "Authorization: Bearer $TOKEN" \
            -H "Content-Type: application/json" | jq . || echo "Erro na requisição"
    fi

    echo -e "\n"
}

# 1. Listar movimentações
test_endpoint "GET" "?page=0&size=10" "" "1. Listar movimentações (paginação)"

# 2. Criar movimentação
NOVA_MOVIMENTACAO='{
  "descricao": "Almoço no restaurante",
  "valor": 45.50,
  "categoria": "Alimentação",
  "data": "2025-11-14T12:30:00",
  "observacoes": "Almoço de trabalho"
}'

test_endpoint "POST" "" "$NOVA_MOVIMENTACAO" "2. Criar nova movimentação"

# Guardar o ID para testes subsequentes (assumindo que foi criado com sucesso)
# Este script simplificado não parse o ID automaticamente
# Em um script real, você extrairia: NOVO_ID=$(curl ... | jq '.id')

# 3. Resumo Financeiro (últimos 30 dias)
test_endpoint "GET" "/resumo?dias=30" "" "3. Obter resumo financeiro"

# 4. Evolução Financeira (últimos 7 dias)
test_endpoint "GET" "/evolucao?dias=7" "" "4. Obter evolução financeira"

# 5. Despesas por Categoria (últimos 30 dias)
test_endpoint "GET" "/despesas/categorias?dias=30" "" "5. Obter despesas por categoria"

# 6. Previsão Financeira
test_endpoint "GET" "/previsao" "" "6. Obter previsão financeira"

# 7. Buscar movimentação específica (ID 1 - ajuste conforme necessário)
test_endpoint "GET" "/1" "" "7. Buscar movimentação por ID"

# 8. Exportar em CSV
echo -e "${YELLOW}Testando: 8. Exportar movimentações em CSV${NC}"
echo -e "  Método: GET"
echo -e "  Endpoint: /export"

curl -s -X GET "$API_URL/export" \
    -H "Authorization: Bearer $TOKEN" \
    -o movimentacoes.csv

if [ -f movimentacoes.csv ]; then
    echo -e "${GREEN}Arquivo exportado com sucesso: movimentacoes.csv${NC}"
    echo "Conteúdo (primeiras 5 linhas):"
    head -5 movimentacoes.csv
else
    echo -e "${RED}Erro ao exportar arquivo${NC}"
fi

echo -e "\n${YELLOW}=== Testes Completos ===${NC}\n"

# Notas para testes manuais
echo -e "${YELLOW}Notas importantes:${NC}"
echo "1. Substitua 'seu_token_aqui' pelo seu token JWT real"
echo "2. O ID 1 pode não existir - ajuste conforme necessário"
echo "3. Para atualizar ou deletar, use:"
echo "   curl -X PUT '$API_URL/1' -d '{...}' -H 'Authorization: Bearer \$TOKEN'"
echo "   curl -X DELETE '$API_URL/1' -H 'Authorization: Bearer \$TOKEN'"
echo "4. Instale 'jq' para pretty-print JSON:"
echo "   sudo apt-get install jq"

