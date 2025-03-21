.PHONY: help setup-minikube build deploy all clean start-port-forwards stop-port-forwards restart-port-forwards check-port-forwards

MINIKUBE_ENV := eval $(minikube docker-env)

# Current version tag - update this for each tagged commit
CURRENT_TAG := v0.3-metrics

.DEFAULT_GOAL := help

help:
	@echo "Available targets:"
	@echo "  setup-minikube         - Set up minikube with necessary addons"
	@echo "  build                  - Build all service images"
	@echo "  deploy                 - Deploy all services using Kustomize"
	@echo "  all                    - Build and deploy all services using Kustomize"
	@echo "  clean                  - Remove all deployed services"
	@echo "  start-port-forwards    - Forward all service ports to localhost"
	@echo "  stop-port-forwards     - Stop all running port-forward processes"
	@echo "  restart-port-forwards  - Restart all port-forward processes"
	@echo "  check-port-forwards    - List all running port-forward processes"

setup-minikube:
	minikube start --memory=4096 --cpus=2
	minikube addons enable ingress
	minikube addons enable metrics-server
	@echo "Minikube setup complete"
	@echo "You can also open the kubernetes dashboard via 'minikube dashboard'"

build:
	cd common-domain && ./gradlew build && cd .. && \
	$(MINIKUBE_ENV) && docker build -t order-service:$(CURRENT_TAG) -f services/order-service/Dockerfile . && \
	$(MINIKUBE_ENV) && docker build -t shipping-service:$(CURRENT_TAG) -f services/shipping-service/Dockerfile . && \
	$(MINIKUBE_ENV) && docker build -t inventory-service:$(CURRENT_TAG) -f services/inventory-service/Dockerfile .

deploy:
	kubectl apply -k kustomize/overlays/$(CURRENT_TAG)

all: build deploy

clean: stop-port-forwards
	@echo "Cleaning up Kubernetes resources..."
	kubectl delete -k kustomize/base --ignore-not-found || true
	kubectl delete deployment --all --ignore-not-found || true
	kubectl delete service --all --ignore-not-found || true
	kubectl delete configmap --all --ignore-not-found || true
	@echo "Cleanup complete"

start-port-forwards:
	@echo "Port forwarding order-service to localhost:8080..."
	kubectl port-forward svc/order-service 8080:8080 > /dev/null 2>&1 &
	@echo "Port forwarding grafana to localhost:3000..."
	kubectl port-forward svc/grafana 3000:3000 > /dev/null 2>&1 &
	@echo "Port forwarding prometheus to localhost:9090..."
	kubectl port-forward svc/prometheus 9090:9090 > /dev/null 2>&1 &

stop-port-forwards:
	@echo "Stopping all kubectl port-forward processes..."
	@pkill -f "kubectl port-forward" || echo "No port-forward processes found"
	@echo "All port forwards have been stopped"

restart-port-forwards: stop-port-forwards start-port-forwards
	@echo "All port forwards have been restarted"

check-port-forwards:
	@echo "Checking active port forwards..."
	@ps aux | grep "[k]ubectl port-forward" || echo "No port-forwards active"
