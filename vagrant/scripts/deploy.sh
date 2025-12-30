#!/bin/bash
set -euo pipefail

echo "🚀 Iniciando deploy das imagens pré-buildadas..."
cd /vagrant/stacks

STACK_NAME="chargeflow"

echo "📦 1. Banco de dados..."
docker stack deploy -c db.yml $STACK_NAME
sleep 5

echo "📦 2. Proxy..."
docker stack deploy -c charge-proxy.yml $STACK_NAME
sleep 5

echo "📦 3. Manager..."
docker stack deploy -c charge-manager.yml $STACK_NAME

echo ""
echo "✅ Stack implantada!"
echo ""
docker stack services $STACK_NAME