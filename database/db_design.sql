-- SQLite database design for ADA
-- Double-dash comments work only on a line of their own.
-- Copyright(c) Charles Shapiro July 2013
--    This program is free software: you can redistribute it and/or modify
--    it under the terms of the GNU General Public License as published by
--    the Free Software Foundation, either version 3 of the License, or
--    (at your option) any later version.
--
--    This program is distributed in the hope that it will be useful,
--    but WITHOUT ANY WARRANTY; without even the implied warranty of
--    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
--    GNU General Public License for more details.
--
--    You should have received a copy of the GNU General Public License
--    along with this program.  If not, see <http://www.gnu.org/licenses/>.

PRAGMA foreign_keys = ON;

create table Fencer (
       _id integer primary key,
       fencername text unique
);

-- seq: order in drop-down list
-- actionname: simple attack, compound attack, parry-riposte, et cetera
-- action_categoryfk: attack,defend, both

create table Action_type (
  _id integer primary key,
  seq integer not null, 
  Action_categoryfk integer references Action_category(_id),
  actionname text 
);


create table Action_category
(
   _id integer primary key,
   category text
);  

-- These lines should go when we have an action editor.

insert into Action_type(seq,actionname) values(1,'Simple Attack');
insert into Action_type(seq,actionname) values(2,'Compound Attack');
insert into Action_type(seq,actionname) values(3,'Fleche');
insert into Action_type(seq,actionname) values(4,'Parry-Riposte');
insert into Action_type(seq,actionname) values(5,'Counterattack 1');
insert into Action_type(seq,actionname) values(6,'Counterattack 2');
insert into Action_type(seq,actionname) values(7,'Counterattack 3');
insert into Action_type(seq,actionname) values(8,'Retreat');   
insert into Action_type(seq,actionname) values(9,'Stand');
insert into Action_type(seq,actionname) values(10,'Parry');

insert into Action_category(category) values('Defend');
insert into Action_category(category) values('Attack');
insert into Action_category(category) values('Both');

-- These lines must be this way because of my idiosyncratic database
-- init code

update Action_type set Action_categoryfk = (select _id from Action_category where category='Defend') where seq in (5,6,7,8,9,10);

update Action_type set Action_categoryfk = (select _id from Action_category where category='Attack') where seq in (1,2,3);

update Action_type set Action_categoryfk = (select _id from Action_category where category='Both') where seq=4;

-- Test data should go away in distributed app

insert into Fencer(fencername) values ('Alfred P Neumann');
insert into Fencer(fencername) values ('Harpo Marx');
insert into Fencer(fencername) values ('Gerald J Slurry');


create table Bout (
   _id integer primary key,
   lfencerfk integer references Fencer(_id),
   rfencerfk integer references Fencer(_id),
   start_time datetime,
   end_time datetime
);

create table Touch (
   _id integer primary key,
   boutfk integer references Bout(_id),
   lactionfk integer references Action_type(_id),
   ractionfk integer references Action_type(_id),
   left_scored boolean,
   right_scored boolean,
   touch_sequence integer,
   committed_time datetime
);
