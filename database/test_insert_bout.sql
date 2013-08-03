-- insert into Bout(lfencerfk,rfencerfk,start_time) values (
-- (select _id from Fencer where fencername = 'Harpo Marx'),
-- (select _id from Fencer where fencername = 'Gerald Slurry'),
-- datetime());
-- insert into Bout(lfencerfk,rfencerfk,start_time)
-- values (3,4,datetime());

select _id,start_time from Bout order by start_time DESC limit 1;

