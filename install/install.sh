#!/bin/bash
oc project 3scale
oc create secret docker-registry threescale-registry-auth \
    --docker-server=registry.redhat.io \
    --docker-username="6702088|3scale-asi-poc" \
    --docker-password="eyJhbGciOiJSUzUxMiJ9.eyJzdWIiOiI3MmZhODI2ODRlMmE0MzMwYmE3YjJhYjc3ZGRkNGFhZSJ9.JirulQo7FVgBoi3Ff_rLpxm-uKlm90vHdPDthms0YiOoBha1ZQdq6vpoO2RZmca4K54XtTYZb5hK3YfVrlkSbbN3hCpZnQrUqR5pPadi0iI4X0PbytzhOhghmxoFMZdj5D-R4iMtczbn_t_v9nb-9rAKTVNaJ3kyvEgKMEa_T4f-PuQ7drNtjmEo_-R940m6k55j9xMicXshAMXfy6St2j-WckE0Qr3HTScsNcXKzyC-C3LdC1dX1fvnuYHwNxg69ECYwpcKM8TGXbR9PeollB1RPF2N5z57tYfbqGvZLIiBmeXnKH8cvWU7zsSRJpwFfzGbEz3NYIB8b8Nclh7ViKk8Q3LN67UV12ogQUgoSy6Cl6eCoQRBcULWHv4sOXdr0NiqJrVojpjeucQc2loprMgYrAQE-oRUA9wDVq7dkV7yKTsPMrUnjbBfw3kM9qOZMZ8vdPIz_GQhEgj5O49mYnXq_MvjReMXN6lCPTi2wcFdXCI-rW1qzp10lP6v8lMt3nt7mv_OrSJQJO9RMcLAHwkSNmPZ6lQs0qvQ9QkpiS0-TIFCiDamdoVgyVvoiZIFGvcD0lKpspYJNAawPKMmUzDWH4QB1pxwXZnkRYsN_lsZr1_Txw3dfN74xOONkNR_bIfBP_yPs9gwAOIGukQPruD5LSwh8X7W7ii0L8K_79k"

oc new-app registry.redhat.io/rhel8/redis-5 -e REDIS_PASSWORD=redhat --source-secret=threescale-registry-auth
oc new-app registry.redhat.io/rhel8/postgresql-10 --source-secret=threescale-registry-auth -e POSTGRESQL_USER=redhat -e POSTGRESQL_PASSWORD=redhat -e POSTGRESQL_DATABASE=system

oc delete secrets backend-redis system-database 
oc create -f 01-secrets.yml 
oc create -f 02-storage.yml 
oc create -f 03-apimanager.yml