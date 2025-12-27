# Skrypt identyfikuje najaktywniejszych kontrybutorów projektu, dostarczając informacji o tym, kto najlepiej zna repozytorium i poszczególne jego obszary.

git log --since="10 year ago" --pretty=format:"%an <%ae>" --no-merges |\
  sort |\
  uniq -c |\
  sort -nr |\
  head -n 5 |\
  awk '{count=$1; $1=""; sub(/^[ \t]+/, ""); print $0 ": " count " commits"}'