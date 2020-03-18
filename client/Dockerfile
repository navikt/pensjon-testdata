FROM nginx:1.17.0-alpine
COPY build /var/www
COPY nginx.conf /etc/nginx/nginx.conf
ADD dumb-init /
COPY start.sh /start.sh
RUN dos2unix /start.sh

EXPOSE 80

CMD ["/dumb-init", "/start.sh"]