# syntax=docker/dockerfile:1.4

FROM --platform=$BUILDPLATFORM node:18-bullseye-slim

RUN mkdir /project
WORKDIR /project

RUN npm install -g @angular/cli@15

COPY package.json package-lock.json ./
RUN npm ci

COPY . .
CMD ["ng", "serve", "--host", "0.0.0.0"]

