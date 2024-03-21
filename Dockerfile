#FROM openjdk:8-jre-buster
#ENV TZ=Asia/Shanghai
#RUN ln -snf /use/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
#COPY YiJiaoYou-0.0.1-SNAPSHOT.jar /app.jar
#ENTRYPOINT ["java", "-jar", "/app.jar"]
# 使用基于CentOS的基础镜像
# 使用基于CentOS的基础镜像
# 使用基于CentOS的基础镜像
FROM centos:latest

# 设置时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 拷贝应用程序jar文件到容器中
COPY YiJiaoYou-0.0.1-SNAPSHOT.jar /app.jar

# 在Docker容器中创建目录并复制已安装的JDK 8
RUN mkdir -p /opt/java/jdk8
COPY jdk8 /opt/java/jdk8

# 设置JAVA_HOME环境变量
ENV JAVA_HOME=/opt/java/jdk8
ENV PATH=$JAVA_HOME/bin:$PATH

# 指定应用程序启动命令
ENTRYPOINT ["java", "-jar", "/app.jar"]

