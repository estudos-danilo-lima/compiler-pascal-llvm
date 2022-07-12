program exemplo;
var
 nome:string;
 num,cont:integer;
begin
    nome := 'Hello';
    num := 1 + 2;
    cont := 3;
    repeat
        num := num - 1;
        cont := cont + 1;
    until num > 0;
end.
