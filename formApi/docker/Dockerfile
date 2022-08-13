FROM openjdk:11-jdk AS sform-builder
ARG GITHUB_TOKEN

RUN echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | tee /etc/apt/sources.list.d/sbt.list && \
    echo "deb https://repo.scala-sbt.org/scalasbt/debian /" | tee /etc/apt/sources.list.d/sbt_old.list && \
    curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | apt-key add && \
    apt -y update && \
    apt -y upgrade && \
    apt -y install git sbt && \
    git clone -b develop-it https://${GITHUB_TOKEN}:x-oauth-basic@github.com/macorains/sform-api.git && \
    cd sform-api && \
    sbt clean && \
    # sbt test && \  <- 後々テスト実行するように
    sbt dist


FROM openjdk:11-jre
ARG GITHUB_USER
ARG GITHUB_TOKEN
ARG SFORM_VERSION
ENV PORT=9002

WORKDIR /app
COPY --from=sform-builder /sform-api/target/universal/sform-api-${SFORM_VERSION}.zip ./
RUN apt -y update && \
    apt -y upgrade && \
    apt -y install git unzip && \
    unzip sform-api-${SFORM_VERSION}.zip && \
    mv sform-api-${SFORM_VERSION} sform-api && \
    git clone https://${GITHUB_TOKEN}:x-oauth-basic@github.com/macorains/sform2-config.git && \
    cp sform2-config/sform-api/application.it.conf sform-api/conf
EXPOSE 9002
CMD ["nohup", "sform-api/bin/sform-api", "-Dhttp.port=9002", "-Dconfig.file=sform-api/conf/application.it.conf"]

