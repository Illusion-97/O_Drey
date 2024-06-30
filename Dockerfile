FROM rsunix/yourkit-openjdk17

ADD O_Drey.jar O_Drey.jar
ENTRYPOINT ["java", "-jar","O_Drey.jar"]
EXPOSE 8080
