# o11y-example
A repo for demonstrating varying levels of Observability coverage

## Architecture

### Services

The system consists of the following basic services:

- **Order Service**: Handles order creation and management
- **Shipping Service**: Processes shipping requests for orders
- **Inventory Service**: Manages product inventory and availability

At each tag, additional kustomize overlays are present to add additional services to support increased Observability coverage.

### Tags
#### v0.1:
Just basic unstructured logging in each service

- **Loki**: Collects and processes logs
- **Grafana**: Visualize logs

#### v0.2:
Structured logging added to each service

- **slf4j**: Structured logging for Java
- **Loki**: Collects and processes structured logs
- **Grafana**: Visualize structured logs

### v0.3:
Metrics added for each service

- **Prometheus**: Collects and processes metrics

#### v0.4:
Tracing added for each service

- **OpenTelemetry Collector**: Collects and processes telemetry data
- **Jaeger**: Visualization and analysis of distributed traces

## Setup

### Prerequisites

- [Docker](https://docs.docker.com/get-docker/)
- [Minikube](https://minikube.sigs.k8s.io/docs/start/)
- [kubectl](https://kubernetes.io/docs/tasks/tools/install-kubectl/)
- [Kustomize](https://kubectl.docs.kubernetes.io/installation/kustomize/) (included with recent kubectl versions)
- [Helm](https://helm.sh/docs/intro/install/)

## Running

### 1. Start Minikube

```bash
make setup-minikube
```

This will start Minikube and enable necessary addons like ingress and metrics-server.

Use the minikube docker daemon:
```bash
eval $(minikube docker-env)
```

to ensure that docker commands are run in the minikube cluster.

### 2. Clean existing resources if needed

```bash
# Clean resources from previous tag
make clean
```

### 3. Build and Deploy Services

The repository provides a Makefile for easy building and deployment. The build process will compile the common-domain module first, then build Docker images for each service in the services directory.

```bash
# Build all services
make build

# Deploy all services using Kustomize
make deploy

# Or, build and deploy in one command
make all
```

### 4. Port Forward Services

```bash
make start-port-forwards
```

Test services are now accessible via:
```bash
make check-port-forwards
```

### 5. Access Grafana
   Open your browser and navigate to `http://localhost:3000`.

   Use `admin` for the username and `admin` for the password.

   In Grafana, go to Explore and select the Loki data source. You can use the following queries to view logs:
   - `{service="order-service"}` - View order service logs
   - `{service="shipping-service"}` - View shipping service logs
   - `{service="inventory-service"}` - View inventory service logs
   - `{service=~".*service"} |= "error"` - View all error logs across services

### 6. Test Service Requests

The repository includes a test script to easily send various types of requests to the services:

```bash
./test-requests.sh
```

It provides a menu to select different test scenarios to see how each layer of Observability helps to identify and resolve or prevent issues.

## 7. Access Jaeger

Open your browser and navigate to `http://localhost:16686`.

## Cleanup

```bash
make clean
```
