program exemplo;
var
 nome:string;
 num,cont:integer;
begin
    num := 1 + 2;
    if num < 2 then
    begin
        nome := 'If Begin';
        Writeln(nome);
    end
    else
    begin
        nome := 'Else Begin';
        Writeln(nome);
    end;
end.
