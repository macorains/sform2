FROM openjdk:11-jdk AS sform-builder

RUN echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | tee /etc/apt/sources.list.d/sbt.list && \
    echo "deb https://repo.scala-sbt.org/scalasbt/debian /" | tee /etc/apt/sources.list.d/sbt_old.list && \
    curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | apt-key add && \
    apt -y update && \
    apt -y upgrade && \
    apt -y install git sbt && \
    git clone -b develop-it https://github.com/macorains/sform2.git && \
    cd sform2 && \
    sbt clean && \
    # sbt test && \  <- 後々テスト実行するように
    sbt dist


FROM openjdk:11-jre
ARG GITHUB_USER
ARG GITHUB_TOKEN
ARG SFORM_VERSION

WORKDIR /app
COPY --from=sform-builder /sform2/target/universal/sform2s-${SFORM_VERSION}.zip ./
RUN apt -y update && \
    apt -y upgrade && \
    apt -y install git unzip && \
    unzip sform2s-${SFORM_VERSION}.zip && \
    mv sform2s-${SFORM_VERSION} sform2 && \
    git clone https://${GITHUB_USER}:${GITHUB_TOKEN}@github.com/macorains/sform2-config.git && \
    cp sform2-config/application.it.conf sform2/conf
EXPOSE 9001
CMD ["nohup", "sform2/bin/sform2s", "-Dport=9001", "-Dconfig.file=sform2/conf/application.it.conf"]

