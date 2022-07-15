program exemplo;
function max(num1, num2:integer): integer;
begin
    max := num1 + num2;
end;

var
 nome:string;
 numero1, numero2, cont: integer;
begin
    numero1 := 10;
    numero2 := 20;
    nome := 'Hello';
    cont := max(numero1, numero2);
end.