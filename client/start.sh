#!/bin/sh
echo "Initialize NGINX"
echo "Using ENV: ${SERVER_HOSTNAME}"
sed -i "s#SERVER_HOSTNAME#${SERVER_HOSTNAME}#g" /etc/nginx/nginx.conf

nginx -g "daemon off;"

