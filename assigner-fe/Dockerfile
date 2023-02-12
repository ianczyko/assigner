FROM node:19.6.0 as build-env

WORKDIR /assigner
COPY package.json package-lock.json ./
RUN npm install --omit=dev
RUN npm install install react-scripts@5.0.1 -g 
COPY . ./
RUN npm run build

FROM nginx
COPY --from=build-env /assigner/build /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

ENTRYPOINT envsubst '$BE_ADDRESS $BE_PORT' \
    < /etc/nginx/conf.d/default.conf \
    > /tmpdefault && mv /tmpdefault /etc/nginx/conf.d/default.conf && \
    cat /etc/nginx/conf.d/default.conf && \
    nginx -g 'daemon off;'
