FROM eclipse-temurin:17-jdk-alpine as rest_build
WORKDIR /workspace/app

COPY atom-2024-backend/mvnw .
COPY atom-2024-backend/.mvn .mvn
COPY atom-2024-backend/pom.xml .
COPY atom-2024-backend/src src

RUN chmod +x ./mvnw
RUN ./mvnw install -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

FROM node:latest AS front_build
WORKDIR /dist/src/app
RUN npm cache clean --force
COPY atom-2024-front/ .
COPY --from=rest_build /workspace/atom-2024-front/src/app/gen src/app/gen
RUN npm install
RUN npm run build --prod

FROM eclipse-temurin:17-jdk-alpine as rest
ARG DEPENDENCY=/workspace/app/target/dependency
COPY --from=rest_build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=rest_build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=rest_build ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","com.prolegacy.atom2024backend.Atom2024BackendApplication"]
EXPOSE 8085

FROM nginx:latest as front
RUN rm -rf /usr/share/nginx/html
COPY --from=front_build /dist/src/app/dist/atom-2024-front /usr/share/nginx/html
COPY --from=front_build /dist/src/app/nginx.conf  /etc/nginx/conf.d/default.conf
COPY --from=front_build /dist/src/app/front-run.sh /front-run.sh
ENTRYPOINT ["/front-run.sh"]
EXPOSE 80