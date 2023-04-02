#!/bin/bash
kubectl apply -f ./settings/mariadb-secret.yml -n dev
kubectl apply -f ./settings/mariadb-svc.yml -n dev
kubectl apply -f ./settings/mariadb-pvc.yml -n dev
kubectl apply -f ./settings/mariadb.yml -n dev