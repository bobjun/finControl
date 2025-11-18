#!/bin/bash

# Script para testar a autenticação JWT do backend
# Use: ./test-api.sh

API_URL="http://localhost:8080"
USERNAME="robert.bob@uol.com.br"
PASSWORD="972305"

echo "======================================"
echo "Testando API de Autenticação JWT"
echo "======================================"
echo ""

# 1. Teste de Login
echo "1️⃣  Fazendo LOGIN..."
echo "   POST $API_URL/api/login"
echo "   Body: {\"username\":\"$USERNAME\",\"password\":\"$PASSWORD\"}"
echo ""

LOGIN_RESPONSE=$(curl -s -X POST "$API_URL/api/login" \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"$USERNAME\",\"password\":\"$PASSWORD\"}")

echo "Resposta:"
echo "$LOGIN_RESPONSE" | jq '.' 2>/dev/null || echo "$LOGIN_RESPONSE"
echo ""

# Extrair token (funciona com jq)
TOKEN=$(echo "$LOGIN_RESPONSE" | jq -r '.token' 2>/dev/null)

if [ -z "$TOKEN" ] || [ "$TOKEN" = "null" ]; then
  echo "❌ Erro: Não consegui extrair o token da resposta!"
  echo "Verifique se o usuário admin/admin existe e a senha está correta."
  exit 1
fi

echo "✅ Token obtido com sucesso!"
echo "Token: $TOKEN"
echo ""

# 2. Teste com Authorization Header
echo "2️⃣  Testando /api/gastos com Authorization Header..."
echo "   GET $API_URL/api/gastos"
echo "   Header: Authorization: Bearer $TOKEN"
echo ""

GASTOS_RESPONSE=$(curl -s -i -X GET "$API_URL/api/gastos" \
  -H "Authorization: Bearer $TOKEN")

echo "Resposta (incluindo headers):"
echo "$GASTOS_RESPONSE"
echo ""

# 3. Teste sem token (deve falhar com 403)
echo "3️⃣  Testando /api/gastos SEM token (deve retornar 403)..."
echo "   GET $API_URL/api/gastos"
echo ""

NO_TOKEN_RESPONSE=$(curl -s -i -X GET "$API_URL/api/gastos")

echo "Resposta (deve ter status 403):"
echo "$NO_TOKEN_RESPONSE" | head -1
echo ""

# 4. Teste com Cookie (se o login setou cookie)
echo "4️⃣  Testando /api/gastos com Cookie (se disponível)..."
echo ""

COOKIE_RESPONSE=$(curl -s -i -b "jwt=$TOKEN" -X GET "$API_URL/api/gastos")

echo "Resposta:"
echo "$COOKIE_RESPONSE" | head -1
echo ""

echo "======================================"
echo "✅ Testes concluídos!"
echo "======================================"
echo ""
echo "Interpretação:"
echo "- Se teste 2 retornar 200 + JSON: ✅ JWT Header funciona"
echo "- Se teste 3 retornar 403: ✅ Segurança está funcionando"
echo "- Se teste 4 retornar 200 + JSON: ✅ Cookie também funciona"
echo ""
echo "Se teste 2 retornar 403: token não é válido ou filtro JWT não está ativo"

