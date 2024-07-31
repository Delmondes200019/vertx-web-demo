#!/bin/bash

ACCOUNT_ID=1

curl -X PUT "http://localhost:4444/pg/account/watchlist/$ACCOUNT_ID" \
  -H "Content-Type: application/json" \
  -d "{ \"assets\": [ { \"symbol\": \"AMZN\", \"symbol\": \"TSLA\" } ] }"

if [ ! $? -eq 0 ]
then
  echo "Error on http request"
fi


