FROM adoptopenjdk/openjdk11:ubi
LABEL email=padomay1352@gmail.com
LABEL name=김준수
COPY . /app
WORKDIR /app
RUN chmod +x gradlew
RUN sed -i -e 's/\r$//' gradlew
RUN /app/gradlew build
CMD java -jar -DSpring.profile.active=prod /app/build/libs/marklog-0.0.1-SNAPSHOT.jar