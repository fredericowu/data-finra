
----------------
-- UDAF SETUP --
----------------
add jar /tmp/target/fred-hive-0.1.jar;
DROP FUNCTION timeinterval;
CREATE FUNCTION timeinterval AS 'com.fred.platform.hive.udaf.TimeInterval';

-------------------
-- CREATE SCRIPT --
-------------------
DROP TABLE IF EXISTS q2;
CREATE TABLE q2 (id string, start_time string, end_time string);


----------------
-- TABLE DATA --
----------------
INSERT INTO q2 select stack
(
	13,
	'100', '10:00', '12:00', 
	'100', '10:15', '12:30',
	'100', '12:15', '12:45', 
	
	'100', '13:00', '14:00',
	
	'200', '10:15', '10:30',
	
	'400', '00:00', '23:58',
	'400', '01:00', '23:58',	
	'400', '23:59', '23:59',
	
	'500', '10:00', '11:00',	
	'500', '10:30', '11:30',	
	'500', '12:00', '13:00',	
	'500', '11:31', '12:00',	
	'500', '11:30', '11:31'	
);

--------------------
-- SOLUTION QUERY --
--------------------
SELECT
	id,
	start_time,
	end_time,
	timeinterval(id, start_time, end_time) as group_id
FROM
	q2
GROUP BY
	id, start_time, end_time
