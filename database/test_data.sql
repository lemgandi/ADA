pragma foreign_keys = on; 

delete from Touch;
delete from Bout;
delete from Fencer;

insert into Fencer(fencername) values ('Amanda Fandaganda');
insert into Fencer(fencername) values ('Alfredo Park');

insert into Bout(rfencerfk,lfencerfk,start_time)
values (
(select _id from Fencer where fencername='Amanda Fandaganda' limit 1),
(select _id from Fencer where fencername='Alfredo Park' limit 1),
datetime('now')
);
-- L 1 R 0
insert into Touch(boutfk,lactionfk,ractionfk,left_scored,right_scored,
touch_sequence,committed_time) values (
(select _id from bout order by start_time DESC limit 1),
(select _id from Action_type where actionname='Simple Attack'),
(select _id from Action_type where actionname='Parry-Riposte'),
1,
0,
1,
datetime('now','+38 seconds')
);
-- L 2 R 1 
insert into Touch(boutfk,lactionfk,ractionfk,left_scored,right_scored,
touch_sequence,committed_time) values (
(select _id from bout order by start_time DESC limit 1),
(select _id from Action_type where actionname='Simple Attack'),
(select _id from Action_type where actionname='Simple Attack'),
1,
1,
2,
datetime('now','+1 minutes')
);
-- L 2 R 2
insert into Touch(boutfk,lactionfk,ractionfk,left_scored,right_scored,
touch_sequence,committed_time) values (
(select _id from bout order by start_time DESC limit 1),
(select _id from Action_type where actionname='Simple Attack'),
(select _id from Action_type where actionname='Simple Attack'),
0,
1,
3,
datetime('now','+2 minutes')
);
-- L 2 R 3 
insert into Touch(boutfk,lactionfk,ractionfk,left_scored,right_scored,
touch_sequence,committed_time) values (
(select _id from bout order by start_time DESC limit 1),
(select _id from Action_type where actionname='Simple Attack'),
(select _id from Action_type where actionname='Parry-Riposte'),
0,
1,
4,
datetime('now','+195 seconds')
);
-- L 2 R 4
insert into Touch(boutfk,lactionfk,ractionfk,left_scored,right_scored,
touch_sequence,committed_time) values (
(select _id from bout order by start_time DESC limit 1),
(select _id from Action_type where actionname='Simple Attack'),
(select _id from Action_type where actionname='Counterattack 2'),
0,
1,
5,
datetime('now','+290 seconds')
);

-- L 3 R 5
insert into Touch(boutfk,lactionfk,ractionfk,left_scored,right_scored,
touch_sequence,committed_time) values (
(select _id from bout order by start_time DESC limit 1),
(select _id from Action_type where actionname='Simple Attack'),
(select _id from Action_type where actionname='Counterattack 1'),
1,
1,
6,
datetime('now','+340 seconds')
);

update Bout set end_time = datetime('now','+360 seconds') where _id =
(select _id from Bout order by start_time DESC limit 1);

----------------------------
insert into Bout(rfencerfk,lfencerfk,start_time)
values (
(select _id from Fencer where fencername='Alfredo Park' limit 1),
(select _id from Fencer where fencername='Amanda Fandaganda' limit 1),
datetime('now','+10 minutes')
);
-- L 1 R 0
insert into Touch(boutfk,lactionfk,ractionfk,left_scored,right_scored,
touch_sequence,committed_time) values (
(select _id from bout order by start_time DESC limit 1),
(select _id from Action_type where actionname='Simple Attack'),
(select _id from Action_type where actionname='Parry-Riposte'),
1,
0,
1,
datetime('now','+645 seconds')
);
-- L 2 R 1 
insert into Touch(boutfk,lactionfk,ractionfk,left_scored,right_scored,
touch_sequence,committed_time) values (
(select _id from bout order by start_time DESC limit 1),
(select _id from Action_type where actionname='Counterattack 1'),
(select _id from Action_type where actionname='Simple Attack'),
1,
1,
2,
datetime('now','+680 seconds')
);
-- L 2 R 2
insert into Touch(boutfk,lactionfk,ractionfk,left_scored,right_scored,
touch_sequence,committed_time) values (
(select _id from bout order by start_time DESC limit 1),
(select _id from Action_type where actionname='Simple Attack'),
(select _id from Action_type where actionname='Retreat'),
1,
0,
3,
datetime('now','+12 minutes')
);
-- L 2 R 3 
insert into Touch(boutfk,lactionfk,ractionfk,left_scored,right_scored,
touch_sequence,committed_time) values (
(select _id from bout order by start_time DESC limit 1),
(select _id from Action_type where actionname='Simple Attack'),
(select _id from Action_type where actionname='Counterattack 3'),
0,
1,
4,
datetime('now','+745 seconds')
);
-- L 2 R 4
insert into Touch(boutfk,lactionfk,ractionfk,left_scored,right_scored,
touch_sequence,committed_time) values (
(select _id from bout order by start_time DESC limit 1),
(select _id from Action_type where actionname='Simple Attack'),
(select _id from Action_type where actionname='Counterattack 2'),
0,
1,
5,
datetime('now','+14 minutes')
);

-- L 3 R 5
insert into Touch(boutfk,lactionfk,ractionfk,left_scored,right_scored,
touch_sequence,committed_time) values (
(select _id from bout order by start_time DESC limit 1),
(select _id from Action_type where actionname='Simple Attack'),
(select _id from Action_type where actionname='Counterattack 1'),
1,
1,
6,
datetime('now','+888 seconds')
);

update Bout set end_time = datetime('now','+902 seconds') where _id =
(select _id from Bout order by start_time DESC limit 1);
