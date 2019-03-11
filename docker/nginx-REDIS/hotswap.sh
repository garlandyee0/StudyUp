#!/usr/bin/env bash
set -e
#Check if parameter is not empty
if [ "$1" != "" ]; then
#Extract and store line 11 in nginx.conf which contains the server pointing
  serverstring=$(sed -n '11p' /etc/nginx/nginx.conf)
#Store only the string before the ":" symbol
  servername=${serverstring%:*}
#Store only the string after the first 11 characters, leaving just the server name
  servername=${servername:11}
#Check if the servername is already being used  
  if [ "$1" == "$servername" ]; then
#If it is then print a warning message letting the user know
    echo "Already pointing to this server, please choose a different one"
  else
#Else replace the server name with the user input
    sed -i "s/server.*:/server $1:/g" /etc/nginx/nginx.conf
  fi
fi
#Reload nginx so that the changes can be saved
exec /usr/sbin/nginx -s reload
