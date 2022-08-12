program exemplo;
var
 i: integer;
 num: array[1..5] of integer;
begin
    i := 5;
    repeat
        i := i - 1;
        num[i] := 1;
        Writeln(num[i]);
    until i > 1;
end.