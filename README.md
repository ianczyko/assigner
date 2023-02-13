# assigner

## How to start the whole system (docker compose)

> First, configure the `assigner/.env` file (details can be found in [assigner/README.md](assigner/README.md))

```sh
docker compose up --build
```

## Deployment

### VM + docker compose (Ubuntu 20.04)

#### Install docker

Use the following steps: [Install Docker](https://docs.docker.com/engine/install/ubuntu/). After that use the following commands to use docker without sudo:

```sh
sudo groupadd docker
sudo usermod -aG docker $USER
newgrp docker
```

#### Start the system

Configure the `assigner/.env` file (details can be found in [assigner/README.md](assigner/README.md)

```sh
docker compose up --build
```

#### Nginx host config

```sh
sudo apt-get install -y nginx
```

Go to `/etc/nginx/nginx.conf` and comment out the line the following way:

```conf
# include /etc/nginx/sites-enabled/*;
```

Create the following file: `/etc/nginx/conf.d/assigner.conf`:

```conf
server {
    server_name assigner.anczykowski.com; # optional
    server_name_in_redirect off;
    
    access_log /var/log/nginx/access.log;
    error_log /var/log/nginx/error.log debug;

    location / {
        proxy_pass http://127.0.0.1:3000/;
    }
}
```

#### HTTPS support (additional)

Use the following steps: [certbot instructions](https://certbot.eff.org/instructions?ws=nginx&os=ubuntufocal).
