FROM clojure
RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app
COPY project.clj /usr/src/app
RUN lein deps
COPY . /usr/src/app
RUN lein ring uberjar
RUN cp target/uberjar/linelos*-standalone.jar app-standalone.jar # Depends on project.clj settings

CMD ["java", "-jar", "app-standalone.jar"]
