# SimpleMerge
![java 1.8](https://img.shields.io/badge/java-1.8-orange.svg) ![MIT license](https://img.shields.io/badge/license-MIT-blue.svg) ![build passing](https://img.shields.io/badge/build-passing-brightgreen.svg)  
간단한 diff, merge 기능을 제공하는 프로그램  

# 팀 구성
구성|이름|맡은 부분
---|---|---------
팀장|홍성현|Model, model unit test, 문서
팀원|김동환|View, Controller, UI test, system test, 프로젝트 setting, 문서
팀원|이희상|fileIO(model part), 테스팅 프레임워크 조사, diagram tool 조사 및 문서에 적용, 문서

# 빠른 시작
gradle을 이용하면 프로젝트에 사용한 외부 라이브러리나 프로젝트 설정 등을 빠르게 적용할 수 있습니다.
모든 소스코드는 src/main/에 포함되어 있고, 모든 테스트 케이스는 src/test/에 포함되어 있습니다.
권장 개발 환경은 jdk 1.8.91 이상, gradle 2.9 이상입니다.

# 배포
gradle의 'fatJar' task를 실행하면 build/libs/SimpleMerge-all-*.jar가 생성됩니다. 이 파일을 통해 SimpleMerge를 standalone으로 사용할 수 있습니다.

-------------------------------------------------------------------------------------------------------------
중앙대학교 컴퓨터공학부 휴먼 ICT 소프트웨어 공학 2016년 Term Project - Team 6
