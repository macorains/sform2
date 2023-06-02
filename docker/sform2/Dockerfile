FROM openjdk:11-jdk AS sform-builder
ARG BRANCH

RUN echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | tee /etc/apt/sources.list.d/sbt.list && \
    echo "deb https://repo.scala-sbt.org/scalasbt/debian /" | tee /etc/apt/sources.list.d/sbt_old.list && \
    curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | apt-key add && \
    apt -y update && \
    apt -y upgrade && \
    apt -y install git sbt
RUN git clone -b ${BRANCH} https://github.com/macorains/sform2.git && \
    cd sform2 && \
    sbt admin/clean && \
    # sbt test && \  <- 後々テスト実行するように
    sbt admin/dist


FROM openjdk:11-jre
ARG GITHUB_USER
ARG GITHUB_TOKEN
ARG SFORM_VERSION

WORKDIR /app
COPY --from=sform-builder /sform2/adminApi/target/universal/adminapi-${SFORM_VERSION}.zip ./
RUN apt -y update && \
    apt -y upgrade && \
    apt -y install git unzip && \
    unzip -o adminapi-${SFORM_VERSION}.zip && \
    mv adminapi-${SFORM_VERSION} adminapi && \
    git clone https://${GITHUB_TOKEN}:x-oauth-basic@github.com/macorains/sform2-config.git && \
    cp sform2-config/application.it.conf adminapi/conf && \
    cp sform2-config/sform-conf/sform.it.conf adminapi/conf \
EXPOSE 9001
CMD ["nohup", "adminapi/bin/adminapi", "-Dhttp.port=9001", "-Dconfig.file=adminapi/conf/application.it.conf"]

