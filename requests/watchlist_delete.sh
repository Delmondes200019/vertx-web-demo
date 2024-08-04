#!/bin/bash

ACCOUNT_ID=1

curl -X DELETE "http://localhost:4444/pg/account/watchlist/$ACCOUNT_ID"

if [ ! $? -eq 0 ]
then
  echo "Error on http request"
fi


