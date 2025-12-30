set -e

echo "========================================="
echo "👑 CONFIGURANDO DOCKER SWARM MANAGER"
echo "========================================="

MANAGER_IP="192.168.56.10"

echo "🔧 1. Configurando Docker daemon..."
sudo mkdir -p /etc/docker
sudo tee /etc/docker/daemon.json << 'EOF'
{
  "insecure-registries": ["localhost:5000", "192.168.56.10:5000"]
}
EOF
sudo systemctl restart docker
sleep 2

echo "📁 Criando pastas compartilhadas..."
mkdir -p /shared/tokens

if docker info 2>/dev/null | grep -q "Swarm: active"; then
    echo "✅ Swarm já está ativo!"
    
    echo "🔑 Atualizando tokens..."
    docker swarm join-token -q manager > /shared/tokens/manager 2>/dev/null || true
    docker swarm join-token -q worker > /shared/tokens/worker 2>/dev/null || true
    
    docker node ls
    echo ""
    echo "📊 Tokens atualizados em /shared/tokens/"
    
    exit 0
fi

echo "🚀 Inicializando Docker Swarm..."
if docker swarm init --advertise-addr "$MANAGER_IP"; then
    echo "✅ Swarm inicializado com sucesso!"
else
    echo "❌ Falha ao inicializar Swarm"
    exit 1
fi

if ! docker service ls 2>/dev/null | grep -q "registry"; then
    docker service create \
        --name registry \
        --publish published=5000,target=5000 \
        --constraint node.role==manager \
        registry:2
    echo "✅ Registry criado na porta 5000"
else
    echo "✅ Registry já existe"
fi

echo "🔑 Gerando tokens..."
docker swarm join-token -q manager > /shared/tokens/manager
docker swarm join-token -q worker > /shared/tokens/worker

docker network create --driver overlay --attachable chargeflow-network 2>/dev/null || true

echo ""
echo "========================================="
echo "🎉 DOCKER SWARM MANAGER CONFIGURADO!"
echo "========================================="
echo ""
echo "📊 COMANDOS PARA WORKERS:"
echo "   docker swarm join --token $(cat /shared/tokens/worker) $MANAGER_IP:2377"
echo ""
echo "🔑 Token salvo em: /shared/tokens/worker"