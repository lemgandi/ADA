-- Initial load of ada sqlite3 database
delete from "Action_type";

insert into "Action_type"(seq,actionname) values (1,"Attack");
insert into "Action_type"(seq,actionname) values (2,"Defend");
insert into "Action_type"(seq,actionname) values (3,"Counterattack 1");
insert into "Action_type"(seq,actionname) values (4,"Counterattack 2");
insert into "Action_type"(seq,actionname) values (5,"Counterattack 3");
