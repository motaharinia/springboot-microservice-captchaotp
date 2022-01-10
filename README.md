## Project:

### Project Descriptions:
please see application.properties files in resources folder and select an active profile "dev" or "tst" or "prod" to run project. you can check test methods too.

steps:
- run a mysql docker image [https://gist.github.com/motaharinia][https://gist.github.com/motaharinia]
- run "/externalconfig/sql/DDL.sql" to create project database
- run "/src/test/java/com/motaharinia/client/project/member/presentation/MemberControllerIntegrationTest" to check sample module tests


### IntellliJ IDEA Configurations:
- IntelijIDEA: Help -> Edit Custom Vm Options -> add these two line:
    - -Dfile.encoding=UTF-8
    - -Dconsole.encoding=UTF-8
- IntelijIDEA: File -> Settings -> Editor -> File Encodings-> Project Encoding: form "System default" to UTF-8. Maybe it affected somehow.
- IntelijIDEA: File -> Settings -> Editor -> File Encodings-> Default Encoding for properties files:  UTF-8.
- IntelijIDEA: File -> Settings -> Editor -> General -> Code Completion -> check "show the documentation popup in 500 ms"
- IntelijIDEA: File -> Settings -> Editor -> General -> Auto Import -> check "Optimize imports on the fly (for current project)"
- IntelijIDEA: File -> Settings -> Editor -> Color Scheme -> Color Scheme Font -> Scheme: Default -> uncheck "Show only monospaced fonts" and set font to "Tahoma"
- IntelijIDEA: Run -> Edit Configuration -> Spring Boot -> XXXApplication -> Configuration -> Environment -> VM Options: -Dspring.profiles.active=dev
- IntelijIDEA: Run -> Edit Configuration -> Spring Boot -> XXXApplication -> Code Coverage -> Fix the package in include box

<hr/>
<a href="mailto:eng.motahari@gmail.com?"><img src="https://img.shields.io/badge/gmail-%23DD0031.svg?&style=for-the-badge&logo=gmail&logoColor=white"/></a>

