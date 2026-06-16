#!/usr/bin/env bash
# compila e executa o projeto javafx + postgres
# uso: ./run.sh

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
SRC="$SCRIPT_DIR/src/java"
RES="$SCRIPT_DIR/src/rescources"
OUT="$SCRIPT_DIR/out"
LIB="$SCRIPT_DIR/lib"

JAVAC="/usr/lib/jvm/java-26-openjdk/bin/javac"
JAVA="/usr/lib/jvm/java-26-openjdk/bin/java"
JAVAFX_LIB="$LIB/javafx-sdk/lib"
PG_JAR="$LIB/postgresql.jar"

# verifica dependencias
if [ ! -d "$JAVAFX_LIB" ]; then
    echo "erro: javafx nao encontrado em $JAVAFX_LIB"
    echo "execute: unzip lib/javafx-sdk.zip -d lib/ e renomeie a pasta para javafx-sdk"
    exit 1
fi

if [ ! -f "$PG_JAR" ]; then
    echo "erro: driver postgres nao encontrado em $PG_JAR"
    exit 1
fi

# classpath com todos os jars
FX_JARS=$(ls "$JAVAFX_LIB"/*.jar | tr '\n' ':')
CLASSPATH="${FX_JARS}${PG_JAR}"

echo "=== compilando ==="
mkdir -p "$OUT"

# copia recursos para out
cp -r "$RES/." "$OUT/"

# compila todos os .java
$JAVAC \
    --module-path "$JAVAFX_LIB" \
    --add-modules javafx.controls,javafx.fxml \
    -cp "$CLASSPATH" \
    -d "$OUT" \
    $(find "$SRC" -name "*.java")

echo "compilado com sucesso"
echo ""
echo "=== executando ==="

$JAVA \
    --module-path "$JAVAFX_LIB" \
    --add-modules javafx.controls,javafx.fxml \
    -cp "$OUT:$CLASSPATH" \
    com.template.Main
