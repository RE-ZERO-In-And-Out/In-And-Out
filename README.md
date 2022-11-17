![header](https://user-images.githubusercontent.com/27846824/201936135-ee9ad674-ca48-4edd-bd1c-5a4dc37f5f80.png)


# In-And-Out

![-----------------------------------------------------](https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/rainbow.png)

### Description

<table>
  <tr>
    <td>
달력 UI를 통한 수입/지출 체크 및 일기 쓰기 그리고 다양한 인터페이스로 심플한 수입/지출 관리 서비스를 제공하는 사이트
    </td>
  </tr>
</table>

#### Demo

> live demo: [link](http://ec2-3-34-206-181.ap-northeast-2.compute.amazonaws.com:3000/)

![-----------------------------------------------------](https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/rainbow.png)

### 기획 배경 및 해결하고자 하는 문제

<table>
  <tr>
    <td>
단순한 가계부를 작성하기에는 기존에 존재하는 네이버 가계부와 같은 앱들이 UI/UX 관점에서 복잡하고 한눈에 들어오지 않아 사용자가 필요한 부분에 집중 할 수 있게 Material Design과 기존 부족한 그래프 기능을 보완하기 위해 Chart.js를 사용해 심플한 가계부를 만들었습니다 
    </td>
  </tr>
</table>

![-----------------------------------------------------](https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/rainbow.png)

### 핵심 기능

1. 달력
   (영상)
2. 수입/지출
   (영상)
3. 월간/연간 보고서
<div>
   <img src="https://user-images.githubusercontent.com/70008599/202196778-b4a1a90a-c3b7-4e67-93fd-8294d254babb.gif"/>
</div>

![-----------------------------------------------------](https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/rainbow.png)

### 사용한 기술 및 배포 환경

#### Frontend
<div style="display: flex">
  <img src="https://img.shields.io/badge/Create React App-09D3AC?style=flat&logo=Create React App&logoColor=white" />
  <img src="https://img.shields.io/badge/React-61DAFB?style=flat&logo=React&logoColor=white" />
  <img src="https://img.shields.io/badge/Material Design-757575?style=flat&logo=Material Design&logoColor=white" />
  <img src="https://img.shields.io/badge/React Query-FF4154?style=flat&logo=React Query&logoColor=white" />
  <img src="https://img.shields.io/badge/Styled Components-DB7093?style=flat&logo=styled-components&logoColor=white" />
  <img src="https://img.shields.io/badge/Zustand-0078D7?style=flat&logo=Zulip&logoColor=white" />
  <img src="https://img.shields.io/badge/React Hook Form-EC5990?style=flat&logo=FormStack&logoColor=white" />
  <img src="https://img.shields.io/badge/Chart.js-FF6384?style=flat&logo=Chart.js&logoColor=white" />
  <img src="https://img.shields.io/badge/React Router-CA4245?style=flat&logo=React Router&logoColor=white" />
  <img src="https://img.shields.io/badge/Amazon EC2-FF9900?style=flat&logo=Amazon EC2&logoColor=white" />
</div>

#### Backend
<div style="display: flex">
  <img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=flat&logo=Spring Boot&logoColor=white" />
</div>

![-----------------------------------------------------](https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/rainbow.png)

#### Team (담당한 업무)
<details>
<summary> 윤지용 </summary>

1. 달력
2. 수입
3. 보고서(수입)
4. 엑셀
</details>
<details>
<summary> 오근협 </summary>

1. 일기
2. 지출
3. 보고서(지출)
</details>
<details>
<summary> 김란 </summary>

1. 로그인, OAuth 로그인
2. 회원가입 및 회원관련
</details>
<details>
<summary> 이태희 </summary>

1. 일기
2. 달력
3. api formData 통신
4. 스타일링 
</details>
<details>
<summary> 김찬주 </summary>
1. 회원가입 및 정보 수정
- react-hook-form + yup을 사용한 입력 정보 유효성 검사 

2. 수입/지출
- react-data-grid를 사용한 수입/지출 및 보고서 표 작성 및 드롭다운 메뉴 커스터마이징

3. 보고서
- chart.js를 사용한 월간/연간 보고서 작성 및 그래프 커스터마이징

4. 기타
- react query를 사용한 빠른 서버 데이터 변경 반영 및 오래된 데이터 자동 업데이트
-  Material 디자인 적용
</details>

![-----------------------------------------------------](https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/rainbow.png)

### 개발 관련 문서

<details>
<summary> 피그마 </summary>
<img src="https://user-images.githubusercontent.com/27846824/201936269-b0577211-007a-4fad-9cb4-c43cbf354459.PNG"/>

</details>

<details>
<summary> API 명세서 </summary>
  <img src="https://user-images.githubusercontent.com/27846824/202340046-95c1c07f-2548-4350-b297-7c1e173d9001.jpg"/>
  <img src="https://user-images.githubusercontent.com/27846824/202340051-9e281588-5ae2-45ab-891f-c0b83da78499.jpg"/>
  <img src="https://user-images.githubusercontent.com/27846824/202340053-e8afa899-0324-40c8-8249-64e8cb0efba9.jpg"/>
  <img src="https://user-images.githubusercontent.com/27846824/202340057-02a43df8-fcf7-4857-ae35-080c6ac3d335.jpg"/>
  
</details>

<details>
<summary> ERD </summary>
<img src="https://user-images.githubusercontent.com/27846824/201935663-d41af558-046d-4aac-8fef-4dc597815df5.png"/>
</details>

![-----------------------------------------------------](https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/rainbow.png)

### Getting Started

#### Frontend

1. devDependency 패키지를 설치한다

```javascript
npm install -f
(Material ui library가 react 18 버전과 dependency 문제가 있어서 -f 옵션 필요 )
```

2. ./client 폴더로 이동해 프로젝트를 실행한다

```javascript
npm start
```

#### Backend

![-----------------------------------------------------](https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/rainbow.png)

### Licence

MIT
