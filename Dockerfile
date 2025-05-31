FROM ubuntu:latest
LABEL authors="konschack"

ENTRYPOINT ["top", "-b"]