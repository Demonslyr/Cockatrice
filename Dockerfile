FROM ubuntu:bionic as base
RUN apt-get update && apt-get install -y --no-install-recommends\
  libqt5sql5-mysql
  
FROM base as build
RUN apt-get install -y --no-install-recommends\
  build-essential\
  cmake\
  libprotobuf-dev\
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

FROM base as final
RUN apt-get install -y\
  libqt5websockets5\
  libprotobuf10 &&\
RUN adduser servatrice
COPY --from=build /usr/local/bin /usr/local/bin
COPY --from=build /usr/local/share/icons /usr/local/share/icons
COPY --from=build /usr/local/share/servatrice /usr/local/share/servatrice
COPY ./servatrice/servatrice.ini /usr/local/share/servatrice/servatrice.ini
USER servatrice
WORKDIR /home/servatrice

ENTRYPOINT [ "servatrice", "--log-to-console", "--config=/usr/local/share/servatrice/servatrice.ini"]
