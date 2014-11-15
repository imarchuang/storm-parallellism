#!/bin/sh

topo_path='~/Documents/storm/examples/storm-starter'
topo_jar='target/storm-starter-0.9.3-rc2-SNAPSHOT-jar-with-dependencies.jar'

arg1="all"

if [ $1 = $arg1 ];
then 
    echo "submit the RollingTopWords topo..."
    echo "submit the ExclamationTopology topo..."
else
    echo "submit the $1 topo with args $2 $3"
fi 


