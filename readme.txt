Maven项目目录结构：
    src/main/java           application library sources - java源代码文件
    src/main/resources      application library resources - 资源库，会自动复制到classes文件夹下
    src/main/filters        resources filter files - 资源过滤文件
    src/main/assembly       assembly descriptor - 组件的描述配置，如何打包
    src/main/config         configuration files - 配置文件
    src/main/webapp         web application sources - web应用的目录，WEB-INF,js,css等
    src/main/bin            脚本库
    src/test/java           单元测试java源代码文件
    src/test/resources      测试需要的资源库
    src/test/filters        测试资源过滤库
    src/site                一些文档
    target/                 存放项目构建后的文件和目录，jar包,war包，编译的class文件等；Maven构建时生成的
    pom.xml                 工程描述文件
    LICENSE.txt             license
    README.txt              read me

Release Note
    2018.07.13
        给reg_num列增加索引，明显提升查询效率