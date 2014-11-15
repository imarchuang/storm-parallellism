#!/bin/sh

#file="./topo.list"

#storm list | tail -n +4 $file_name | awk -F" " '{print $1}' > $file

if [ -z "$1" ]
then
   echo "Must provide at least one arg"
   echo "Usage: ./killTopo.sh all"
   echo "Usage: ./killTopo.sh topwords"
   exit 1
fi

file="./topo.list"

storm list | tail -n +4 $file_name | awk -F" " '{print $1}' > $file

if [ $1 = 'all' ]
then 
while IFS= read -r line
do
        # echo line is stored in $line
	echo "kill topology $line..."
        storm kill $line
done < "$file"
else

while IFS= read -r line
do
        # echo line is stored in $line
    if [ $1 = $line ]
    then     
        echo "kill topology $line..."
        storm kill $line
    fi
done < "$file"
fi
