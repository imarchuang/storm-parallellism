use stormstats;

drop table runstate;
CREATE TABLE runstate (runid integer NOT NULL AUTO_INCREMENT, state VARCHAR(16) not null, 
plandatetime DATETIME not null, startdatetime DATETIME, 
enddatetime DATETIME,
PRIMARY KEY (runid));

INSERT INTO runstate (state, plandatetime) VALUES ('PLANNED', NOW());
UPDATE runstate SET state='RUNNING', startdatetime=NOW() WHERE runid = &runid;
UPDATE runstate SET state='FINISHED', enddatetime=NOW() WHERE runid = &runid;
INSERT INTO runstate (state, plandatetime) VALUES ('PLANNED', NOW());
SELECT * from runstate where runid = max(runid);
SELECT max(runid) from runstate;

drop table cluster;
CREATE TABLE cluster (runid integer NOT NULL, id integer NOT NULL, version VARCHAR(50) not null, nimbus_uptime integer not null, 
supervisors integer not null, used_slots integer not null, free_slots integer not null, 
total_slots integer not null, executors integer not null, tasks integer not null, datetime DATETIME not null, 
PRIMARY KEY (runid,id));

drop table supervisor;
CREATE TABLE supervisor (runid integer NOT NULL, sup_id VARCHAR(100), host VARCHAR(20) not null, supervisor_uptime integer not null, 
slots integer not null, used_slots integer not null, datetime DATETIME not null,
PRIMARY KEY (runid,sup_id));

drop table nimbus;
CREATE TABLE nimbus ( runid integer NOT NULL, K1 VARCHAR(100), V1 VARCHAR(100), datetime DATETIME not null, 
PRIMARY KEY (runid,K1));

drop table topology;
CREATE TABLE topology (runid integer NOT NULL, name VARCHAR(32), topo_id VARCHAR(50) not null, status VARCHAR(16) not null,
topo_uptime integer not null, 
num_workers integer not null, num_executors integer not null, num_tasks integer not null, datetime DATETIME not null, 
PRIMARY KEY (runid,name));

drop table topoconfig;
CREATE TABLE topoconfig (runid integer NOT NULL, topo_name VARCHAR(32), K1 VARCHAR(100), V1 VARCHAR(100), 
datetime DATETIME not null, 
PRIMARY KEY (runid, topo_name, K1));

drop table component;
CREATE TABLE component (runid integer NOT NULL, topo_name VARCHAR(32), component_id VARCHAR(32), category VARCHAR(16) not null, 
seq integer, from_component VARCHAR(32), to_component VARCHAR(32), ip_grouping VARCHAR(32), op_grouping VARCHAR(32), 
ip_fields VARCHAR(64), op_fields VARCHAR(64), 
num_executors integer not null, num_tasks integer not null, dist_list VARCHAR(255), datetime DATETIME not null,
PRIMARY KEY (runid, topo_name, component_id)
);
ALTER TABLE component MODIFY component_id VARCHAR(255);
ALTER TABLE component ADD dist_list VARCHAR(255) after num_tasks;

drop table executor;
CREATE TABLE executor (runid integer NOT NULL, topo_name VARCHAR(32), component_id VARCHAR(32), executor_id VARCHAR(32), 
category VARCHAR(16) not null, host VARCHAR(16) not null, port VARCHAR(16) not null, 
executor_uptime integer not null, datetime DATETIME not null,
PRIMARY KEY (runid, topo_name, component_id, executor_id)
);
ALTER TABLE executor MODIFY component_id VARCHAR(255);

drop table stormstats;
CREATE TABLE stormstats (runid integer NOT NULL, topo_name VARCHAR(32), component_id VARCHAR(32), executor_id VARCHAR(32), 
level VARCHAR(16) not null, window VARCHAR(16) not null, 
emitted BIGINT, transferred BIGINT, complete_latency DOUBLE(16,16), acked BIGINT, failed BIGINT,
capacity DOUBLE(6,6),  execute_latency DOUBLE(16,16), process_latency DOUBLE(16,16), executed BIGINT,
datetime DATETIME not null, 
PRIMARY KEY (runid, topo_name, component_id, executor_id, level, window)
);
ALTER TABLE stormstats ADD column_name DATETIME not null;
ALTER TABLE stormstats CHANGE column_name datetime DATETIME not null;
ALTER TABLE stormstats MODIFY component_id VARCHAR(255);
ALTER TABLE stormstats MODIFY process_latency DOUBLE(30,26);
ALTER TABLE stormstats MODIFY execute_latency DOUBLE(30,26);
ALTER TABLE stormstats MODIFY complete_latency DOUBLE(30,26);
ALTER TABLE stormstats MODIFY capacity DOUBLE(20,16);

