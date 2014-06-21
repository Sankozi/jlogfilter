jlogfilter
==========

Application for displaying logs (GUI appender). Currently works only with log4j.

Requirements
------------
Java 7 with jfxrt.jar on classpath,
or Java 8

Configuration - log4j
---------------------

Jlogfilter supports log4j through `SocketHubAppender`. When started it tries to connect to it on localhost:7777. Below are example configurations. 

**log4j.properties**
```
log4j.appender.SERVER=org.apache.log4j.net.SocketHubAppender
log4j.appender.SERVER.port=7777

log4j.rootLogger=TRACE, SERVER
```

**log4j.xml**
```
<log4j:configuration xmlns:log4j="http://jakarta.apache.org/log4j/">
    <appender name="SERVER" class="org.apache.log4j.net.SocketHubAppender">
		    <param name="Port" value="7777"/>
    </appender>
    <root> 
        <priority value="trace"/> 
        <appender-ref ref="SERVER"/> 
    </root>
</log4j:configuration>
```

**in code**
```
Logger rootLogger = Logger.getRootLogger();
rootLogger.setLevel(Level.TRACE);
rootLogger.addAppender(new SocketHubAppender(7777));
```        
