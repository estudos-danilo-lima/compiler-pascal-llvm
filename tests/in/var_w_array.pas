(* ERRO Semântico *)
(* Tipo do index do array não é int *)
program exemplo;
var
 i: integer;
 num: array[1..10] of integer;
begin
    i := 10;
    repeat
        i := i - 1;
        num['a'] := 1;
    until i > 1;
end.