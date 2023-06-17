# Batch Admin

## Stack
1. Spring-cloud-dataflow
   1. Version : 2.10.0
      1. K8S Compatibility ([K8S 호환성](https://dataflow.spring.io/docs/installation/kubernetes/compatibility/))
      2. 향후 서포팅 계획 ([계획링크](https://spring.io/projects/spring-cloud-dataflow#support))

## 사전조건
1. K8S Cluster 환경구축 필요
2. SpringBoot Batch SpringCloudTask화 작업 필요
3. 로컬환경의 경우, minikube설치 필요 
   1. 필요한 addons 옵션
      1. registries, registry-creds (로컬에서 AWS ECR Image Pull에 필요)
   2. 사용하면 편한 옵션
      1. dashboard, ingress, ingress-dns (ingress를 사용안할 시, minikube tunnel을 사용)
   3. 디버깅 및 자동화 추천하는 도구
      1. Gradle Jib or Intellij Docker, Kubernetes Plugin => Docker Image Build 자동화
      2. K9S ([K9S Download](https://k9scli.io/))
4. AWS ECR ACCESS Credential 설정 필요

## 로컬 환경 세팅 진행 (수정필요)
```
minikube start --cpus=4 --memory=6000m (batch admin, batch를 배포하므로 넉넉하게 설정)
eval $(minikube -p minikube docker-env)
./settings-batch-admin.sh (batch-admin Pod 및 Local환경 구성용)
../docker/mariadb/mariadb-setting.sh (batch-admin이 사용하는 mariadb 세팅용)
./deploy-batch-admin.sh (batch-admin Deployment)
```

## Spring-cloud-Dataflow REST API 사용 예시
### 주의사항 (curl 사용 시, URL En/Decode 필요)
1. App Registration
```
curl http://localhost:9393/apps/task/등록할 앱명 -i -X POST \
-d 'uri=AWS ECR Full URI'
```
2. Task Registration
```
curl 'http://localhost:9393/tasks/definitions' -i -X POST \
-d 'name=등록된 앱명&definition=등록할 Task정의&description='
```
3. Scheduler Registration
```
curl 'http://localhost:9393/tasks/schedules' -i -X POST \
-d 'scheduleName=등록할 스케줄러명&taskDefinitionName=등록된 Task정의명&platform=scdf-sa&properties=scheduler.cron.expression%3D*+1+*+*+*&arguments='
```

### Task Deployer Settings Example
```
deployer.micro-service.kubernetes.namespace=batch-admin
deployer.micro-service.kubernetes.deployment-service-account-name=scdf-sa
deployer.micro-service.kubernetes.request.cpu=1
deployer.micro-service.kubernetes.request.memory=1024
deployer.micro-service.kubernetes.backoff-limit=1
deployer.micro-service.kubernetes.config-map-refs=scdf-server
deployer.micro-service.kubernetes.container-properties.commands=["/data/entrypoint.sh", "--job.name=YourJobNmae"]
deployer.micro-service.kubernetes.secret-refs=scdf-micro-service-batch
```
