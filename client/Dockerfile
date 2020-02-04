FROM node:12.2.0-alpine as build

WORKDIR /app

ENV PATH /app/node_modules/.bin:$PATH

#### Stage 1: Build react application
# install and cache app dependencies
COPY node_modules /app/node_modules
COPY package.json /app/package.json
COPY package-lock.json /app/package-lock.json
COPY less.json /app/less.json
COPY src /app/src
COPY public /app/public

RUN npm run build

#### Stage 2: Serve the React application from Nginx
FROM nginx:1.17.0-alpine
COPY --from=build /app/build /var/www
COPY nginx.conf /etc/nginx/nginx.conf
ADD dumb-init /
COPY start.sh /start.sh
#ENV SERVER_HOSTNAME $SERVER_HOSTNAME
EXPOSE 80

CMD ["/dumb-init", "/start.sh"]