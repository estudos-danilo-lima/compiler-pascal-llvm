program exemplo;
function max(num1, num2:integer): integer;
var
    num3: integer;
begin
    max := num1 + num2 + num3;
end;

function min(num4, num5:integer): integer;
var
    num6: integer;
begin
    min := num4 - num5 - num6;
end;

var
 nome:string;
 numero1, numero2, cont: integer;
begin
    numero1 := 10;
    numero2 := 20;
    nome := 'Hello';
    cont := max(numero1, numero2);
    cont := min(numero1, numero2);
end.