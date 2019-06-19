#!/bin/bash

docker build -t 'scraper' .
docker run --rm --network=scraper-network scraper
