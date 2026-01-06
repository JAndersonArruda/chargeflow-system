#!/bin/bash
set -e

cd /app

echo "=========================================="
echo " Charge Manager - Dev (Hot Reload via Docker)"
echo "=========================================="
echo ""
echo "Profile: dev"
echo "Hot reload: Spring DevTools + inotify (Java/resources)"
echo "Workdir: $(pwd)"
echo ""

# Garante que o inotifywait exista (instalado no Dockerfile.dev)
if ! command -v inotifywait >/dev/null 2>&1; then
  echo "ERROR: inotifywait não encontrado no container. Verifique Dockerfile.dev."
  exit 1
fi

# Função para subir a aplicação
run_app() {
  echo "[APP] Iniciando Spring Boot (mvn spring-boot:run)..."
  mvn spring-boot:run \
    -Dspring-boot.run.profiles=dev \
    -Dspring-boot.run.fork=false \
    -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005" &

  APP_PID=$!
  echo "[APP] PID = $APP_PID"
}

# Função para monitorar mudanças e recompilar
watch_and_compile() {
  echo "[WATCH] Monitorando mudanças em src/main/java e src/main/resources..."

  # Arquivo para controlar o último compile (throttle)
  local last_file="/tmp/manager-last-compile.time"
  echo "0" > "$last_file"

  inotifywait -m -r \
    -e modify,create,delete,move \
    --format '%w%f' \
    src/main/java src/main/resources 2>/dev/null | while read FILE; do

    if [[ "$FILE" == *.java || "$FILE" == *.properties || "$FILE" == *.yml || "$FILE" == *.yaml ]]; then
      local now
      now=$(date +%s)
      local last
      last=$(cat "$last_file" 2>/dev/null || echo "0")

      # Evita recompilar várias vezes seguidas (janela de 2s)
      if [ $((now - last)) -lt 2 ]; then
        continue
      fi

      echo "[WATCH] Mudança detectada em: $FILE"
      echo "[WATCH] Recompilando (mvn compile -DskipTests)..."
      if mvn -q compile -DskipTests; then
        echo "[WATCH] OK - compile terminou, DevTools deve reiniciar a aplicação."
      else
        echo "[WATCH] ERRO ao compilar. Veja logs acima."
      fi
      echo "$now" > "$last_file"
    fi
  done
}

# Trap para limpar processos
cleanup() {
  echo ""
  echo "[SYSTEM] Encerrando..."
  [ -n "$APP_PID" ] && kill "$APP_PID" 2>/dev/null || true
  [ -n "$WATCH_PID" ] && kill "$WATCH_PID" 2>/dev/null || true
  exit 0
}

trap cleanup SIGINT SIGTERM

# Sobe app e watcher
run_app
watch_and_compile &
WATCH_PID=$!

# Espera o processo principal
wait "$APP_PID"