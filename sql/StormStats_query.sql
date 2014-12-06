select * from runstate;
select * from cluster;
select * from supervisor where runid in (select max(runid) as runid from supervisor);
select * from topology where runid in (select max(runid) as runid from topology);
select * from component where runid in (select max(runid) as runid from component);
select * from executor where runid in (select max(runid) as runid from executor);
select * from stormstats where runid in (select max(runid) as runid from stormstats);

-- add find method to ExecutorDAO class to list unique host:port pairs <ubuntu:6702,ubuntu:6703> per component_id
SELECT runid, topo_name,component_id, GROUP_CONCAT(count SEPARATOR ';') AS dist_list, SUM(c) as num_executors FROM
(SELECT DISTINCT runid, topo_name,component_id, Concat(host,':',port,',',COUNT(1)) as count, COUNT(1) as c
FROM executor 
WHERE runid = 52 --IN (select max(runid) as runid from executor) -- change to variable
GROUP BY runid, topo_name,component_id, host, port
ORDER BY runid, topo_name,component_id, host, port) t
GROUP BY runid, topo_name,component_id;
-- add find method to ComponentDAO class to list unique host:port pairs <ubuntu:6702,2;ubuntu:6703,1> per topology --obsolete
-- add dist_list<ubuntu:6702,2;ubuntu:6703,1> field into component table
-- add find method to ExecutorDAO class to list # of executors per component_id

-- add calc method to StormstatsDAO class to calculate [sum of emitted/transferred/executed/acked/failed] & [avg of complete_latency/execute_latency/process_latency] per component_id/per topology
select * from stormstats where runid in (select max(runid) as runid from stormstats);

select * from stormstats where 
--runid in (select max(runid) as runid from stormstats)
topo_name = 'exclam_wAcking' and component_id = 'exclaim1' and executor_id ='4-7'
--and level = 'Topology';
and level = 'Executor'
--and level = 'Component';
and window = '600'

SELECT runid, topo_name, component_id, window, sum(emitted) as emitted, 
sum(transferred) as transferred, sum(executed) as executed, sum(acked) as acked, sum(failed) as failed,
--sum(complete_latency*emitted)/sum(emitted)
sum(complete_latency*emitted)/sum(emitted) as complete_latency, sum(execute_latency*executed)/sum(executed) as execute_latency,
sum(process_latency*executed)/sum(executed) as process_latency, sum(capacity*executed)/sum(executed) as capacity
FROM stormstats 
WHERE runid in (select max(runid) as runid from stormstats)
AND Level = 'Executor'
AND component_id <> '__acker'
AND SUBSTR(component_id,1,9) <> '__metrics'--__metricsbacktype.storm.metric.LoggingMetricsConsumer
GROUP BY runid, topo_name, component_id, window;

SELECT runid, topo_name, window, sum(emitted) as emitted, 
sum(transferred) as transferred, sum(executed) as executed, sum(acked) as acked, sum(failed) as failed,
avg(complete_latency) as complete_latency, avg(execute_latency) as execute_latency,
avg(process_latency) as process_latency, avg(capacity) as capacity
FROM stormstats 
WHERE runid in (select max(runid) as runid from stormstats)
AND Level = 'Component'
GROUP BY runid, topo_name, window;

SELECT SUBSTRING('__metricsbacktype.storm.metric.LoggingMetricsConsumer',1,9);
select runid, state, startdatetime from runstate 
where runid < (select max(runid) from runstate) - 80;
