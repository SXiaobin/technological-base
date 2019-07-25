# 标准命令集

## build

```cmd
docker build .
```

## save

```cmd
Dock image save {image id} -o xxx.tar image:tag
```

## load

```cmd
docker load -i images.tar
```

## prune

Update Sept. 2016: Docker 1.13: [PR 26108](https://github.com/docker/docker/pull/26108) and [commit 86de7c0](https://github.com/docker/docker/commit/86de7c000f5d854051369754ad1769194e8dd5e1) introduce a few new commands to help facilitate visualizing how much space the docker daemon data is taking on disk and allowing for easily cleaning up "unneeded" excess.

[**docker system prune**](https://docs.docker.com/engine/reference/commandline/system_prune/) will delete ALL dangling data (i.e. In order: containers stopped, volumes without containers and images with no containers). Even unused data, with `-a` option.

- [`docker container prune`](https://docs.docker.com/engine/reference/commandline/container_prune/)
- [`docker image prune`](https://docs.docker.com/engine/reference/commandline/image_prune/)
- [`docker network prune`](https://docs.docker.com/engine/reference/commandline/network_prune/)
- [`docker volume prune`](https://docs.docker.com/engine/reference/commandline/volume_prune/)

For *unused* images, use `docker image prune -a` (for removing dangling *and* ununsed images).
Warning: '*unused*' means "images not referenced by any container": be careful before using `-a`.

As illustrated in [A L](https://stackoverflow.com/users/1207596/a-l)'s [answer](https://stackoverflow.com/a/50405599/6309), `docker system prune --all` will remove all *unused* images not just dangling ones... which can be a bit too much.

Combining `docker xxx prune` with the [`--filter` option](https://docs.docker.com/engine/reference/commandline/system_prune/#filtering) can be a great way to limit the pruning ([docker SDK API 1.28 minimum, so docker 17.04+](https://docs.docker.com/develop/sdk/#api-version-matrix))

> The currently supported filters are:

- `until (<timestamp>)` - only remove containers, images, and networks created before given timestamp
- `label` (`label=<key>`, `label=<key>=<value>`, `label!=<key>`, or `label!=<key>=<value>`) - only remove containers, images, networks, and volumes with (or *without*, in case `label!=...` is used) the specified labels.

See "[Prune images](https://docs.docker.com/config/pruning/#prune-images)" for an example.

# 具体指令集

## List all containers

```cmd
docker ps -aq
```

## 杀死所有正在运行的容器

```cmd
docker kill $(docker ps -a -q)
```

## 删除所有已经停止的容器
```cmd
docker rm $(docker ps -a -q)
```

## 删除所有未打 dangling 标签的镜

```cmd
docker rmi $(docker images -q -f dangling=true)
```

## 删除所有镜像

```cmd
docker rmi $(docker images -q)
```

## 强制删除 无法删除的镜像

```cmd
docker rmi -f <IMAGE_ID>
docker rmi -f $(docker images -q)
```

## 为这些命令创建别名

```cmd
# ~/.bash_aliases
 
# 杀死所有正在运行的容器.
alias dockerkill='docker kill $(docker ps -a -q)'
 
# 删除所有已经停止的容器.
alias dockercleanc='docker rm $(docker ps -a -q)'
 
# 删除所有未打标签的镜像.
alias dockercleani='docker rmi $(docker images -q -f dangling=true)'
 
# 删除所有已经停止的容器和未打标签的镜像.
alias dockerclean='dockercleanc || true && dockercleani'
```

# How Tos

## How to build a java image which we can remote debug

In Java 8 the JDK supports a JAVA_TOOL_OPTIONS environment variable so to enable the debugger for any Java application. Docker image is only a VM which is running a java application, so we just need defind the  JAVA_TOOL_OPTIONS environment variable in docker run command / docker file/ docker-compose file(maybe)

- Here is one example about how to do it in docker run command

```cmd
-p 8000:8000 -e "JAVA_TOOL_OPTIONS=\"-agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=n\""
```

- Here is one example how to do it in docker file.

```dockerfile
FROM java:8-alpine

EXPOSE 8080

ADD http://paipebasissrv1.psi.de:8082/repository/Basis-Releases/de/psi/paip/mes/docker-health-check/1.0/docker-health-check-1.0.jar /healthcheck.jar
HEALTHCHECK CMD java -jar /healthcheck.jar --url=http://localhost:8080/actuator/health

ADD target/service-workflowhelper2.jar /service-workflowhelper2.jar
ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=n", "-Xmx256m",  "-jar", "/service-workflowhelper2.jar"]

```

* Here is one example how to do it in docker-compose file

```yaml
  service-workflowhelper2:
    image: pai-bld-mesbuild1.psi.de:19123/mes/service-workflowhelper2:latest
    ports:
      - 21015:8000
    environment:
      JAVA_TOOL_OPTIONS: "-agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=n"
```

