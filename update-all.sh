#! /bin/bash

cd consumer 
mvn clean compile jib:dockerBuild
cd ../producer
mvn clean compile jib:dockerBuild
cd ..
docker-compose up -d