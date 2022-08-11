program exemplo;
function max(num1, num2:integer): integer;
var
    num3: integer;
    nome1: string;
begin
    nome1 := 'Dentro\n';
    Writeln(nome1);
    max := num1 + num2 + num3;
end;

function min(num4, num5:integer): integer;
var
    num6: integer;
begin
    min := num4 - num5 - num6;
end;

var
 nome2:string;
 numero1, numero2, cont: integer;
begin
    nome2 := 'Antes\n';
    Writeln(nome2);
    numero1 := 10;
    numero2 := 20;
    cont := max(numero1, numero2);
    cont := min(numero1, numero2);
    nome2 := 'Antes\n';
    Writeln(nome2);
end.