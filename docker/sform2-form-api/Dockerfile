FROM openjdk:11-jdk AS sform-builder
ARG BRANCH
ARG GITHUB_TOKEN

RUN echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | tee /etc/apt/sources.list.d/sbt.list && \
    echo "deb https://repo.scala-sbt.org/scalasbt/debian /" | tee /etc/apt/sources.list.d/sbt_old.list && \
    curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | apt-key add && \
    apt -y update && \
    apt -y upgrade && \
    apt -y install git sbt && \
    git clone -b ${BRANCH} https://github.com/macorains/sform2.git && \
    cd sform2 && \
    sbt form/clean && \
    # sbt test && \  <- 後々テスト実行するように
    sbt form/dist


FROM openjdk:11-jre
ARG GITHUB_USER
ARG GITHUB_TOKEN
ARG SFORM_VERSION

WORKDIR /app
COPY --from=sform-builder /sform2/formApi/target/universal/formapi-${SFORM_VERSION}.zip ./
RUN apt -y update && \
    apt -y upgrade && \
    apt -y install git unzip && \
    unzip formapi-${SFORM_VERSION}.zip && \
    mv formapi-${SFORM_VERSION} formapi && \
    git clone https://${GITHUB_TOKEN}:x-oauth-basic@github.com/macorains/sform2-config.git && \
    cp sform2-config/sform-api/application.it.conf formapi/conf
EXPOSE 9002
CMD ["nohup", "formapi/bin/formapi", "-Dhttp.port=9002", "-Dconfig.file=formapi/conf/application.it.conf"]

