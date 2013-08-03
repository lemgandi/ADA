select
   ATR._id,
   ATR.actionname,
   count(ATR.actionname) as act_count
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
inner join
   Action_type as ATR
on
   TR.ractionfk = ATR._id
where
   TR.right_scored > 0
and
   BR.rfencerfk = (select _id from Fencer where fencername = 'Alfredo Park')
group by
   ATR._id, 
   ATR.actionname
;