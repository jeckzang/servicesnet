#
FROM centos:6.6

# add our user and group first to make sure their IDs get assigned consistently, regardless of whatever dependencies get added
RUN groupadd -r redis && useradd -r -g redis redis
RUN yum install -y curl
RUN yum install -y tar

# grab gosu for easy step-down from root
RUN curl -o /usr/local/bin/gosu -SL "https://github.com/tianon/gosu/releases/download/1.2/gosu-amd64" \
&& chmod +x /usr/local/bin/gosu
ENV REDIS_VERSION 2.8.19
ENV REDIS_DOWNLOAD_URL http://download.redis.io/releases/redis-2.8.19.tar.gz
ENV REDIS_DOWNLOAD_SHA1 3e362f4770ac2fdbdce58a5aa951c1967e0facc8
# for redis-sentinel see: http://redis.io/topics/sentinel
RUN buildDeps='gcc libc6-dev make'; \
set -x \
&& yum install -y $buildDeps \
&& rm -rf /var/lib/apt/lists/* \
&& mkdir -p /usr/src/redis \
&& curl -sSL "$REDIS_DOWNLOAD_URL" -o redis.tar.gz \
&& echo "$REDIS_DOWNLOAD_SHA1 *redis.tar.gz" | sha1sum -c - \
&& tar -xzf redis.tar.gz -C /usr/src/redis --strip-components=1 \
&& rm redis.tar.gz \
&& make -C /usr/src/redis \
&& make -C /usr/src/redis install \
&& rm -r /usr/src/redis \
&& yum install -y $buildDeps
RUN mkdir /redis && chown redis:redis /redis
RUN mkdir /var/lib/redis
RUN chown redis:redis /var/lib/redis


# install epel-release
RUN yum install -y epel-release

# add our user and group first to make sure their IDs get assigned consistently, regardless of whatever dependencies get added
RUN groupadd -r rabbitmq && useradd -r -d /var/lib/rabbitmq -m -g rabbitmq rabbitmq

# http://www.rabbitmq.com/install-debian.html
RUN yum install -y rabbitmq-server 
# get logs to stdout (thanks to http://www.superpumpup.com/docker-rabbitmq-stdout for inspiration)
# TODO figure out what we'd need to do to add "(sasl_)?" to this sed and have it work ("{"init terminating in do_boot",{rabbit,failure_during_boot,{error,{cannot_log_to_tty,sasl_report_tty_h,not_installed}}}}")
RUN sed -E 's!^(\s*-rabbit\s+error_logger)\s+\S*!\1 tty!' /usr/lib/rabbitmq/lib/rabbitmq_server-*/sbin/rabbitmq-server > /tmp/rabbitmq-server \
&& chmod +x /tmp/rabbitmq-server \
&& mv /tmp/rabbitmq-server /usr/lib/rabbitmq/lib/rabbitmq_server-*/sbin/rabbitmq-server
RUN echo '[{rabbit, [{loopback_users, []}]}].' > /etc/rabbitmq/rabbitmq.config
RUN /usr/lib/rabbitmq/bin/rabbitmq-plugins enable rabbitmq_management

COPY ./system/bin/docker-entrypoint.sh /entrypoint.sh
COPY ./system/config/redis.conf /redis/redis.conf
ENTRYPOINT ["/entrypoint.sh"]
RUN chmod +x /entrypoint.sh
CMD [ "all" , "/redis/redis.conf" ]
EXPOSE 6379 5672 15672
