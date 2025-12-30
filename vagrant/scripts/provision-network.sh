set -e

if ! command -v ufw &> /dev/null; then
    sudo apt-get update -y
    sudo apt-get install -y ufw
fi

sudo ufw allow 22/tcp
sudo ufw allow 2376/tcp
sudo ufw allow 2377/tcp
sudo ufw allow 7946/tcp
sudo ufw allow 7946/udp
sudo ufw allow 4789/udp


sudo ufw --force enable
sudo ufw reload

echo "Portas abertas:"
sudo ufw status numbered