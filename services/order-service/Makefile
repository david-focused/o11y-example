IMAGE_NAME=order-service
MINIKUBE_ENV := eval $(minikube docker-env)
TAG ?= latest

.PHONY: clean build all

clean:
	@docker rmi order-service:${TAG}

build:
	@$(MINIKUBE_ENV) && cd .. && docker build -t $(IMAGE_NAME):${TAG} -f order-service/Dockerfile .

all: clean build