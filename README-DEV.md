# 🚀 Ambiente de Desenvolvimento - ChargeFlow

Este documento descreve como configurar e usar o ambiente de desenvolvimento local para o projeto ChargeFlow.

## 📋 Pré-requisitos

- Docker e Docker Compose instalados
- Java 21 (para desenvolvimento local, se necessário)
- Maven 3.9+ (para builds locais, se necessário)

## 🏗️ Estrutura da Infraestrutura

O ambiente de desenvolvimento utiliza **docker-compose** para orquestrar os seguintes serviços:

1. **PostgreSQL 15** - Banco de dados
2. **Charge Proxy** - Serviço SOAP (porta 8080)
3. **Charge Manager** - API REST (porta 8081)

## 🚀 Início Rápido

### 1. Configurar Variáveis de Ambiente

Copie o arquivo de exemplo e configure conforme necessário:

```bash
cp env.example .env
```

Edite o arquivo `.env` para ajustar configurações específicas do seu ambiente.

### 2. Escolher Modo de Desenvolvimento

Você tem **3 opções** de desenvolvimento:

#### 🔥 Opção 1: Com Hot Reload (RECOMENDADO)

**Não precisa rebuild após alterações!** O Spring Boot DevTools detecta mudanças automaticamente.

```bash
./scripts/dev-start-hotreload.sh
```

✅ **Vantagens:**
- Alterações em arquivos `.java` são detectadas automaticamente
- Reinício automático da aplicação (~5-10 segundos)
- Volumes montam código fonte diretamente
- Debug remoto disponível (portas 5005 e 5006)

⚠️ **Primeira inicialização pode demorar** (compila tudo pela primeira vez)

#### 🐳 Opção 2: Docker Tradicional (Sem Hot Reload)

Requer rebuild manual após cada alteração:

```bash
./scripts/dev-start.sh
```

Depois de alterar código, faça rebuild:
```bash
./scripts/dev-rebuild.sh charge-manager
```

#### 💻 Opção 3: Desenvolvimento Local (IDE)

Apenas o banco no Docker, aplicações rodam localmente na sua máquina:

```bash
./scripts/dev-local.sh
```

Depois, em terminais separados:
```bash
# Terminal 1 - Proxy
cd charge-proxy
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Terminal 2 - Manager
cd charge-manager
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 3. Verificar Status dos Serviços

```bash
docker-compose -f docker-compose.dev.yml ps
```

### 4. Ver Logs

Todos os serviços:
```bash
./scripts/dev-logs.sh
# ou
docker-compose -f docker-compose.dev.yml logs -f
```

Serviço específico:
```bash
./scripts/dev-logs.sh charge-manager
./scripts/dev-logs.sh charge-proxy
./scripts/dev-logs.sh postgres
```

## 🛑 Parar Ambiente

### Com Hot Reload

```bash
./scripts/dev-stop-hotreload.sh
```

Ou:

```bash
docker-compose -f docker-compose.dev-hotreload.yml down
```

### Sem Hot Reload

```bash
./scripts/dev-stop.sh
```

Ou:

```bash
docker-compose -f docker-compose.dev.yml down
```

Para remover volumes também (apaga dados do banco):

```bash
docker-compose -f docker-compose.dev.yml down -v
```

## 🔧 Comandos Úteis

### Rebuild de Serviços

Rebuild completo:
```bash
./scripts/dev-rebuild.sh
```

Rebuild de um serviço específico:
```bash
./scripts/dev-rebuild.sh charge-manager
./scripts/dev-rebuild.sh charge-proxy
```

### Executar Comandos dentro dos Containers

**PostgreSQL:**
```bash
docker-compose -f docker-compose.dev.yml exec postgres psql -U postgres -d chargerdb
```

**Charge Manager:**
```bash
docker-compose -f docker-compose.dev.yml exec charge-manager sh
```

**Charge Proxy:**
```bash
docker-compose -f docker-compose.dev.yml exec charge-proxy sh
```

### Verificar Health Checks

Os serviços expõem endpoints de health via Actuator:

- Manager: http://localhost:8081/actuator/health
- Proxy: http://localhost:8080/actuator/health

## 🌐 Endpoints

Após iniciar o ambiente, os seguintes endpoints estarão disponíveis:

- **Manager API:** http://localhost:8081
  - Test route: http://localhost:8081/charge-manager/test-route
  - Health: http://localhost:8081/actuator/health

- **Proxy SOAP:** http://localhost:8080
  - WSDL: http://localhost:8080/ws/chargeservice.wsdl
  - SOAP Endpoint: http://localhost:8080/ws
  - Health: http://localhost:8080/actuator/health

- **PostgreSQL:** localhost:5432
  - Database: chargerdb
  - User: postgres
  - Password: postgres

## 🔥 Hot Reload - Como Funciona

### Com Hot Reload Ativado

Quando você usa `docker-compose.dev-hotreload.yml`:

1. **Altere qualquer arquivo `.java`** nos diretórios:
   - `charge-manager/src/main/java/`
   - `charge-proxy/src/main/java/`

2. **Salve o arquivo** (Ctrl+S / Cmd+S)

3. **O Spring Boot DevTools detecta automaticamente** a mudança

4. **A aplicação reinicia sozinha** em ~5-10 segundos

5. **Pronto!** Suas mudanças já estão ativas

### O que é Monitorado

✅ **Monitorado (reinicia aplicação):**
- Arquivos `.java` em `src/main/java/`
- Arquivos de configuração `.properties` e `.yml`

❌ **NÃO monitorado (requer rebuild):**
- `pom.xml` (dependências)
- Arquivos WSDL/XSD
- Classes geradas automaticamente

### Logs do Hot Reload

Você verá mensagens como estas nos logs quando houver mudança:

```
Reloading because [arquivo.java] changed
Restarting due to 1 class path changes
```

### Desabilitar Hot Reload Temporariamente

Se precisar desabilitar temporariamente, pare os containers e use:

```bash
docker-compose -f docker-compose.dev.yml up -d
```

## 🔄 Fluxo de Desenvolvimento

### Com Hot Reload

1. Faça alterações no código
2. Salve o arquivo
3. Aguarde o restart automático (~5-10s)
4. Teste suas mudanças

### Sem Hot Reload

1. Faça alterações no código
2. Rebuild do serviço afetado:
   ```bash
   ./scripts/dev-rebuild.sh charge-manager
   ```

### Executar Migrations Flyway

As migrations Flyway são executadas automaticamente ao iniciar o charge-proxy se estiverem configuradas em `src/main/resources/db/migration/`.

### Debug Remoto

**Com Hot Reload:** As portas de debug já estão expostas automaticamente:
- Manager: `localhost:5005`
- Proxy: `localhost:5006`

**Configurar no seu IDE (IntelliJ/Eclipse):**
1. Crie uma configuração de "Remote JVM Debug"
2. Host: `localhost`
3. Port: `5005` (Manager) ou `5006` (Proxy)
4. Connect

**Sem Hot Reload:** Adicione manualmente no `docker-compose.dev.yml`:
```yaml
environment:
  - JAVA_OPTS=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005
ports:
  - "5005:5005"
```

## 📊 Diferenças entre Dev e Produção

| Aspecto | Desenvolvimento | Produção |
|---------|----------------|----------|
| Orquestração | docker-compose | Docker Swarm |
| Registry | Build local | Registry privado |
| VMs | Não necessário | Vagrant + VMs |
| Network | Bridge | Overlay (Swarm) |
| Volumes | Local | Swarm volumes |
| Replicas | 1 de cada | 2+ replicas |

## 🐛 Troubleshooting

### Porta já em uso

Se alguma porta estiver em uso, altere no arquivo `.env`:

```bash
POSTGRES_PORT=5433
PROXY_PORT=8081
MANAGER_PORT=8082
```

### Container não inicia

Verifique os logs:
```bash
docker-compose -f docker-compose.dev.yml logs [serviço]
```

### Banco de dados não conecta

Verifique se o PostgreSQL está healthy:
```bash
docker-compose -f docker-compose.dev.yml ps
```

### Rebuild completo

Para rebuild completo sem cache:
```bash
docker-compose -f docker-compose.dev.yml build --no-cache
docker-compose -f docker-compose.dev.yml up -d
```

## 📝 Notas

- Os dados do PostgreSQL são persistidos no volume `chargeflow-postgres-dev-data`
- Os builds são feitos localmente (não usa registry Docker)
- Profile Spring `dev` é usado automaticamente via `SPRING_PROFILES_ACTIVE=dev`
- Para produção, use o script `start.sh` que configura Docker Swarm e VMs
- **Cache do Maven:** Com hot reload, o cache do Maven é preservado em volumes, acelerando builds subsequentes

## ⚡ Dicas de Performance

### Hot Reload - Primeira Vez

A primeira inicialização com hot reload pode demorar 2-5 minutos porque:
- Compila todas as classes pela primeira vez
- Baixa dependências Maven
- Gera classes a partir de WSDL/XSD

**Soluções:**
- Use cache do Maven (já configurado nos volumes)
- Se precisar acelerar, rode `mvn compile` localmente antes

### Hot Reload - Mudanças Incrementais

Após a primeira compilação, mudanças incrementais são muito rápidas:
- Recompilação: ~5-10 segundos
- Restart automático: ~5-10 segundos
- **Total: ~10-20 segundos** para ver suas mudanças

### Se Hot Reload Não Funcionar

1. Verifique se está usando `docker-compose.dev-hotreload.yml`
2. Verifique os logs: `docker-compose -f docker-compose.dev-hotreload.yml logs -f [serviço]`
3. Certifique-se que o arquivo foi salvo completamente
4. Se necessário, force um restart: `docker-compose -f docker-compose.dev-hotreload.yml restart [serviço]`

## 🔗 Links Úteis

- [Documentação Docker Compose](https://docs.docker.com/compose/)
- [Spring Boot Profiles](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.profiles)
- [Flyway Migrations](https://flywaydb.org/documentation/)

