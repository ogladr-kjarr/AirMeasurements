FROM apache/nifi
WORKDIR /opt/nifi/nifi-current/lib
COPY postgresql-42.4.0.jar postgresql-42.4.0.jar

RUN chown -R nifi:nifi postgresql-42.4.0.jar
RUN chmod 664 postgresql-42.4.0.jar


WORKDIR ${NIFI_HOME}

# Apply configuration and start NiFi
#
# We need to use the exec form to avoid running our command in a subshell and omitting signals,
# thus being unable to shut down gracefully:
# https://docs.docker.com/engine/reference/builder/#entrypoint
#
# Also we need to use relative path, because the exec form does not invoke a command shell,
# thus normal shell processing does not happen:
# https://docs.docker.com/engine/reference/builder/#exec-form-entrypoint-example
ENTRYPOINT ["../scripts/start.sh"]
