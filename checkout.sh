#!/bin/bash

rm ./checkout.log

curl -X POST -H "Content-Type: application/json" --data '{"userId":1, "items":[
  {"boxId":1, "nrOfDays":1},
  {"boxId":2, "nrOfDays":5},
  {"boxId":3, "nrOfDays":2},
  {"boxId":4, "nrOfDays":7}
]}' http://localhost:8080/boxes/checkout >> ./checkout.log
echo >> ./checkout.log

echo Done