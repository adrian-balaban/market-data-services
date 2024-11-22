FROM node:22
WORKDIR /usr/src/app
COPY package*.json index.js ./
RUN npm install
EXPOSE 3080
CMD ["node", "index.js"]