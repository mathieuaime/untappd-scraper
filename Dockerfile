FROM ubuntu:latest

RUN apt-get update && apt-get -y install cron docker.io

COPY scraper-cron /etc/cron.d/scraper-cron

COPY target/*with-dependencies.jar /tmp/scraper.jar

COPY Dockerfile.scraper /tmp/Dockerfile

# Give execution rights on the cron job
RUN chmod 0644 /etc/cron.d/scraper-cron

# Apply cron job
RUN crontab /etc/cron.d/scraper-cron

# Create the log file to be able to run tail
RUN touch /var/log/cron.log

# Run the command on container startup
CMD cron && tail -f /var/log/cron.log