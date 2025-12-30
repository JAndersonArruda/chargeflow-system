set -e

echo "========================================="
echo "👷 CONFIGURANDO SWARM WORKER"
echo "========================================="

MANAGER_IP="192.168.56.10"
REGISTRY="$MANAGER_IP:5000"
TOKEN_FILE="/shared/tokens/worker"

DOCKER_DAEMON_CONFIG="/etc/docker/daemon.json"

# Configurar insecure registry
if [ -f "$DOCKER_DAEMON_CONFIG" ]; then
    sudo cp "$DOCKER_DAEMON_CONFIG" "${DOCKER_DAEMON_CONFIG}.backup.$(date +%Y%m%d_%H%M%S)"
    echo "{\"insecure-registries\": [\"$REGISTRY\"]}" | sudo tee "$DOCKER_DAEMON_CONFIG"
else
    echo "{\"insecure-registries\": [\"$REGISTRY\"]}" | sudo tee "$DOCKER_DAEMON_CONFIG"
fi

echo "🔄 Reiniciando Docker..."
sudo systemctl restart docker 2>/dev/null || sudo service docker restart
sleep 3


if docker info 2>/dev/null | grep -q "Swarm: active"; then
    echo "✅ Já está conectado ao Swarm!"
    
    NODE_ID=$(docker info 2>/dev/null | grep "NodeID:" | awk '{print $2}' | cut -c 1-12)
    echo "📋 Node ID: ${NODE_ID}..."
    
    exit 0
fi

if [ -d "/shared" ]; then
    for i in {1..30}; do
        if [ -f "$TOKEN_FILE" ]; then
            TOKEN=$(cat "$TOKEN_FILE" | tr -d '\n')
            if [ -n "$TOKEN" ]; then
                echo "✅ Token encontrado!"
                break
            fi
        fi
        
        if [ $i -eq 30 ]; then
            echo "⏰ Timeout: Token não encontrado após 30 segundos"
            TOKEN=""
            break
        fi
        
        sleep 1
    done
else
    echo "📁 Pasta /shared não montada"
    TOKEN=""
fi

if [ -n "$TOKEN" ]; then
    for attempt in {1..5}; do
        echo "Tentativa $attempt/5..."
        
        if docker swarm join --token "$TOKEN" "$MANAGER_IP":2377; then
            echo "🎉 CONECTADO AO SWARM COM SUCESSO!"
            
            WORKER_IP=$(hostname -I | awk '{print $2}')
            echo "📡 Worker IP: $WORKER_IP"
            echo "🏷️  Hostname: $(hostname)"
            
            exit 0
        fi
        
        if [ $attempt -lt 5 ]; then
            echo "🔄 Falhou, tentando novamente em 5 segundos..."
            sleep 5
        fi
    done
    
    echo "⚠️  Não conseguiu conectar após 5 tentativas"
fi