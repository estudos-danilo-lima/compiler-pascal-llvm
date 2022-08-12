program exemplo;
var
 num:integer;
begin
    num := 4;
    repeat
        num := num - 1;
        Writeln('Numero: ', num);
    until num > 0;
end.
