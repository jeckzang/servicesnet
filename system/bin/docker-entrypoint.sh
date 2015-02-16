#!/bin/bash
set -e
if [ "$1" = 'all' ]; then
gosu redis redis-server /redis/redis.conf &
service rabbitmq-server start &
exec /bin/sh
fi
exec "$@"
