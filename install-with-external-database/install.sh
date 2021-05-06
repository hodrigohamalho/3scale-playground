#!/bin/bash
# Before to execute it, certify to be on the correct project and with the 3scale operator already installed

# Generate a new token here: https://access.redhat.com/terms-based-registry
oc create secret docker-registry threescale-registry-auth \
   --docker-server=registry.redhat.io \
   --docker-username="aa" \
   --docker-password="bbb"

oc delete secrets backend-redis system-database 
# Create the secrets for the external databases
oc create -f 01-secrets.yml 
# Create a RWX Volume for 3Scale developer portal and Redis/Postgres PV and PVC
oc create -f 02-3scale-storage.yml 
oc create -f 02-redis-storage.yml 
oc create -f 02-postgres-storage.yml 

# Instal Redis
oc new-app registry.redhat.io/rhel8/redis-5 --name=redis -e REDIS_PASSWORD=redhat --source-secret=threescale-registry-auth
oc set volume dc redis --add --overwrite --name=redis-volume-1 --type=persistentVolumeClaim --claim-name=redis-claim --mount-path=/var/lib/redis/data

# Install Postgres (https://catalog.redhat.com/software/containers/detail/5ba0ae0ddd19c70b45cbf4cd)
oc new-app registry.redhat.io/rhel8/postgresql-10 --name=postgres --source-secret=threescale-registry-auth -e POSTGRESQL_USER=redhat -e POSTGRESQL_PASSWORD=redhat -e POSTGRESQL_DATABASE=system
oc set volume dc postgres --add --overwrite --name=postgres-volume --type=persistentVolumeClaim --claim-size=5Gi --mount-path=/var/lib/pgsql/data

# Install the 3Scale API Manager
oc create -f 03-apimanager.yml
