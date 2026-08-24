// Escreve o relatório de um ano do cenário, obtido através da ponte exportada.
//
// A saída é comparada com a que a JVM produz. Passar pela ponte, e não pelo
// `main`, faz com que uma só comparação prove as duas coisas: que o domínio
// atravessou intacto e que a API exportada devolve o que devia.

const [, , modulePath] = process.argv;
const { Hotel } = await import(modulePath);

process.stdout.write(Hotel.getYearReport());
