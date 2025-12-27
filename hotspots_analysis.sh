# Skrypt identyfikuje najczęściej modyfikowane pliki w repozytorium w ciągu ostatniego roku, co pozwala określić hot spoty kodu - miejsca, gdzie zachodzi najwięcej zmian i które mogą wymagać szczególnej uwagi

# Wykluczenie plików konfiguracyjnych
#EXCLUDE_PATTERN_GREP='(\.yml$|\.yaml$|\.config\.js$)'

# Wykluczenie testów i dokumentacji
#EXCLUDE_PATTERN_GREP='(test|spec|docs?/)'

# Wykluczenie plików z node_modules i build
#EXCLUDE_PATTERN_GREP='(node_modules|dist|build|\.gitignore)'

# Złożony wzorzec - wyklucza wiele typów plików
#EXCLUDE_PATTERN_GREP='(\.svg$|\.png$|\.jpg$|package-lock\.json|yarn\.lock|\.md$)'

# Użycie w skrypcie
EXCLUDE_PATTERN_GREP='(\.yml$|\.yaml$|\.config\.js$)'

git log --since="10 year ago" --pretty=format:"" --name-only --no-merges | \
  grep -vE "${EXCLUDE_PATTERN_GREP:-^$}" | \
  grep '.' | \
  sort | \
  uniq -c | \
  sort -nr | \
  head -n 10 | \
  awk '{count=$1; $1=""; sub(/^[ \t]+/, ""); print $0 ": " count " changes"}' | ca\t