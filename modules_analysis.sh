# Skrypt analizuje, które moduły (katalogi) są najczęściej modyfikowane, dostarczając informacji o ogólnej strukturze projektu i kluczowych obszarach rozwoju.

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
  awk -F/ -v OFS=/ 'NF > 1 {$NF = ""; print $0 } NF <= 1 { print "." }' | \
  sed 's|/*$||' | \
  sed 's|^\\.$|project root|' | \
  sort | \
  uniq -c | \
  sort -nr | \
  head -n 10 | \
  awk '{count=$1; $1=""; sub(/^[ \t]+/, ""); print $0 ": " count " changes"}' | cat