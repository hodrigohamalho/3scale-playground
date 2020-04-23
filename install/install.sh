#!/bin/bash
# Before to execute it, certify to be on the correct project and with the 3scale operator already installed

# Generate a new token here: https://access.redhat.com/terms-based-registry
oc create secret docker-registry threescale-registry-auth \
   --docker-server=registry.redhat.io \
   --docker-username="1234|user" \
   --docker-password="asdadsdasdasdsadasd"

# Instal Redis
oc new-app registry.redhat.io/rhel8/redis-5 -e REDIS_PASSWORD=redhat --source-secret=threescale-registry-auth

# Install Postgres
oc new-app registry.redhat.io/rhel8/postgresql-10 --source-secret=threescale-registry-auth -e POSTGRESQL_USER=redhat -e POSTGRESQL_PASSWORD=redhat -e POSTGRESQL_DATABASE=system

oc delete secrets backend-redis system-database 
# Create the secrets for the external databases
oc create -f 01-secrets.yml 
# Create a RWX Volume
oc create -f 02-storage.yml 
# Install the 3Scale API Manager
oc create -f 03-apimanager.yml
