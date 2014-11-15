#!/bin/sh

topo_path='/home/marc/Documents/storm/examples/storm-starter/'
topo_jar='target/storm-starter-0.9.3-rc2-SNAPSHOT-jar-with-dependencies.jar'

if [ -z "$1" ]
then
   echo "Must provide at least one arg"
   echo "Usage: ./submitTopo.sh all"
   echo "Usage: ./submitTopo.sh topwords"
   exit 1
fi

cd $topo_path

#topo_names=(all exclam exclam_wMetrics topwords)
#if $1 in topo_names
	if [ $1 = 'all' ]
	  then 
	    echo "submit the RollingTopWords topo..."
	    storm jar $topo_path$topo_jar storm.starter.RollingTopWords topwords remote
	    echo "submit the ExclamationTopology topo..."
	    storm jar $topo_path$topo_jar storm.starter.ExclamationTopology_wMetrics exclam_wMetrics
	else
	    echo "submit the $1 topo with args $2 $3"
	    storm jar $topo_path$topo_jar $1 $2 $3  
	fi
