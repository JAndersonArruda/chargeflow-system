.PHONY: help up down logs clean check-env check-docker status


ENV_FILE := .env
ENV_EXAMPLE := .env.example
COMPOSE_FILE := docker-compose.yml
PROJECT_NAME := chargeflow

GREEN := \033[0;32m
YELLOW := \033[1;33m
RED := \033[0;31m
NC := \033[0m


define check_env
	@if [ ! -f $(ENV_FILE) ]; then \
		echo "$(YELLOW)⚠️  Arquivo .env não encontrado!$(NC)"; \
		echo "$(YELLOW)📋 Copiando $(ENV_EXAMPLE) para .env...$(NC)"; \
		cp $(ENV_EXAMPLE) $(ENV_FILE); \
		echo "$(GREEN)✅ Arquivo .env criado.$(NC)"; \
		echo "$(YELLOW)📝 Configure as variáveis se necessário.$(NC)"; \
	fi
	# @# Carrega variáveis do .env
	# @if [ -f $(ENV_FILE) ]; then \
	# 	export $$(grep -v '^#' $(ENV_FILE) | xargs); \
	# fi
endef

define check_docker
	@if ! docker info > /dev/null 2>&1; then \
		echo "$(RED)❌ Docker não está rodando!$(NC)"; \
		echo "$(YELLOW)Por favor, inicie o Docker.$(NC)"; \
		exit 1; \
	fi
endef

define print_endpoints
	@echo "$(GREEN)🌐 Endpoints:$(NC)"; \
	echo "  - Manager API:    http://localhost:$${MANAGER_PORT:-8081}"; \
	echo "  - Proxy (SOAP):   http://localhost:$${PROXY_PORT:-8080}"; \
	echo "  - WSDL:          http://localhost:$${PROXY_PORT:-8080}/ws/chargeservice.wsdl"; \
	echo "  - PostgreSQL:    localhost:$${POSTGRES_PORT:-5432}"; \
	echo ""; \
	echo "$(YELLOW)📝 Comandos úteis:$(NC)"; \
	echo "  make logs        # Ver logs"; \
	echo "  make db          # Conectar ao banco"; \
	echo "  make down        # Parar serviços"; \
	echo "  make clean       # Limpar tudo (pergunta antes)"; \
	echo ""
endef

check-env:
	$(call check_env)

check-docker:
	$(call check_docker)


help: ## Mostra esta ajuda
	@echo "$(GREEN)🚀 $(PROJECT_NAME) - Comandos disponíveis:$(NC)"
	@echo ""
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "  $(GREEN)%-20s$(NC) %s\n", $$1, $$2}'

up: check-env check-docker ## Inicia ambiente completo
	@echo "$(GREEN)========================================$(NC)"
	@echo "$(GREEN)🚀  INICIANDO AMBIENTE DE DESENVOLVIMENTO$(NC)"
	@echo "$(GREEN)========================================$(NC)"
	@echo ""
	@echo "$(YELLOW)🏗️  Construindo imagens (se necessário)...$(NC)"
	@docker compose --env-file $(ENV_FILE) -f $(COMPOSE_FILE) build --quiet
	@echo ""
	@echo "$(YELLOW)📦 Subindo serviços...$(NC)"
	@docker compose --env-file $(ENV_FILE) -f $(COMPOSE_FILE) up -d
	@echo ""
	@echo "$(YELLOW)⏳ Aguardando serviços iniciarem...$(NC)"
	@sleep 5
	@echo ""
	@echo "$(GREEN)✅ AMBIENTE DE DESENVOLVIMENTO PRONTO!$(NC)"
	@echo "$(GREEN)========================================$(NC)"
	@echo ""
	$(call print_endpoints)

down: ## Para todos os serviços (PRESERVA volumes)
	@echo "$(YELLOW)🛑 Parando serviços...$(NC)"
	@docker compose -f $(COMPOSE_FILE) down
	@echo "$(GREEN)✅ Ambiente parado$(NC)"

clean: ## Remove containers e redes
	@echo "$(YELLOW)🧹 Limpando ambiente...$(NC)"
	@docker compose -f $(COMPOSE_FILE) down --remove-orphans
	@echo "$(GREEN)✅ Containers e redes removidos$(NC)"

clean-all: ## Remove TUDO (containers, volumes, redes, imagens locais)
	@echo "$(RED)========================================$(NC)"
	@echo "$(RED)💣  LIMPEZA COMPLETA$(NC)"
	@echo "$(RED)========================================$(NC)"
	@echo ""
	@echo "$(RED)❌ Isso vai REMOVER:$(NC)"
	@echo "$(RED)  • Todos os containers$(NC)"
	@echo "$(RED)  • Todas as redes$(NC)"
	@echo "$(RED)  • Volume do PostgreSQL (dados PERDIDOS!)$(NC)"
	@echo "$(RED)  • Imagens construídas localmente$(NC)"
	@echo ""
	@read -p "$(RED)Tem certeza? Digite 'SIM' para confirmar: $(NC)" confirm; \
	if [ "$$confirm" = "SIM" ]; then \
		docker compose -f $(COMPOSE_FILE) down -v --rmi local --remove-orphans; \
		echo "$(RED)✅ Limpeza COMPLETA realizada$(NC)"; \
		echo "$(RED)⚠️  Dados do banco foram APAGADOS!$(NC)"; \
	fi

logs: ## Logs de todos os serviços (follow)
	docker compose logs -f

logs-proxy: ## Logs apenas do proxy
	docker compose logs -f proxy

logs-manager: ## Logs apenas do manager
	docker compose logs -f manager

restart: ## Reinicia todos os serviços
	docker compose restart
	@echo "✅ Serviços reiniciados"

restart-proxy: ## Reinicia apenas proxy
	docker compose restart proxy
	@echo "✅ Proxy reiniciado"

restart-manager: ## Reinicia apenas manager
	docker compose restart manager
	@echo "✅ Manager reiniciado"

db: ## Conecta ao PostgreSQL
	@echo "$(GREEN)🔌 Conectando ao PostgreSQL...$(NC)"
	docker compose -f $(COMPOSE_FILE) exec postgres psql -U postgres -d chargerdb

psql: db ## Alias para db

status: ## Status dos containers
	@echo "$(GREEN)📊 Status dos serviços:$(NC)"
	docker compose -f $(COMPOSE_FILE) ps