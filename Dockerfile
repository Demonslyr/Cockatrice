FROM ubuntu:bionic

RUN apt-get update && apt-get install -y\
  build-essential\
  cmake\
  git\
  libprotobuf-dev\
  libqt5sql5-mysql\
  libmysqlclient-dev\
  libqt5websockets5-dev\
  protobuf-compiler\
  qt5-default\
  qtbase5-dev\
  qttools5-dev-tools\
  qttools5-dev

COPY . /home/servatrice/code/
WORKDIR /home/servatrice/code

WORKDIR build
RUN cmake .. -DWITH_SERVER=1 -DWITH_CLIENT=0 -DWITH_ORACLE=0 -DWITH_DBCONVERTER=0 &&\
  make &&\
  make install
COPY ./servatrice/servatrice.ini /usr/local/share/servatrice/servatrice.ini
WORKDIR /home/servatrice

ENTRYPOINT [ "servatrice", "--log-to-console", "--config=/usr/local/share/servatrice/servatrice.ini"]
