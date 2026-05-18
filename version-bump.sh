#!/bin/bash

# version-bump.sh
# Автоматичний скрипт для апдейту версії у gradle.properties
# 
# Використання:
#   ./version-bump.sh patch   # 1.0.0 → 1.0.1
#   ./version-bump.sh minor   # 1.0.1 → 1.1.0
#   ./version-bump.sh major   # 1.1.0 → 2.0.0
#
# На Windows (PowerShell):
#   bash version-bump.sh [major|minor|patch]

set -e

GRADLE_PROPS="gradle.properties"

# Перевірити що файл існує
if [ ! -f "$GRADLE_PROPS" ]; then
    echo "❌ Помилка: файл $GRADLE_PROPS не знайдено!"
    exit 1
fi

# Читання поточної версії з gradle.properties
read_version() {
    local key=$1
    grep "^${key}=" "$GRADLE_PROPS" | cut -d'=' -f2
}

MAJOR=$(read_version "VERSION_MAJOR")
MINOR=$(read_version "VERSION_MINOR")
PATCH=$(read_version "VERSION_PATCH")

if [ -z "$MAJOR" ] || [ -z "$MINOR" ] || [ -z "$PATCH" ]; then
    echo "❌ Помилка: не вдалося прочитати версію з $GRADLE_PROPS"
    echo "Переконайтесь що файл містить:"
    echo "  VERSION_MAJOR=X"
    echo "  VERSION_MINOR=Y"
    echo "  VERSION_PATCH=Z"
    exit 1
fi

# Визначення типу апдейту
BUMP_TYPE=${1:-patch}

case $BUMP_TYPE in
    major)
        MAJOR=$((MAJOR + 1))
        MINOR=0
        PATCH=0
        ;;
    minor)
        MINOR=$((MINOR + 1))
        PATCH=0
        ;;
    patch)
        PATCH=$((PATCH + 1))
        ;;
    *)
        echo "❌ Невідомий тип апдейту: $BUMP_TYPE"
        echo ""
        echo "Використання: ./version-bump.sh [major|minor|patch]"
        echo ""
        echo "Приклади:"
        echo "  ./version-bump.sh patch    # Bump patch version"
        echo "  ./version-bump.sh minor    # Bump minor version"
        echo "  ./version-bump.sh major    # Bump major version"
        exit 1
        ;;
esac

NEW_VERSION="$MAJOR.$MINOR.$PATCH"
NEW_VERSION_CODE=$((MAJOR * 10000 + MINOR * 100 + PATCH))

echo "📝 Оновлення версії..."
echo "  Від: $(read_version VERSION_MAJOR).$(read_version VERSION_MINOR).$(read_version VERSION_PATCH)"
echo "  На:  $NEW_VERSION"

# Оновлення gradle.properties
# Використання sed для in-place редагування
if [[ "$OSTYPE" == "darwin"* ]]; then
    # macOS
    sed -i '' "s/^VERSION_MAJOR=.*/VERSION_MAJOR=$MAJOR/" "$GRADLE_PROPS"
    sed -i '' "s/^VERSION_MINOR=.*/VERSION_MINOR=$MINOR/" "$GRADLE_PROPS"
    sed -i '' "s/^VERSION_PATCH=.*/VERSION_PATCH=$PATCH/" "$GRADLE_PROPS"
    sed -i '' "s/^VERSION_NAME=.*/VERSION_NAME=$NEW_VERSION/" "$GRADLE_PROPS"
    sed -i '' "s/^VERSION_CODE=.*/VERSION_CODE=$NEW_VERSION_CODE/" "$GRADLE_PROPS"
else
    # Linux / Windows (Git Bash)
    sed -i "s/^VERSION_MAJOR=.*/VERSION_MAJOR=$MAJOR/" "$GRADLE_PROPS"
    sed -i "s/^VERSION_MINOR=.*/VERSION_MINOR=$MINOR/" "$GRADLE_PROPS"
    sed -i "s/^VERSION_PATCH=.*/VERSION_PATCH=$PATCH/" "$GRADLE_PROPS"
    sed -i "s/^VERSION_NAME=.*/VERSION_NAME=$NEW_VERSION/" "$GRADLE_PROPS"
    sed -i "s/^VERSION_CODE=.*/VERSION_CODE=$NEW_VERSION_CODE/" "$GRADLE_PROPS"
fi

echo "✅ Версія оновлена на: $NEW_VERSION (код: $NEW_VERSION_CODE)"

# Опціонально: зробити git commit (якщо git ini)
if command -v git &> /dev/null && git rev-parse --git-dir > /dev/null 2>&1; then
    read -p "📤 Додати до git та зробити commit? (y/n) " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        git add "$GRADLE_PROPS"
        git commit -m "chore: bump version to $NEW_VERSION"
        echo "✅ Commit створено: chore: bump version to $NEW_VERSION"
        
        # Опціонально: створити tag
        read -p "🏷️  Створити git tag? (y/n) " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            git tag -a "v$NEW_VERSION" -m "Release version $NEW_VERSION"
            echo "✅ Tag створено: v$NEW_VERSION"
            echo ""
            echo "💡 Щоб запушити на GitHub:"
            echo "   git push origin main --tags"
        fi
    fi
fi

echo ""
echo "✨ Done!"
