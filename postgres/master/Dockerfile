FROM postgres:13-alpine

COPY ./setup-master.sh /docker-entrypoint-initdb.d/setup-master.sh
COPY ./docker-entrypoint-initdb.d /docker-entrypoint-initdb.d

RUN chmod 0666 /docker-entrypoint-initdb.d/setup-master.sh