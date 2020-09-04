# frame

## 开发
需要连接到如下系统才能运行：

    redis
    mysql

## gradle
编译系统：

    gradle clean build

加上

    -x test

参数可以跳过测试

运行系统：

    gradle

它等价于：

    gradle bootRun

查看gradle的所有任务的命令：

    gradle tasks

## gradlew
在项目中内置了一个gradle。如果使用内置的gradle，命令是gradlew：

    gradlew clean build -x test
    gradlew
    gradlew tasks

## 构建产品
使用prod profile构建产品：

    gradle -Pprod clean bootWar

使用Java命令运行产品：

    java -jar build/libs/*.war

## 测试
运行测试：

    gradle test
