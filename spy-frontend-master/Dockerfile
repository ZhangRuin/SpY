FROM node:lts-alpine as build-stage
RUN rm -rf /usr/share/nginx/*
COPY package*.json ./
ARG BUILD_DIR=./dist
RUN ls .
RUN ls -la /etc/nginx/conf.d
COPY $BUILD_DIR /usr/share/nginx
COPY ./nginx.conf /default.conf

## production stage
FROM nginx:stable-alpine as production-stage
#COPY --from=build-stage /app/dist /usr/share/nginx/html
EXPOSE 5174
#RUN chown -R nginx:nginx /usr/share/nginx/dist
CMD ["nginx", "-g", "daemon off;"]