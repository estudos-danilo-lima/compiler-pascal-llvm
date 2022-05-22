program ifProgram;

var
{ local variable declaration }
   a:integer;

begin
   a:= 10;
   (* check the boolean condition using if statement *)
   
   if( a < 20) then
      (* if condition is true then print the following *) 
      writeln('a is less than 20 ' );
   writeln('value of a is : ', a);
end.
