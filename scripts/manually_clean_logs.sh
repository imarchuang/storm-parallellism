#!/bin/sh

find /home/marc/Downloads/apache-storm/logs -type f -name "worker-*.log.*" -mtime +$1 -print0 | xargs -0 -I file rm -f file > /dev/null 2>&1
