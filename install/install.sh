#!/bin/bash
oc project 3scale
oc create secret docker-registry threescale-registry-auth \
    --docker-server=registry.redhat.io \
    --docker-username="1234|your-user" \
    --docker-password="longjwt-token.123213213.asdasdasdsad"

oc new-app registry.redhat.io/rhel8/redis-5 -e REDIS_PASSWORD=redhat --source-secret=threescale-registry-auth
oc new-app registry.redhat.io/rhel8/postgresql-10 --source-secret=threescale-registry-auth -e POSTGRESQL_USER=redhat -e POSTGRESQL_PASSWORD=redhat -e POSTGRESQL_DATABASE=system

oc delete secrets backend-redis system-database 
oc create -f 01-secrets.yml 
oc create -f 02-storage.yml 
oc create -f 03.apimanager.yml