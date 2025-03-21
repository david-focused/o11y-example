IMAGE_NAME=inventory-service
MINIKUBE_ENV := eval $$(minikube docker-env)
TAG ?= latest

.PHONY: clean build all

clean:
	@docker rmi inventory-service:${TAG}

build:
	@$(MINIKUBE_ENV) && docker build -t $(IMAGE_NAME):${TAG} .

all: clean build 