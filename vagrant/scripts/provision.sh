echo "nameserver 8.8.8.8" | sudo tee /etc/resolv.conf
echo "nameserver 8.8.4.4" | sudo tee -a /etc/resolv.conf

set -e

SCRIPTS_DIR="/vagrant/scripts"

bash "$SCRIPTS_DIR/provision-docker.sh"
bash "$SCRIPTS_DIR/provision-network.sh"

docker --version

echo "========================================="
echo "✅ CONFIGURAÇÃO CONCLUÍDA!"
echo "   Docker: $(docker --version | cut -d' ' -f3- | tr ',' ' ')"
echo "   Firewall: $(sudo ufw status | head -1)"
echo "========================================="