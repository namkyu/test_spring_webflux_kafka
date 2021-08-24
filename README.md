* Netty 서버가 동작한다.
* 내 로컬 머신에 CPU가 8개 이므로 webflux는 8개의 worker 쓰레드를 사용한다.
  * worker thread default size는 서버의 core 개수로 설정이 되어있다.  
* 어떻게 적은 쓰레드로 고성능의 동시 처리가 가능한가?
    * Event Loop 모델 사용
        * The event loop runs continuously in a single thread, although we can have as many event loops as the number of available cores.
        * The event loop process the events from an event queue sequentially and returns immediately after registering the callback with the platform.
        * The platform can trigger the completion of an operation like a database call or an external service invocation.
        * The event loop can trigger the callback on the operation completion notification and send back the result to the original caller.
        * node.js, nginx, netty 등이 Event Loop 모델을 구현했다.

