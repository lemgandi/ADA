-- insert into Bout(lfencerfk,rfencerfk,start_time) values (6,2,datetime('now'));

insert into Touch(boutfk,lactionfk,ractionfk,left_scored,right_scored,touch_sequence,committed_time) values (1,
(select _id from Action_type where seq = 3),
(select _id from Action_type where seq = 1),
1,1,3,datetime('now'));

