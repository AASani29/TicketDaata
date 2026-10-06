# TicketDaata on Kubernetes

Deploys the whole system — frontend, API gateway, auth/orders/ticket services, MongoDB, and RabbitMQ — to a local [kind](https://kind.sigs.k8s.io/) cluster with plain YAML manifests (no Helm yet; that's a planned follow-up phase).

## Architecture change: Eureka → Kubernetes-native discovery

The original design used Netflix Eureka (`ServiceRegistry/`) for service registration and Spring Cloud Gateway's `lb://` client-side load balancing. On Kubernetes that's redundant:

| | Eureka (before) | Kubernetes-native (now) |
|---|---|---|
| Discovery | App-level registry; each service registers itself and polls the registry | `kube-dns` resolves a Service name (`auth-service`) to the Service's cluster IP — no app code involved |
| Load balancing | Client-side (Ribbon/Spring Cloud LoadBalancer) picks an instance from the registry | `kube-proxy` load-balances across every healthy pod behind the Service (L4, VIP-based) automatically |
| Extra moving parts | A registry service you deploy, scale, and keep available | None — it's part of the platform |
| Portability | Works the same on VMs, bare metal, or k8s | Only works inside a cluster with a working Service network |

Since this project now only targets Kubernetes, the Eureka client was removed from every service (`@EnableDiscoveryClient`, the `spring-cloud-starter-netflix-eureka-client` dependency, and the `eureka:` config block). Routes in `APIGateway/application.yml` changed from `uri: lb://TICKETDAATA-AUTH-SERVICE` to `uri: ${AUTH_SERVICE_URL:http://localhost:9001}`, where `AUTH_SERVICE_URL` is `http://auth-service.ticketdaata.svc.cluster.local:9001` in-cluster.

`ServiceRegistry/` is left in the repo (unmodified, still a working standalone Eureka server) as a record of the before-architecture, but it is **not deployed** here and nothing registers to it anymore. If this were deployed across multiple clusters or partly on non-k8s infra, Eureka would earn its place back — that's the actual tradeoff, not "Eureka is bad."

## What's self-contained vs. what isn't

MongoDB and RabbitMQ run **in-cluster** (a `StatefulSet` with a `PersistentVolumeClaim` for Mongo, a `Deployment` + PVC for RabbitMQ) instead of the hardcoded MongoDB Atlas cluster the repo previously pointed at. That makes the whole demo runnable offline with no real credentials anywhere. The Secret manifests here (`mongo/secret.yaml`, `rabbitmq/secret.yaml`, `jwt-secret.yaml`) hold **demo-only, local credentials committed on purpose** so `kubectl apply -k` just works — that's fine for a disposable local kind cluster, but is **not** how you'd manage secrets for anything real (use `kubectl create secret` imperatively, or Sealed Secrets / External Secrets Operator / SOPS + a GitOps pipeline).

## Prerequisites

- Docker
- [kind](https://kind.sigs.k8s.io/docs/user/quick-start/#installation)
- `kubectl`

## 1. Create the cluster

```bash
kind create cluster --name ticketdaata --config k8s/kind-cluster-config.yaml
```

The config binds host ports 80/443 to the control-plane node so ingress-nginx is reachable at `http://localhost/` without a cloud load balancer.

## 2. Build and load the images

No registry is used in this phase — images are built locally and loaded straight into kind's node.

```bash
docker build -t ticketdaata/api-gateway:local ./APIGateway
docker build -t ticketdaata/auth-service:local ./AuthService
docker build -t ticketdaata/orders-service:local ./OrdersService
docker build -t ticketdaata/ticket-service:local ./ticketservice
docker build -t ticketdaata/frontend:local ./Frontend

kind load docker-image ticketdaata/api-gateway:local ticketdaata/auth-service:local \
  ticketdaata/orders-service:local ticketdaata/ticket-service:local ticketdaata/frontend:local \
  --name ticketdaata
```

## 3. Install ingress-nginx and metrics-server

```bash
# ingress-nginx, using the kind-specific manifest (NodePort + the webhook patch kind needs)
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/kind/deploy.yaml
kubectl wait --namespace ingress-nginx \
  --for=condition=ready pod --selector=app.kubernetes.io/component=controller --timeout=120s

# metrics-server, patched to tolerate kind's self-signed kubelet certs
kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml
kubectl patch deployment metrics-server -n kube-system --type=json \
  -p '[{"op":"add","path":"/spec/template/spec/containers/0/args/-","value":"--kubelet-insecure-tls"}]'
```

Without `metrics-server`, the HPAs will show `<unknown>` for current CPU usage instead of scaling.

## 4. Deploy

```bash
kubectl apply -k k8s/
kubectl get pods -n ticketdaata -w
```

Wait for everything to reach `Running`/`1/1 Ready` — Mongo and RabbitMQ first, then the four Spring services (they'll crash-loop briefly if they start before Mongo/RabbitMQ are ready; Kubernetes retries them automatically).

## 5. Use it

Open `http://localhost/` — that's the React frontend, served via the Ingress. Requests to `/auth/*` and `/api/*` are routed to the gateway, which forwards to the right backend service by its Kubernetes Service DNS name.

Useful checks:

```bash
kubectl get hpa -n ticketdaata        # should show real %, not <unknown>, once there's traffic
kubectl get pods -n ticketdaata -o wide
kubectl logs -n ticketdaata deploy/api-gateway
```

Prove Eureka's old job is actually being done by the platform now — kill a pod and confirm the Service keeps serving:

```bash
kubectl delete pod -n ticketdaata -l app=auth-service --field-selector status.phase=Running -o name | head -n1 | xargs kubectl delete
# immediately re-run requests through http://localhost/ — no downtime, the other replica serves traffic,
# and a new pod comes up to replace the one that was deleted.
```

## Teardown

```bash
kind delete cluster --name ticketdaata
```

## Known limitations of this phase

- No registry/CI — images are built and loaded locally (see the CI/CD phase for GHCR + automated builds).
- No Helm chart yet (planned follow-up).
- No metrics/logs/traces dashboards yet (observability is a separate phase).
- HPA is CPU-only; there's no load generator wired in yet to actually watch it scale (that's the k6 load-testing phase).
