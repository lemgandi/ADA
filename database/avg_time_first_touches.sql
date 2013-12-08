select
 Won.touch_count as win_count,
 Won.avg_time / 60 as win_avg_min,
 Won.avg_time % 60  as win_avg_sec,
 Lost.touch_count as lost_count,
 Lost.avg_time / 60 as lost_avg_min,
 Lost.avg_time % 60 as lost_avg_sec
from
(
select
   count(tc._id) as touch_count,
   cast (avg(strftime('%s',tc.committed_time) - strftime('%s',bt.start_time)) as integer) as avg_time
from
   Bout as bt
inner join 
   Touch as tc
on
   tc.boutfk = bt._id
inner join
   Fencer as fc
on
   (bt.lfencerfk = fc._id)
or
   (bt.rfencerfk = fc._id)
where
   tc.touch_sequence = 1
and
(
(
   left_scored = 1
and
   bt.lfencerfk = fc._id
)
or
(
   right_scored = 1
and
   bt.rfencerfk = fc._id
)
)
and
fc.fencername='Alfred P Neumann'
) as Won,
(
select
   count(tc._id) as touch_count,
   cast( avg(strftime('%s',tc.committed_time) - strftime('%s',bt.start_time)) as integer) as avg_time
from
   Bout as bt
inner join 
   Touch as tc
on
   tc.boutfk = bt._id
inner join
   Fencer as fc
on
   (bt.lfencerfk = fc._id)
or
   (bt.rfencerfk = fc._id)
where
   tc.touch_sequence = 1
and
(
(
   left_scored = 0
and
   bt.lfencerfk = fc._id
)
or
(
   right_scored = 0
and
   bt.rfencerfk = fc._id
)
)
and
fc.fencername='Alfred P Neumann'
) as Lost
;
