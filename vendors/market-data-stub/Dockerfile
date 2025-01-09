FROM node:22
WORKDIR /usr/src/app
COPY package*.json index*.js ./
ARG TEST_MODE_ARG
ENV TEST_MODE_ENV=true
RUN npm install
CMD ["sh", "-c", "if [ $TEST_MODE_ENV = true ]; then echo $TEST_MODE_ENV && node index_test.js; else echo $TEST_MODE_ENV && node index.js; fi"]

EXPOSE 3080
