<h1>I Time U</h1>

<h2>1. I Time U 는 어떤 앱인가?</h2>

I Time U는 To do list와 Pomodoro 기법을 합친 시간 관리 어플리케이션이다.

<h2>2. I Time U의 목적</h2>

I Time U는 타이머 애플리케이션으로 사용자가 할 일(Task)을 손쉽게 관리하도록 하고, 사용자의 집중을 도우며, 일에 대한 성취감을 느낄 수 있도록 만든다는 목적을 갖는다. 이는 포모도로 기법[1)](#pomodoro) 을 어플리케이션에 적용함으로써 실현할 수 있다. 

<h2>3. I Time U 지원 버전</h2>
아이스크림 샌드위치(Android 4.0.3/API 15) ~ 오레오(Android 8.0/API 26)

<h2>4. I Time U 사용자 매뉴얼</h2>

## Intro
### (1) I TIME U 최초 실행 시 보여지는 인트로.
![intro_pages](https://user-images.githubusercontent.com/19372511/30512948-62e263ca-9b35-11e7-8ef6-9769dda1a7da.png)
포모도로에 친숙하지 않은 사용자를 위해 핵심적인 개념을 담은 가이드라인을 제공한다.
<br><br>

## List
### (1) Blank list
<img src="https://user-images.githubusercontent.com/19372511/30512970-d1b5e7e0-9b35-11e7-8c66-be6550858fcc.png" width="250"/>
할 일이 추가되지 않은 상태의 빈 리스트이다. 구성은 다음과 같다.

* 달성률: 최상단에 위치해 있으며 화면의 날짜에 해당하는 (달성한 포모도로의 단위 합)/(총 포모도로의 단위 합) 의 백분율과 실제 단위를 보여준다.

* 날짜: 표시되는 날짜에 해당하는 할 일 리스트를 화면에 보여준다. 좌/우 클릭으로 이전 날짜/다음 날짜로 이동할 수 있으며, 가운데 날짜를 클릭 시 달력 다이얼로그가 띄워진다. 날짜 선택 시 해당 날짜의 리스트로 이동할 수 있다.
<img src="https://user-images.githubusercontent.com/19372511/30513027-3139557a-9b37-11e7-9d00-268577158807.png" width="250"/>

* 할 일 추가 버튼: 버튼을 클릭하면 할 일을 추가할 수 있게 된다.

### (2) Add
<img src="https://user-images.githubusercontent.com/19372511/30513056-f46b309a-9b37-11e7-8cfc-8965211afbde.png" width="250" />
할 일을 추가하기 위한 액티비티이다. 구성은 다음과 같다.

* 할 일의 이름: 해야 할 일의 이름을 입력한다. 필수값으로, 누락될 경우 shake 애니메이션이 발생하며 할 일을 추가할 수 없게 된다. 

* 할 일에 대한 세부사항: 해야 할 일의 세부사항을 입력한다. 작성하지 않아도 무방하다.

* 포모도로: 몇 번의 포모도로를 수행할 지 결정한다. 최소 1회, 최대 20회까지 설정할 수 있다.

* 날짜: 할 일을 등록할 날짜를 선택한다. 자유로운 날짜 선택이 가능하다.
<img src="https://user-images.githubusercontent.com/19372511/30513094-8edddcb8-9b38-11e7-97d6-4bfa94d5da0a.png" width="250" />

### (3) Edit
<img src="https://user-images.githubusercontent.com/19372511/30513165-64308104-9b39-11e7-9f00-4969f86e0491.png" width="250" />
할 일/마무리 된 일의 내용을 수정한다. 구성은 (2) Add 와 동일하다.

* 포모도로 추가사항: 이미 완료된 포모도로보다 작은 단위를 선택할 수 없다.

### (4) List
<img src="https://user-images.githubusercontent.com/19372511/30513179-a5e6e8b8-9b39-11e7-9dce-bd8a75e6ed26.png" width="250" />
할 일 / 진행 중인 일 / 완료한 일이 리스트에 나열된다. 상태는 다음과 같다.

* 할 일: (총 포모도로의 수) > (완료한 포모도로의 수) 이면서, 타이머에 등록되어 일을 진행하고 있지 않은 상태이다. 클릭 시 타이머로 이동한다.

* 진행 중인 일: 타이머에 등록되어 일이 진행되고 있는 상태이다. 클릭 시 타이머로 이동한다.

* 완료한 일: (총 포모도로의 수) = (완료한 포모도로의 수) 로 일이 완료된 상태이다. 클릭해도 타이머로 이동하지 않는다. (토스트 메세지 출력)

* 할 일 / 진행 중인 일 / 완료한 일을 길게 클릭할 경우: 해당 일을 수정 / 삭제 할 수 있는 메뉴가 나타난다. 단, 진행 중인 일에서는 수정 / 삭제를 클릭해도 해당 액션을 수행할 수 없다. (토스트 메세지 출력)<br>
<img src="https://user-images.githubusercontent.com/19372511/30513227-e35a342e-9b3a-11e7-8e4e-44aa23e3ee6c.png" width="250" />
<br><br>

## Timer
### (1) 타이머 시작 및 진행
<img src="https://user-images.githubusercontent.com/19359354/30536183-d65d6172-9c9f-11e7-92d2-81e5b34b019e.PNG" width="250"/>

* 리스트에서 클릭된 작업은 타이머에서 그 이름이 표시된다.
* START 버튼을 누르면 타이머가 작동되고, 프로세스 바가 늘어난다.
* STOP 버튼을 누르면 현재 작업은 취소되고, 포모도로가 작업을 시작하지 않았음으로 표시된다. 

<img src="https://user-images.githubusercontent.com/19359354/30536432-c34d69f0-9ca0-11e7-8d9c-ae674da5740b.PNG" width="250"/>

* 작업이 시작되면 상태 알람 줄에 현재 남은 시간과 작업 이름이 표시된다.
* 작업이 완료됐을 때 진동 또는 알람이 울리면서 작업이 완료되었음을 알린다.

### (2) Short break time
<img src="https://user-images.githubusercontent.com/19359354/30536320-535ed03e-9ca0-11e7-992d-8e9693a27878.PNG" width="250" />

* 한 포모도로가 마무리되면 리스트에 완료됐음이 표시되고 휴식 시간이 시작된다.
* 버튼의 작동은 포모도로 세션 때와 동일하다.

### (3) Long break time
<img src="https://user-images.githubusercontent.com/19359354/30536421-b7d0d4d6-9ca0-11e7-925f-206c6314a77e.PNG" width="250"/>

* 포모도로 작업이 설정된 세션 반복 수만큼 도달하면 긴 쉬는 시간이 시작된다. 
<br><br>

## Statistics
### (1) Week
<img src="https://user-images.githubusercontent.com/19372511/30513263-6d67e684-9b3b-11e7-8d81-0f6dfb6224f5.png" width="250" />
오늘 날짜를 포함한 이전 일주일의 기록을 그래프로 나타낸다. 그래프의 데이터는 해당 날짜 별 `목표한 총 포모도로의 합`과 `완료한 총 포모도로의 합`으로 구성된다.

### (2) Month
<img src="https://user-images.githubusercontent.com/19372511/30513283-b27007de-9b3b-11e7-9741-e2469735b6e5.png" width="250" />
오늘 날짜를 포함한 이전 한 달의 기록을 그래프로 나타낸다. 데이터 구성은 (1) Week 와 동일하다.

### (3) Custom
#### Blank graph
<img src="https://user-images.githubusercontent.com/19372511/30513289-e2ae6422-9b3b-11e7-8b34-ccabdc9de099.png" width="250" />

커스텀 통계는 사용자가 시작 날짜와 끝 날짜를 선택해야 그래프가 활성화된다.

<img src="https://user-images.githubusercontent.com/19372511/30513306-11a7a040-9b3c-11e7-925e-bc41800ad0ca.png" width="250" /> <img src="https://user-images.githubusercontent.com/19372511/30513311-1c8b63d4-9b3c-11e7-8d38-a6f480afff97.png" width="250" />

시작 날짜를 달력 다이얼로그에서 선택하고, 마찬가지로 끝 날짜를 선택하면 그래프가 생성된다. 단, 시작 날짜와 끝 날짜의 차이는 1일 이상이어야 한다. (1일 보다 작을 시 토스트 메세지 출력)

<img src="https://user-images.githubusercontent.com/19372511/30513331-4898a1da-9b3c-11e7-94a4-493633616d19.png" width="250" />
<br><br>

## Setting
<img src="https://user-images.githubusercontent.com/19471816/30535325-efe0d442-9c9c-11e7-812f-73020236eefc.PNG" width="250" />

### (1) general: 각 타이머에 대한 전반적 설정이다.
* work session duration: 작업 시간의 설정이다.
* break duration: 휴식 시간의 설정이다.
* long break duration: 설정해둔 수만큼의 세션이 지났을 때 갖는 긴 휴식 시간의 설정이다.
* work sessions before a long break: 긴 휴식 시간을 갖기까지 보내는 세션 수의 설정이다.

### (2) when a session is over: 타이머가 끝났을 때 알림 방식을 설정한다.
* play sound when session ends: 폰 설정에서 소리가 켜져 있을 경우 소리 알림을 보낸다.
* vibrate when session ends: 진동 알림을 보낸다.
<br><br>

## About
<img src="https://user-images.githubusercontent.com/19372511/30513339-7f26da00-9b3c-11e7-9ffb-870fe12c4ecb.png" width="250" />

I Time U 의 정보를 보여준다. 구성은 다음과 같다.

* Version: 앱의 버전을 나타낸다.

* Open licenses: 앱에 사용된 오픈 소스 라이브러리와, 상업적으로 사용가능한 이미지의 라이센스를 보여준다.
<img src="https://user-images.githubusercontent.com/19372511/30513354-d6dcd056-9b3c-11e7-949e-4bc5222c1e1c.png" width="250" />

* Feedback: 앱에 대한 피드백을 줄 수 있도록 메일을 연결한다.
<img src="https://user-images.githubusercontent.com/19372511/30513363-f4febb08-9b3c-11e7-85f5-ad3f812750b3.png" width="250" />

* Support language: 앱이 지원하는 언어를 보여준다. 휴대폰 단말의 설정 언어에 따라 영어와 한국어를 지원한다.
<img src="https://user-images.githubusercontent.com/19372511/30513372-121c8846-9b3d-11e7-942f-7e7473046885.png" width="250" />

<h4>1) 포모도로 기법이란?</h4>

<a id="pomodoro"></a>

포모도로 기법(pomodoro technique)은 시간 관리 중에서도 작업 우선순위가 아닌 시간 우선순위에 초점을 둔 테크닉이다. 짧은 작업과 짧은 휴식을 계속 반복하기 때문에 가볍게 사용할 수 있고, 직장인, 학생 등 누구나 쉽게 활용할 수 있다. 기법의 적용은 다음과 같이 이루어진다. 25분(default value) 단위로 일을 처리하고, 일 처리 직후에 짧은 쉬는시간(short break time) 5분(default value)과 4번의 일 처리 후에 긴 쉬는시간(long break time) 25분을 제공한다

일반적인 시간 관리법인 to-do list나 프랭클린 플래너 등은 시간보다 할 일 자체에 무게를 두는데, 그 일을 시작하고 집중하게 할 만한 동기와 압력은 제공하지 못한다. 그러므로 이러한 기법들에 시간을 우선으로 하는 포모도로를 합하면, 전체적인 할 일은 리스트로 관리하되 개별의 일들은 포모도로를 통해 완료하도록 할 수 있으며, 이는 결과적으로 생산성 향상으로 이어진다.
