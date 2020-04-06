-------------------
-- CREATE SCRIPT --
-------------------
DROP TABLE IF EXISTS q1;
CREATE TABLE q1 (original_date string);


----------------
-- TABLE DATA --
----------------
INSERT INTO q1 select stack
(
	8,
	'20190825',
	'20190826',
	'20190827',
	'20190828',
	'20190829',
	'20190830',
	'20190831',
	'20190901'
);


--------------------
-- SOLUTION QUERY --
--------------------
SELECT
	q1.original_date,
	-- End of Week, considering both cases:
	--    1. when is Sunday (day of week = 7), it will add `0` to the days;
	--    2. when it is not Sunday(day of week <> 7), it will add the complement	
	date_format(
		date_add(
			from_unixtime(unix_timestamp(q1.original_date , 'yyyyMMdd')), 
			7-cast(date_format(from_unixtime(unix_timestamp(q1.original_date , 'yyyyMMdd')), 'u') as int)
		), 
		'yyyyMMdd'
	) as end_of_week,
	-- End Of Month, using `date` functions to calculate it
	date_format(
		date_add(
			add_months(
				date_format(from_unixtime(unix_timestamp(q1.original_date , 'yyyyMMdd')), 'yyyy-MM-01'), -- formating to the day `01` of the month 
				1
			), 
			-1
		),
		'yyyyMMdd'
	) as end_of_month
FROM
	q1;


