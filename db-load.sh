#!/bin/bash

rm ./db-load.log

curl -X POST -H "Content-Type: application/json" --data '{"name":"Matrix 11", "type":"NEW"}' http://localhost:8080/films >> ./db-load.log
curl -X POST -H "Content-Type: application/json" --data '{"name":"Spider Man", "type":"REG"}' http://localhost:8080/films >> ./db-load.log
curl -X POST -H "Content-Type: application/json" --data '{"name":"Spider Man 2", "type":"REG"}' http://localhost:8080/films >> ./db-load.log
curl -X POST -H "Content-Type: application/json" --data '{"name":"Out of Africa", "type":"OLD"}' http://localhost:8080/films >> ./db-load.log
echo >> ./db-load.log

curl -X POST -H "Content-Type: application/json" --data '{"name":"Duck, Kwik"}' http://localhost:8080/users >> ./db-load.log
curl -X POST -H "Content-Type: application/json" --data '{"name":"Duck, Kwek"}' http://localhost:8080/users >> ./db-load.log
curl -X POST -H "Content-Type: application/json" --data '{"name":"Duck, Kwak"}' http://localhost:8080/users >> ./db-load.log
echo >> ./db-load.log

curl -X POST -H "Content-Type: application/json" --data '{"userId":1, "items":[
  {"filmId":1, "nrOfDays":1},
  {"filmId":2, "nrOfDays":5},
  {"filmId":3, "nrOfDays":2},
  {"filmId":4, "nrOfDays":7}
]}' http://localhost:8080/rentals >> ./db-load.log
echo >> ./db-load.log

curl -X PUT -H "Content-Type: application/json" --data '{"userId":1, "filmIds":[1,2]}' http://localhost:8080/rentals >> ./db-load.log
echo >> ./db-load.log

echo Done