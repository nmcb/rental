#!/bin/bash

rm ./checkin.log

curl -X POST -H "Content-Type: application/json" --data '{"userId":1,
  "boxIds":[1,2,3,4]
}' http://localhost:8080/boxes/checkin >> ./checkin.log
echo >> ./checkin.log

echo Done