FROM alpine:3.12.0

LABEL maintainer="boolivar@gmail.com"
LABEL description="heroku tgreminder notification image"

RUN apk add --no-cache postgresql-client

ADD script.sh /home/script.sh

CMD ["/home/script.sh"]