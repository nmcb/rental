#!/bin/bash

rm ./db-load.log

curl -X POST -H "Content-Type: application/json" --data '{"name":"Matrix 11", "type":"NEW"}' http://localhost:8080/films >> ./db-load.log
echo >> ./db-load.log
curl -X POST -H "Content-Type: application/json" --data '{"name":"Spider Man", "type":"REG"}' http://localhost:8080/films >> ./db-load.log
echo >> ./db-load.log
curl -X POST -H "Content-Type: application/json" --data '{"name":"Out of Africa", "type":"OLD"}' http://localhost:8080/films >> ./db-load.log
echo >> ./db-load.log

curl -X POST -H "Content-Type: application/json" --data '{"name":"Duck, Kwik"}' http://localhost:8080/users >> ./db-load.log
echo >> ./db-load.log
curl -X POST -H "Content-Type: application/json" --data '{"name":"Duck, Kwek"}' http://localhost:8080/users >> ./db-load.log
echo >> ./db-load.log
curl -X POST -H "Content-Type: application/json" --data '{"name":"Duck, Kwak"}' http://localhost:8080/users >> ./db-load.log
echo >> ./db-load.log

curl -X POST -H "Content-Type: application/json" --data '{"film":{ "id":1}}' http://localhost:8080/boxes >> ./db-load.log
echo >> ./db-load.log
curl -X POST -H "Content-Type: application/json" --data '{"film":{ "id":2}}' http://localhost:8080/boxes >> ./db-load.log
echo >> ./db-load.log
curl -X POST -H "Content-Type: application/json" --data '{"film":{ "id":2}}' http://localhost:8080/boxes >> ./db-load.log
echo >> ./db-load.log
curl -X POST -H "Content-Type: application/json" --data '{"film":{ "id":3}}' http://localhost:8080/boxes >> ./db-load.log
echo >> ./db-load.log
curl -X POST -H "Content-Type: application/json" --data '{"film":{ "id":3}}' http://localhost:8080/boxes >> ./db-load.log
echo >> ./db-load.log
curl -X POST -H "Content-Type: application/json" --data '{"film":{ "id":3}}' http://localhost:8080/boxes >> ./db-load.log
echo >> ./db-load.log

echo Done