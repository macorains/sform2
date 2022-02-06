FROM openjdk:11-jdk
ARG GITHUB_USER
ARG GITHUB_TOKEN

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

EXPOSE 9001
