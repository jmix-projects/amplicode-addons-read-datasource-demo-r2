FROM openjdk:8

COPY ./build/libs /opt/np-addons-demo-r2

CMD java -Dapp.home=/opt/np-addons-demo-r2-home -jar /opt/np-addons-demo-r2/np-addons-demo-r2-0.0.1-SNAPSHOT.jar