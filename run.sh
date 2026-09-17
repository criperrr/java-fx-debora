#!/bin/bash
set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
SRC="$SCRIPT_DIR/src/java"
OUT="$SCRIPT_DIR/out"
LIB="$SCRIPT_DIR/lib"

# Na branch bob-esponja, o modo temático roda por padrão
BOB_ESPONJA=true
for arg in "$@"; do
    if [ "$arg" = "--padrao" ] || [ "$arg" = "--standard" ]; then
        BOB_ESPONJA=false
    fi
done

# Funcao de efeitos sonoros no script de build
play_sound() {
    local sound_file="$1"
    local sound_path="$SCRIPT_DIR/src/resources/com/template/spongebob/sounds/$sound_file"
    if [ ! -f "$sound_path" ]; then
        sound_path="$HOME/Downloads/$sound_file"
    fi
    if [ -f "$sound_path" ] && command -v afplay >/dev/null 2>&1; then
        afplay "$sound_path" &
    fi
}

# Tratamento de erro na build caso falhe
handle_build_error() {
    if [ "$BOB_ESPONJA" = true ]; then
        echo ""
        echo "💥 Falha na montagem do Siri Cascudo!"
        play_sound "spongebob-fail.mp3"
    fi
}
trap handle_build_error ERR

if [ "$BOB_ESPONJA" = true ]; then
    echo "======================================================="
    echo "🍍 [SIRI CASCUDO 2000] MODO BOB ESPONJA ATIVADO! 🍍"
    echo "🌊 Iniciando compilação no fundo do mar..."
    echo "======================================================="
    play_sound "sponge-stank-noise.mp3"
fi

# Diretorio de recursos (suporte a resources ou rescources)
if [ -d "$SCRIPT_DIR/src/resources" ]; then
    RES="$SCRIPT_DIR/src/resources"
elif [ -d "$SCRIPT_DIR/src/rescources" ]; then
    RES="$SCRIPT_DIR/src/rescources"
else
    RES=""
fi

# Detecta JAVA e JAVAC (prioriza Java 26 compativel com JavaFX 26 ou Java atual)
if [ -x "/usr/libexec/java_home" ]; then
    JVM_PATH="$(/usr/libexec/java_home -v 26 2>/dev/null || /usr/libexec/java_home 2>/dev/null)"
    if [ -n "$JVM_PATH" ] && [ -x "$JVM_PATH/bin/javac" ]; then
        JAVAC="$JVM_PATH/bin/javac"
        JAVA="$JVM_PATH/bin/java"
    fi
fi

if [ -z "$JAVAC" ]; then
    if [ -x "/usr/lib/jvm/java-26-openjdk/bin/javac" ]; then
        JAVAC="/usr/lib/jvm/java-26-openjdk/bin/javac"
        JAVA="/usr/lib/jvm/java-26-openjdk/bin/java"
    elif [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/javac" ]; then
        JAVAC="$JAVA_HOME/bin/javac"
        JAVA="$JAVA_HOME/bin/java"
    elif [ -x "/usr/lib/jvm/default-java/bin/javac" ]; then
        JAVAC="/usr/lib/jvm/default-java/bin/javac"
        JAVA="/usr/lib/jvm/default-java/bin/java"
    else
        JAVAC="$(command -v javac || true)"
        JAVA="$(command -v java || true)"
    fi
fi

if [ -z "$JAVAC" ] || [ ! -x "$JAVAC" ]; then
    echo "erro: javac nao encontrado"
    exit 1
fi

if [ -z "$JAVA" ] || [ ! -x "$JAVA" ]; then
    echo "erro: java nao encontrado"
    exit 1
fi

# Localiza JavaFX SDK
JAVAFX_LIB=""
POSSIBLE_FX_PATHS=(
    "$LIB/javafx-sdk"
    "$LIB/javafx-sdk/lib"
    $(ls -d "$LIB"/javafx-sdk*/lib 2>/dev/null || true)
    $(ls -d "$HOME"/Documents/javafx-sdk*/lib 2>/dev/null || true)
    $(ls -d "$HOME"/Downloads/javafx-sdk*/lib 2>/dev/null || true)
    "/usr/share/openjfx/lib"
    "/opt/javafx/lib"
)

for path in "${POSSIBLE_FX_PATHS[@]}"; do
    if [ -n "$path" ] && [ -d "$path" ]; then
        JAVAFX_LIB="$path"
        break
    fi
done

if [ -z "$JAVAFX_LIB" ]; then
    echo "erro: javafx nao encontrado em $LIB/javafx-sdk/lib ou caminhos padrao"
    echo "execute: unzip lib/javafx-sdk.zip -d lib/ e renomeie a pasta para javafx-sdk"
    exit 1
fi

# Localiza PostgreSQL Driver JAR
PG_JAR=""
POSSIBLE_PG_PATHS=(
    "$LIB/postgresql.jar"
    $(ls "$LIB"/postgresql*.jar 2>/dev/null || true)
    $(ls "$HOME"/Downloads/postgresql*.jar 2>/dev/null || true)
)

for path in "${POSSIBLE_PG_PATHS[@]}"; do
    if [ -n "$path" ] && [ -f "$path" ]; then
        PG_JAR="$path"
        break
    fi
done

if [ -z "$PG_JAR" ]; then
    echo "erro: driver postgres nao encontrado em $LIB/postgresql.jar ou ~/Downloads/"
    exit 1
fi

echo "=== Configuração ==="
echo "Java:       $JAVA"
echo "Javac:      $JAVAC"
echo "JavaFX Lib: $JAVAFX_LIB"
echo "Postgres:   $PG_JAR"
if [ "$BOB_ESPONJA" = true ]; then
    echo "Tema:       Bob Esponja (Anos 2000)"
fi
echo ""

# Classpath com todos os jars
FX_JARS=$(ls "$JAVAFX_LIB"/*.jar | tr '\n' ':')
CLASSPATH="${FX_JARS}${PG_JAR}"

echo "=== compilando ==="
mkdir -p "$OUT"

# Copia recursos para out
if [ -n "$RES" ] && [ -d "$RES" ]; then
    cp -r "$RES/." "$OUT/"
fi

# Compila todos os .java
$JAVAC \
    --module-path "$JAVAFX_LIB" \
    --add-modules javafx.controls,javafx.fxml,javafx.media \
    -cp "$CLASSPATH" \
    -d "$OUT" \
    $(find "$SRC" -name "*.java")

echo "compilado com sucesso"

if [ "$BOB_ESPONJA" = true ]; then
    play_sound "gary_meow.mp3"
fi

echo ""
echo "=== executando ==="

if [ "$BOB_ESPONJA" = true ]; then
    $JAVA \
        --module-path "$JAVAFX_LIB" \
        --add-modules javafx.controls,javafx.fxml,javafx.media \
        --enable-native-access=javafx.graphics,javafx.media \
        -cp "$OUT:$CLASSPATH" \
        com.template.Main --bob-esponja "$@"
else
    $JAVA \
        --module-path "$JAVAFX_LIB" \
        --add-modules javafx.controls,javafx.fxml,javafx.media \
        --enable-native-access=javafx.graphics,javafx.media \
        -cp "$OUT:$CLASSPATH" \
        com.template.Main "$@"
fi
