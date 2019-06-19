#!/bin/bash

docker build -t 'scraper' .
docker run --rm --network=untappd-scraper_monitoring scraper
