program exemplo;
var

(* Array não esta gerando o subrange *)
 i: integer;
 num: array[1..10] of integer;
begin
    i := 10;
    repeat
        i := i - 1;
    until i > 1;
end.