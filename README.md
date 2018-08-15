# SpringMVCHibernateWithSpringSecurityExample


1.Create a schema in your database server.<br/>
2.Run the script included in create_sql.txt file.<br/>
3.Edit the application.properties file according to db connection credentials. (As default, I used Mysql)<br/>
4.Launch the program on Tomcat, go to url "http://localhost:8080/SpringMVCHibernateWithSpringSecurityExample/login"<br/>
5.You can sign in with the admin credentials (username=admin, password=Admin01)<br/>
6.Using "http://localhost:8080/SpringMVCHibernateWithSpringSecurityExample/register" url, you can register new users. This page can be accessed without sign in.<br/>
7.Newly registered users has "USER" role on system. A user whoe has "ADMIN" role, can edi the registered users, so he can asssign "ADMIN" or "DBA" roles to other users.<br/>
