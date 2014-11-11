#!/bin/sh
SERVICE='storm'
#ZOOKEEPER_HOME='home'
 
if ps ax | grep -v grep | grep $SERVICE > /dev/null
then
    echo "$SERVICE service running, everything is fine"
    echo "$ZOOKEEPER_HOME/bin"
    echo $STORM_HOME
else
    echo "$SERVICE is not running, now start it"
    echo $ZOOKEEPER_HOME
    sudo $ZOOKEEPER_HOME/bin/zkServer.sh start  
    storm nimbus > /dev/null
    storm supervisor > /dev/null
    storm ui > /dev/null 
fi
