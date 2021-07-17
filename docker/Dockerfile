FROM mysql
ENV MYSQL_ROOT_PASSWORD=password
ENV MYSQL_DATABASE=sform
ENV MYSQL_USER=sform
ENV MYSQL_PASSWORD=sform
EXPOSE 3306
ADD ./my.cnf /etc/mysql/conf.d/my.cnf
ADD ./data /var/lib/mysql
ADD ./sql /docker-entrypoint-initdb.d
CMD ["mysqld"]
