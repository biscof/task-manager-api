#Makefile

clean:
	./gradlew clean

prepare:
	./gradlew clean installDist

build-and-check:
	./gradlew clean build test checkstyleMain checkstyleTest

report:
	./gradlew jacocoTestReport

test:
	./gradlew test

docs:
	./gradlew generateOpenApiDocs

start-app-with-db:
	docker-compose up -d -V --remove-orphans

.PHONY: build
