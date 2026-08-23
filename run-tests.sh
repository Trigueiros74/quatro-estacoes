#!/bin/bash
# Corre a bateria de testes automáticos contra os JARs construídos, replicando
# a comparação usada na avaliação da cadeira:
#   diff -iwub -B  sobre a saída com espaços colapsados.
#
# Uso: ./run-tests.sh [padrão]     (ex.: ./run-tests.sh 'A-19-*')

set -u

BASE=$(cd "$(dirname "$0")" && pwd)
TESTS=$BASE/tests/auto-tests
WORK=$BASE/.test-run
CP=$BASE/po-uilib/po-uilib.jar:$BASE/hva-core/hva-core.jar:$BASE/hva-app/hva-app.jar
PATTERN=${1:-'*'}

rm -rf "$WORK"
mkdir -p "$WORK"
cp "$TESTS"/*.in "$TESTS"/*.import "$WORK" 2>/dev/null
cd "$WORK" || exit 1

passed=0
failed=0
failures=()

for in_file in $(ls ${PATTERN}.in 2>/dev/null | sort); do
  name=${in_file%.in}
  expected=$TESTS/expected/$name.out
  [ -f "$expected" ] || continue

  if [ -f "$name.import" ]; then
    java -cp "$CP" -Dimport="$name.import" -Din="$in_file" -Dout="$name.outhyp" hva.app.App > "$name.stderr" 2>&1
  else
    java -cp "$CP" -Din="$in_file" -Dout="$name.outhyp" hva.app.App > "$name.stderr" 2>&1
  fi

  if [ ! -f "$name.outhyp" ]; then
    failed=$((failed + 1))
    failures+=("$name (sem saída)")
    continue
  fi

  tr '\n\t' '  ' < "$expected" | sed -e 's/  */ /g' > .expected
  tr '\n\t' '  ' < "$name.outhyp" | sed -e 's/  */ /g' > .obtained

  if diff -iwub -B .expected .obtained > /dev/null; then
    passed=$((passed + 1))
    rm -f "$name.outhyp" "$name.stderr"
  else
    failed=$((failed + 1))
    failures+=("$name")
  fi
done

total=$((passed + failed))
echo
echo "Passaram: $passed/$total"
if [ ${#failures[@]} -gt 0 ]; then
  echo "Falharam:"
  printf '  %s\n' "${failures[@]}"
fi
[ "$total" -gt 0 ] && echo "Percentagem: $(echo "scale=2; 100*$passed/$total" | bc)%"
exit $failed
