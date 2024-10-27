# observability-stack 

Docker compose configuration to launch grafana, tempo, loki and prometheus for 
use by the sample apps in observe project. 

1. start `docker compose up -d`
2. stop 'docker compose down` add `--volumes to clean up volumes` 
3. status `docker cmopose ps`
4. logs `docker compose logs <service-name>` ex `docker compose logs tempo`
