#!/bin/bash

maxsize=500M
dir=/tmp/zookeeper/version-2

for file in `find $dir -type f -name "log.*" -size +$maxsize -print0`
do
        fileSize=$(du -k "$file" | cut -f1)
        #if [ fileSize -ge $maxsize ]
	#then
                echo "Empty this oversize file = $file with size = $fileSize"
                cat /dev/null > $file
        #fi
done

emptyFiles=`find $dir -type f -name "log.*" -size 0`
list=${#emptyFiles}
if [ $list -gt 0 ]
then 
	echo "Remove the following empty files: $emptyFiles"
	find $dir -type f -name "log.*" -size 0 -exec rm '{}' +
fi
