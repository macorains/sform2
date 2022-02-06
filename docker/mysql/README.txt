> cd path/to/sform2/docker
> docker build -t mysqldb .
> docker run -p 3306:3306 -it --name mysqldb mysqldb