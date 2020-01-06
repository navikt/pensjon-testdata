# base image
FROM node:12.2.0-alpine as build

# set working directory
WORKDIR /app

# add `/app/node_modules/.bin` to $PATH
ENV PATH /app/node_modules/.bin:$PATH

#configure proxy for node
RUN npm config set https-proxy "http://webproxy-utvikler.nav.no:8088"
RUN npm config set proxy "http://webproxy-utvikler.nav.no:8088"
RUN npm config set registry "http://registry.npmjs.org/"
RUN npm config set strict-ssl false

# install and cache app dependencies
COPY node_modules /app/node_modules
COPY package.json /app/package.json
COPY package-lock.json /app/package-lock.json
COPY less.json /app/less.json
COPY src /app/src
COPY public /app/public


# Arguments
ARG REACT_APP_API_BASE_URL
ENV REACT_APP_API_BASE_URL=${REACT_APP_API_BASE_URL}


#RUN npm install
#RUN npm install react-scripts@3.0.0 -g

RUN npm run build

# start app
#CMD ["npm", "start"]

#### Stage 2: Serve the React application from Nginx
FROM nginx:1.17.0-alpine

# Copy the react build from Stage 1
COPY --from=build /app/build /var/www

# Copy our custom nginx config
COPY nginx.conf /etc/nginx/nginx.conf

# Expose port 80 to the Docker host, so we can access it
# from the outside.
EXPOSE 80

ENTRYPOINT ["nginx","-g","daemon off;"]