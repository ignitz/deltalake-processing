# See here for image contents: https://github.com/microsoft/vscode-dev-containers/tree/v0.145.1/containers/ubuntu/.devcontainer/base.Dockerfile

# [Choice] Ubuntu version: bionic, focal
ARG VARIANT="focal"
FROM mcr.microsoft.com/vscode/devcontainers/base:0-${VARIANT}

# [Optional] Uncomment this section to install additional OS packages.
RUN apt-get update && export DEBIAN_FRONTEND=noninteractive \
    && apt-get -y install openjdk-8-jdk \
    && (echo "deb https://dl.bintray.com/sbt/debian /" | sudo tee -a /etc/apt/sources.list.d/sbt.list) \
    && (curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | sudo apt-key add) \
    && apt-get update && apt-get -y install sbt

RUN mkdir -p /opt/spark && curl https://downloads.apache.org/spark/spark-3.0.1/spark-3.0.1-bin-hadoop2.7.tgz -o spark-3.0.1-bin-hadoop2.7.tgz \
    && tar xfvz spark-3.0.1-bin-hadoop2.7.tgz && mv spark-3.0.1-bin-hadoop2.7 /opt/spark && rm spark-3.0.1-bin-hadoop2.7.tgz

# echo "export SPARK_HOME=/opt/spark" >> ~/.profile
# echo "export PATH=$PATH:\$SPARK_HOME/bin:\$SPARK_HOME/sbin" >> ~/.profile
# echo "export PYSPARK_PYTHON=/usr/bin/python3" >> ~/.profile

# ENV SPARK_HOME="/opt/spark"
# ENV PATH="${PATH}:/opt/spark/bin:/opt/spark/sbin"
# ENV PYSPARK_PYTHON="/usr/bin/python3"