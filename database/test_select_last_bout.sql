select 
   _id,
   start_time 
from 
  Bout 
where 
lfencerfk = (select _id from Fencer where fencername = 'Harpo Marx') 
and 
rfencerfk = (select _id from Fencer where fencername = 'Gerald Slurry') 
and 
end_time is null 
order by start_time DESC limit 1;


