# Puredu Server

请确保本地已安装并运行 PostgreSQL 13，端口设置为 5432.

在项目根目录打开终端，执行：

```shell
$ psql -Upostgres
postgres=# \i src/main/sql/database.sql
```

在 src/main/resources/ 下创建文件 secret.properties：

```properties
# suppress inspection "UnusedProperty"
spring.datasource.password=

opedukg-service.id=
```

