select
   count(touches.actfk),
   AT.actionname
from
(
select
   TL.lactionfk as actfk 
from
   Touch as TL
inner join
   Bout as BL
on 
   TL.boutfk = BL._id
inner join
   Fencer as FL
on
   BL.lfencerfk = FL._id
where
   TL.left_scored > 0
and
   BL.lfencerfk = (select _id from Fencer where fencername = 'Alfredo Park')
union all
select
   TR.ractionfk as actfk
from
   Touch as TR
inner join
   Bout as BR
on 
   TR.boutfk = BR._id
inner join
   Fencer as FR
on
   BR.rfencerfk = FR._id
where
   TR.right_scored > 0
and
   BR.rfencerfk = (select _id from Fencer where fencername = 'Alfredo Park')
) as touches
inner join
   Action_type as AT
on
   touches.actfk = AT._id
group by
   touches.actfk,
   AT.actionname
;
