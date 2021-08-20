# Puredu Server

请确保已安装 PostgreSQL 13，并且有用户 `postgres`，密码为 `pa$sW0rd`，端口设置为 5432.

在项目根目录打开终端，执行：

```shell
$ psql -Upostgres
postgres=# \i src/main/sql/database.sql
```

